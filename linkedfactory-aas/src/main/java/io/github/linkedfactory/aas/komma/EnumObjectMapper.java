package io.github.linkedfactory.aas.komma;

import io.adminshell.aas.v3.model.annotations.IRI;
import net.enilink.komma.core.*;
import net.enilink.vocab.xmlschema.XMLSCHEMA;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

/**
 * Maps enums to string literals.
 */
public class EnumObjectMapper implements IObjectMapper {
	final Class<? extends Enum> enumClass;

	EnumObjectMapper(Class<? extends Enum> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public IReference getReference(Object o) {
		try {
			for (Annotation annotation : o.getClass().getField(((Enum) o).name()).getAnnotations()) {
				if (annotation instanceof IRI) {
					// use value of AAS IRI annotation
					return URIs.createURI(((IRI) annotation).value()[0]);
				}
			}
		} catch (NoSuchFieldException nsfe) {
			// ignore
		}
		// use a standard value that combines enum class name and value name
		return URIs.createURI(KommaUtils.aasNamespace + enumClass.getSimpleName() + "/" + ((Enum<?>) o).name());
	}

	@Override
	public Object readObject(IReference reference, IStatementSource source) {
		return reference;
	}

	@Override
	public void writeObject(Object o, Consumer<IStatement> consumer) {
	}
}
