package io.github.linkedfactory.aas.komma;

import io.adminshell.aas.v3.model.annotations.IRI;
import net.enilink.composition.mapping.PropertyAttribute;
import net.enilink.composition.mapping.PropertyDescriptor;
import net.enilink.composition.properties.mapper.AbstractPropertyMapper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Custom mapper for AAS Java model that interprets the {@link IRI} annotations on getters.
 */
public class AasPropertyMapper extends AbstractPropertyMapper {

	@Override
	protected String getPredicate(Method method) {
		IRI iri = method.getAnnotation(IRI.class);
		return iri.value()[0];
	}

	@Override
	protected boolean isMappedGetter(Method method) {
		return method.isAnnotationPresent(IRI.class);
	}

	@Override
	protected List<PropertyAttribute> getAttributes(Method method) {
		return Collections.emptyList();
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor(Method readMethod) {
		PropertyDescriptor pd = super.createPropertyDescriptor(readMethod);
		if (List.class.isAssignableFrom(readMethod.getReturnType())) {
			pd.setEnforceList(true);
		}
		return pd;
	}
}
