package io.github.linkedfactory.aas.edit;

import net.enilink.komma.core.KommaModule;

public class AasModelModule extends KommaModule {
	{
		addBehaviour(AasModelSupport.class);
	}
}