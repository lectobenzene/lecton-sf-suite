package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.contentprovider;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.FieldModel;
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.IndexedCollectionModel;
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel;
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel;

public class ContextContentProvider extends ArrayContentProvider implements
		ITreeContentProvider {
	
	public Object[] getChildren(Object parentElement) {
		ArrayList<DataElementModel> children = new ArrayList<DataElementModel>();
		if(parentElement instanceof KeyedCollectionModel){
			KeyedCollectionModel kColl = (KeyedCollectionModel)parentElement;
			return kColl.getChildren().toArray();
		}else if(parentElement instanceof IndexedCollectionModel){
			// IColl has a single child. Hence adding it to a list and returning an array
			IndexedCollectionModel iColl = (IndexedCollectionModel)parentElement;
			children.add((DataElementModel)iColl.getElement());
		}else if(parentElement instanceof FieldModel){
			// do nothing, no children
		}
		return children.toArray();
	}

	public Object getParent(Object element) {
		if(element instanceof DataElementModel){
			return ((DataElementModel)element).getParent();
		}else{
			return null;
		}

	}

	public boolean hasChildren(Object element) {
		if(element instanceof DataElementModel){
			DataElementModel elementModel = (DataElementModel)element;
			return (elementModel.getChildren() != null && elementModel.getChildren().size() >=0);
		}
		return false;
	}
}
