package io.github.linkedfactory.aas.edit;

import net.enilink.composition.annotations.Iri;
import net.enilink.composition.traits.Behaviour;
import net.enilink.komma.core.INamespace;
import net.enilink.komma.core.KommaException;
import net.enilink.komma.dm.IDataManager;
import net.enilink.komma.model.IModel;
import net.enilink.komma.model.IModelSet;
import net.enilink.komma.model.ModelUtil;
import net.enilink.komma.model.concepts.Model;
import org.eclipse.core.runtime.content.IContentDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	public void load(final InputStream in, final Map<?, ?> options)
			throws IOException {
		final List<INamespace> namespaces = new ArrayList<>();
		final IDataManager dm = ((IModelSet.Internal) getModelSet())
				.getDataManagerFactory().get();
		getModelSet().getDataChangeSupport().setEnabled(dm, false);
		try {
			setModelLoading(true);
			if (in != null && in.available() > 0) {
				dm.getTransaction().begin();

				dm.getTransaction().commit();
			}
		} catch (Throwable e) {
			if (e instanceof KommaException) {
				throw (KommaException) e;
			}
			throw new KommaException("Unable to load model", e);
		} finally {
			setModelLoading(false);
			setModified(false);

			if (dm.getTransaction().isActive()) {
				dm.getTransaction().rollback();
			}
			dm.close();
		}
		setModelLoaded(true);
	}

	@Override
	public void save(OutputStream os, Map<?, ?> options) throws IOException {
	}
}