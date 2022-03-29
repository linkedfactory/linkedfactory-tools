package io.github.linkedfactory.aas.edit;

import net.enilink.komma.core.URIs;
import net.enilink.komma.model.IModel;
import net.enilink.komma.model.IModelSet;
import net.enilink.komma.model.MODELS;
import net.enilink.komma.core.URI;

public class AasModelFactory implements IModel.Factory {
	@Override
	public IModel createModel(IModelSet modelSet, URI uri) {
		IModel model = (IModel) modelSet.getMetaDataManager().createNamed(uri,
				URIs.createURI("aas:AasModel"),
				MODELS.TYPE_MODEL);
		return model;
	}
}