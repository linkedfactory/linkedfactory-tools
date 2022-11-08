package io.github.linkedfactory.aas.komma;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.adminshell.aas.v3.model.*;
import io.adminshell.aas.v3.model.impl.*;
import junit.framework.AssertionFailedError;
import net.enilink.composition.properties.traits.Refreshable;
import net.enilink.komma.core.IEntity;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.em.ManagerCompositionModule;
import net.enilink.komma.em.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RdfMapperTest extends BaseMapperTest {

	static void assertEquals(Collection<?> expected, Set<?> actual) {
		Set<?> actualCopy = new HashSet<>(actual);
		actualCopy.removeAll(expected);
		if (!actualCopy.isEmpty()) {
			throw new AssertionFailedError("Set contains unexpected elements: " + actualCopy);
		}
		Set<?> expectedCopy = new HashSet<>(expected);
		expectedCopy.removeAll(actual);
		if (!expectedCopy.isEmpty()) {
			throw new AssertionFailedError("Set is missing expected elements: " + expectedCopy);
		}
	}

	@Override
	protected KommaModule createModule() throws Exception {
		return KommaUtils.createModule();
	}

	AssetAdministrationShellEnvironment createAasEnv() {
		AssetAdministrationShell aas = new DefaultAssetAdministrationShell.Builder()
				.assetInformation(new DefaultAssetInformation.Builder()
						.assetKind(AssetKind.INSTANCE)
						.build())
				.idShort("aas1")
				.description(new LangString("This is a test AAS"))
				.displayName(new LangString("Display Name 1", "en"))
				.displayName(new LangString("Anzeigename 2@de"))
				.build();
		Submodel submodel = new DefaultSubmodel.Builder()
				.idShort("submodel1")
				.description(new LangString("Submodel 1"))
				.displayNames(new ArrayList<>(
						Arrays.asList(
								new LangString("First Submodel Element name"),
								new LangString("Second Submodel Element name"))))
				.category("Example category")
				.embeddedDataSpecification(new DefaultEmbeddedDataSpecification.Builder()
						.dataSpecification(new DefaultReference.Builder()
								.key(new DefaultKey.Builder()
										.idType(KeyType.IRI).value("https://example.org")
										.build())
								.build())
						.dataSpecificationContent(new DefaultDataSpecificationIEC61360.Builder()
								.dataType(DataTypeIEC61360.RATIONAL)
								.build())
						.build())
				.build();
		Submodel submodel2 = new DefaultSubmodel.Builder()
				.idShort("submodel2")
				.description(new LangString("Submodel 2"))
				.displayNames(new ArrayList<>(
						Arrays.asList(
								new LangString("First Submodel Element name"),
								new LangString("Second Submodel Element name"))))
				.category("Example category")
				.build();
		List<AssetAdministrationShell> aasList = new ArrayList<>(Collections.singletonList(aas));
		AssetAdministrationShellEnvironment aasEnv = new DefaultAssetAdministrationShellEnvironment.Builder()
				.assetAdministrationShells(aasList)
				.submodels(Arrays.asList(submodel, submodel2))
				.build();
		return aasEnv;
	}

	@Test
	public void testMapper() throws IOException {
		// convert to RDF data
		long start = System.currentTimeMillis();
		manager.merge(createAasEnv());
		System.out.println("conversion time (IEntityManager.merge): " + (System.currentTimeMillis() - start));
		// delete data
		manager.clear();

		start = System.currentTimeMillis();
		// use serializer to convert AAS to RDF
		Injector injector = Guice.createInjector(
				new ManagerCompositionModule(manager.getFactory().getModule()), new AbstractModule() {
					@Override
					protected void configure() {
						bind(Locale.class).toInstance(manager.getLocale());
					}
				});
		Serializer serializer = injector.getInstance(Serializer.class);
		serializer.serialize(createAasEnv(), stmt -> {
			manager.add(stmt);
		});

		System.out.println("conversion time (Serializer): " + (System.currentTimeMillis() - start));

		manager.match(null, null, null).forEach(System.out::println);

		// retrieve AAS environment from RDF store
		manager.findAll(AssetAdministrationShellEnvironment.class).forEach(env -> {
			assertEquals(Arrays.asList("submodel1", "submodel2"), env.getSubmodels().stream()
					.map(m -> m.getIdShort()).collect(Collectors.toSet()));
		});

		// retrieve submodel via SPARQL
		Submodel s1 = manager.createQuery("select ?submodel {" +
						"  ?submodel <https://admin-shell.io/aas/3/0/RC01/Referable/idShort> ?idShort " +
						" }").setParameter("idShort", "submodel1").evaluate(Submodel.class)
				.toList().get(0);
		Assert.assertEquals("submodel1", s1.getIdShort());

		// remove complete submodel
		manager.removeRecursive(s1, true);

		System.out.println("statements after removal:");
		manager.match(null, null, null).forEach(System.out::println);

		manager.findAll(AssetAdministrationShellEnvironment.class).forEach(env -> {
			// note that submodels property needs to be refreshed
			assertEquals(Arrays.asList("submodel2"), refresh(env.getSubmodels()).stream()
					.map(m -> m.getIdShort()).collect(Collectors.toSet()));
		});
	}
}
