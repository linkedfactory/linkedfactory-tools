package io.github.linkedfactory.aas.edit;

import net.enilink.komma.core.URI;
import net.enilink.komma.core.URIs;
import net.enilink.komma.edit.provider.ReflectiveItemProviderAdapterFactory;
import net.enilink.komma.em.concepts.IClass;

import java.util.Collection;

public class AasAdapterFactory extends ReflectiveItemProviderAdapterFactory {
	public final static String NAMESPACE = "http://komma.github.io/komma-examples/vocab/smarthome.ttl#";
	public final static URI NAMESPACE_URI = URIs.createURI(NAMESPACE);

	public AasAdapterFactory() {
		super(AasEditPlugin.INSTANCE, NAMESPACE_URI);
	}

	@Override
	protected Object createItemProvider(Object object, Collection<IClass> types, Object providerType) {
		/*if (object instanceof Switch) {
			return new AasItemProvider(this, resourceLocator, types) {
				@Override
				public String getText(Object object) {
					return ((Switch) object).label();
				}
			};
		}
		if (!(object instanceof IClass || object instanceof IProperty)) {
			return new AasItemProvider(this, resourceLocator, types);
		}*/
		return null;
	}
}