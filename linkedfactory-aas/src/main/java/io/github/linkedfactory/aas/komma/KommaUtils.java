package io.github.linkedfactory.aas.komma;

import io.adminshell.aas.v3.dataformat.core.ReflectionHelper;
import io.adminshell.aas.v3.model.LangString;
import net.enilink.komma.core.KommaModule;

import java.util.stream.Stream;

public class KommaUtils {
	public static final String aasNamespace = "https://admin-shell.io/aas/3/0/RC01/";

	public static KommaModule createModule() {
		KommaModule module = new KommaModule(KommaUtils.class.getClassLoader());
		AasPropertyMapper propertyMapper = new AasPropertyMapper();
		ReflectionHelper.ENUMS.forEach(enumClass -> {
			module.addLiteralMapper(enumClass.getName(), new EnumLiteralMapper(enumClass));
		});
		Stream.concat(ReflectionHelper.INTERFACES.stream(),
				ReflectionHelper.INTERFACES_WITHOUT_DEFAULT_IMPLEMENTATION.stream()).forEach(entityClass -> {
			module.addConcept(entityClass, aasNamespace + entityClass.getSimpleName());
			module.addPropertyMapper(entityClass, propertyMapper);
		});
		module.addLiteralMapper(LangString.class, new LangStringLiteralMapper());
		return module;
	}
}
