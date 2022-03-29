package io.github.linkedfactory.aas.edit;

import net.enilink.komma.common.AbstractKommaPlugin;
import net.enilink.komma.common.util.IResourceLocator;
import net.enilink.komma.edit.KommaEditPlugin;

public class AasEditPlugin extends AbstractKommaPlugin {
	/**
	 * The singleton instance of the plugin.
	 */
	public static final AasEditPlugin INSTANCE = new AasEditPlugin();

	AasEditPlugin() {
		super(new IResourceLocator[] { KommaEditPlugin.INSTANCE });
	}

	@Override
	public IResourceLocator getBundleResourceLocator() {
		return plugin;
	}

	@Override
	public Object getImage(String key) {
		if (!key.contains(".")) {
			key += ".png";
		}
		return super.getImage(key);
	}

	static Implementation plugin;

	/**
	 * The implementation of an Eclipse <b>plugin</b>.
	 */
	public static class Implementation extends EclipsePlugin {
		public Implementation() {
			// Remember the static instance.
			plugin = this;
		}
	}
}