package io.github.linkedfactory.aas.edit;

import io.adminshell.aas.v3.model.Referable;
import net.enilink.komma.common.adapter.IAdapterFactory;
import net.enilink.komma.common.util.IResourceLocator;
import net.enilink.komma.core.IEntity;
import net.enilink.komma.core.IReference;
import net.enilink.komma.edit.provider.IViewerNotification;
import net.enilink.komma.edit.provider.ReflectiveItemProvider;
import net.enilink.komma.edit.provider.ViewerNotification;
import net.enilink.komma.em.concepts.IResource;
import net.enilink.komma.model.event.IStatementNotification;
import net.enilink.vocab.rdfs.Class;

import java.util.Collection;

public class AasItemProvider extends ReflectiveItemProvider {
	public AasItemProvider(IAdapterFactory adapterFactory, IResourceLocator resourceLocator,
	                       Collection<? extends IReference> targetTypes) {
		super(adapterFactory, resourceLocator, targetTypes);
	}

	@Override
	protected void addViewerNotifications(Collection<IViewerNotification> viewerNotifications,
			IStatementNotification notification) {
		IEntity object = resolveReference(notification.getSubject());
		if (object instanceof IResource) {
			((IResource) object).refresh(notification.getPredicate());
			viewerNotifications.add(new ViewerNotification(object, true, true));
		}
	}

	@Override
	protected boolean childRequiresName(IResource subject, IReference property, Class rangeClass) {
		return true;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof Referable) {
			String idShort = ((Referable)object).getIdShort();
			if (idShort != null) {
				return idShort;
			}
		}
		return super.getText(object);
	}

	@Override
	public Object getImage(Object object) {
		/*if (object instanceof Switchable) {
			boolean off = !((Switchable) object).on();
			String prefix = "full/obj16/";
			Collection<?> types = getTypes(object);
			if (object instanceof Switch) {
				return getImage(prefix + "Switch" + (off ? "-off" : ""));
			} else if (types.contains(SMARTHOME.TYPE_LAMP)) {
				return getImage(prefix + "Lamp" + (off ? "-off" : ""));
			}
		}*/
		return super.getImage(object);
	}
}