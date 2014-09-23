package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.elements.RefDataModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel


/**
 * Model class to represent KeyedElement in BTT
 * @author Saravana
 *
 */
class KeyedCollectionModel extends DataElementModel {

	// kColl can contain any number of iColl, kColl or fields
	def keyedCollectionModels
	def indexedCollectionModels
	def fieldModels
	def refDataModels
	
	public KeyedCollectionModel(String id){
		this.id = id
		this.type = "KCOLL"
		indexedCollectionModels = []
		keyedCollectionModels = []
		fieldModels = []
		refDataModels = []
	}

	public KeyedCollectionModel(def id, def parent) {
		this(id)
		this.parent = parent
	}

	def void addModel(DataElementModel model){
		
		if(model){
			println "MODEL ID = ${model.id} \\ ${model.refId}"
		}else{
			println "MODEL ADDED IS NULL"
		}
		
		if(model instanceof KeyedCollectionModel){
			addKColl(model)
		}else if(model instanceof IndexedCollectionModel){
			addIColl(model)
		}else if(model instanceof FieldModel){
			addField(model)
		}else if(model instanceof RefDataModel){
			addRefData(model)
		}else{
			
		}
	}
	
	/**
	 * Adds an refData as a child
	 * @param refData the refData element inside collection dataType
	 */
	def void addRefData(def refData){
		refDataModels.add(refData)
	}
	
	/**
	 * Adds an iColl as a child
	 * @param iColl an indexedCollection
	 */
	def void addIColl(def iColl){
		indexedCollectionModels.add(iColl)
	}

	/**
	 * Adds another kColl as a child
	 * 
	 * @param kColl a keyedCollection
	 */
	def void addKColl(def kColl){
		keyedCollectionModels.add(kColl)
	}

	/**
	 * Removes all the kColl entries with the respective id and returns a copy of the
	 * kColl that is removed
	 * 
	 * @param id The id of the kColl
	 * @return the kColl that is removed
	 */
	def removeKColl(def id){
		KeyedCollectionModel kColl = getKColl(id)
		for (Iterator<?> iter = keyedCollectionModels.iterator(); iter.hasNext(); ){
			if (id.equals(iter.next().id))
				iter.remove();
		}
		return kColl
	}

	/**
	 * Obtain the child kColl corresponding to the id
	 * @param iD the Id of the kColl
	 * @return the matching child kColl
	 */
	def getKColl(def iD){
		for(keyedCollectionModel in keyedCollectionModels){
			if(keyedCollectionModel.id.equals(iD)){
				return keyedCollectionModel
			}
		}
	}

	/**
	 * Sets new parent for the kColl
	 * 
	 * @param parent parent DataElement
	 */
	def void changeParent(def parent){
		this.parent = parent
	}

	/**
	 * Adds a field as a child
	 * 
	 * @param field the child field
	 */
	def void addField(def field){
		fieldModels.add(field)
	}

	@Override
	public List getChildren() {
		def child = []
		child.add(fieldModels)
		child.add(keyedCollectionModels)
		child.add(indexedCollectionModels)
		// Not checked for Data Model Jar creation
		child.add(refDataModels)		
		return child.flatten()
	}

	@Override
	public String getJavaFieldStatement() {
		return "private ${getDataModelAnnotationName()} ${getDataModelObjectName()}"
	}
	
	def obtainAttributes(){
		Set props = ['keyedCollectionModels','indexedCollectionModels','fieldModels','refDataModels']
		super.obtainAttributes(props)
	}
}
