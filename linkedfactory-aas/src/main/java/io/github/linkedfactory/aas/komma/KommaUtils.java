package io.github.linkedfactory.aas.komma;

import io.adminshell.aas.v3.model.LangString;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.enilink.komma.core.KommaModule;

public class KommaUtils {
	public static final String aasNamespace = "https://admin-shell.io/aas/3/0/RC01/";

	public static KommaModule createModule() {
		KommaModule module = new KommaModule(KommaUtils.class.getClassLoader());
		AasPropertyMapper propertyMapper = new AasPropertyMapper();
		try (ScanResult scanResult = new ClassGraph()
				.acceptPackagesNonRecursive("io.adminshell.aas.v3.model")
				.scan()) {
			for (ClassInfo classInfo : scanResult.getAllClasses()) {
				if (classInfo.isEnum()) {
					Class<?> enumClass = classInfo.loadClass();
					module.addLiteralMapper(classInfo.getName(), new EnumLiteralMapper((Class<? extends Enum>) enumClass));
				} else if (classInfo.isInterface()) {
					Class<?> entityClass = classInfo.loadClass();
					module.addConcept(entityClass, aasNamespace + classInfo.getSimpleName());
					module.addPropertyMapper(entityClass, propertyMapper);
				} else {
					// ignore classes like LangString etc.
					// System.out.println("class: " + classInfo.getName());
				}
			}
		}
		module.addLiteralMapper(LangString.class, new LangStringLiteralMapper());
		return module;
	}
}
