package io.github.linkedfactory.aas.komma;

import io.adminshell.aas.v3.model.*;
import io.adminshell.aas.v3.model.impl.*;
import net.enilink.komma.core.KommaModule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RdfMapperTest extends BaseMapperTest {

	@Override
	protected KommaModule createModule() throws Exception {
		return KommaUtils.createModule();
	}

	AssetAdministrationShellEnvironment createAasEnv() {
		AssetAdministrationShell aas = new DefaultAssetAdministrationShell.Builder()
				.assetInformation(new DefaultAssetInformation.Builder()
						.assetKind(AssetKind.INSTANCE)
						.build())
				.description(new LangString("This is a test AAS"))
				.displayName(new LangString("Display Name 1", "en"))
				.displayName(new LangString("Anzeigename 2@de"))
				.build();
		Submodel submodel = new DefaultSubmodel.Builder()
				.description(new LangString("My Submodel"))
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
		List<AssetAdministrationShell> aasList = new ArrayList<>(Collections.singletonList(aas));
		AssetAdministrationShellEnvironment aasEnv = new DefaultAssetAdministrationShellEnvironment.Builder()
				.assetAdministrationShells(aasList)
				.submodels(submodel)
				.build();
		return aasEnv;
	}

	@Test
	public void testMapper() throws IOException {
		// convert to RDF data
		long start = System.currentTimeMillis();
		manager.merge(createAasEnv());
		System.out.println("conversion time: " + (System.currentTimeMillis() - start));
		// delete data
		manager.clear();

		start = System.currentTimeMillis();
		manager.merge(createAasEnv());
		System.out.println("conversion time (warm): " + (System.currentTimeMillis() - start));

		manager.match(null, null, null).forEach(stmt -> {
			System.out.println(stmt);
		});

		// retrieve AAS environment from RDF store
		manager.findAll(AssetAdministrationShellEnvironment.class).forEach(env -> {
			env.getSubmodels().forEach(model -> {
				System.out.println(model.getDescriptions());
			});
		});
	}
}
