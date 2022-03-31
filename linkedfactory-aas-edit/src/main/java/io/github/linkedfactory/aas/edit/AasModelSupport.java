package io.github.linkedfactory.aas.edit;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.adminshell.aas.v3.dataformat.aasx.AASXDeserializer;
import io.adminshell.aas.v3.dataformat.xml.XmlDeserializer;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.github.linkedfactory.aas.komma.KommaUtils;
import net.enilink.composition.annotations.Iri;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.*;
import net.enilink.komma.em.ManagerCompositionModule;
import net.enilink.komma.em.Serializer;
import net.enilink.komma.model.IModel;
import net.enilink.komma.model.ModelUtil;
import net.enilink.komma.model.concepts.Model;
import org.eclipse.core.runtime.content.IContentDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Iri("aas:AasModel")
public abstract class AasModelSupport implements IModel.Internal,
		Model, Behaviour<IModel> {

	private IContentDescription determineContentDescription(Map<?, ?> options)
			throws IOException {
		if (options == null) {
			options = Collections.emptyMap();
		}
		IContentDescription contentDescription = (IContentDescription) options
				.get(IModel.OPTION_CONTENT_DESCRIPTION);
		if (contentDescription == null) {
			contentDescription = ModelUtil.determineContentDescription(
					getURI(), getModelSet().getURIConverter(), options);
		}
		return contentDescription;
	}

	@Override
	public void load(final InputStream in, final Map<?, ?> options) throws IOException {
		addImport(URIs.createURI("https://admin-shell.io/aas/3/0/RC01/"), "aas");
		getModelSet().getModule().includeModule(KommaUtils.createModule());
		// ensure that manager is re-created with additional module
		unloadManager();

		ITransaction tx = getManager().getTransaction();
		boolean changeSupportEnabled = getModelSet().getDataChangeSupport().isEnabled(null);
		if (changeSupportEnabled) {
			getModelSet().getDataChangeSupport().setEnabled(null, false);
		}
		try {
			setModelLoading(true);
			if (in != null && in.available() > 0) {
				tx.begin();

				XmlFactory xmlFactory = new XmlFactory(new WstxInputFactory(), new WstxOutputFactory());
				XmlDeserializer xmlDeserializer = new XmlDeserializer(xmlFactory);
				AASXDeserializer deserializer = new AASXDeserializer(xmlDeserializer, in);
				AssetAdministrationShellEnvironment aasEnv = deserializer.read();

				// use serializer to convert AAS to RDF
				Injector injector = Guice.createInjector(
						new ManagerCompositionModule(getManager().getFactory().getModule()), new AbstractModule() {
							@Override
							protected void configure() {
								bind(Locale.class).toInstance(getManager().getLocale());
							}
						});
				Serializer serializer = injector.getInstance(Serializer.class);
				IEntityManager em = getManager();
				serializer.serialize(aasEnv, stmt -> {
					em.add(stmt);
				});

				// alternative to using the serializer but slower
				// getManager().merge(aasEnv);

				tx.commit();
			}
		} catch (Throwable e) {
			if (e instanceof KommaException) {
				throw (KommaException) e;
			}
			throw new KommaException("Unable to load model", e);
		} finally {
			if (changeSupportEnabled) {
				getModelSet().getDataChangeSupport().setEnabled(null, true);
			}
			setModelLoading(false);
			setModified(false);

			if (tx.isActive()) {
				tx.rollback();
			}
		}
		setModelLoaded(true);
	}

	@Override
	public void save(OutputStream os, Map<?, ?> options) throws IOException {

	}
}