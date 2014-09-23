package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.elements.RefDataModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel
import com.tcs.mobility.sf.lecton.bttsource.utils.mappers.SimpleMappers


/**
 * Model class to represent IndexedElement in BTT
 * 
 * @author Saravana
 *
 */
class IndexedCollectionModel extends DataElementModel {

	// IColl can contain either a kColl or a field
	private KeyedCollectionModel kColl
	private FieldModel field
	def RefDataModel refData

	public IndexedCollectionModel(def id) {
		this.id = id
		this.type = "ICOLL"
	}

	public IndexedCollectionModel(def id, def parent) {
		this(id)
		this.parent = parent
	}

	def addModel(def model){
		if(model instanceof KeyedCollectionModel){
			kColl = model
		}else if(model instanceof FieldModel){
			field = model
		}else if(model instanceof RefDataModel){
			refData = model
		}
	}

	def addElement(def element){
		if(element instanceof KeyedCollectionModel){
			kColl = element
		}else if(element instanceof FieldModel){
			field = element
		}
	}

	/**
	 * Returns the element within the iColl
	 * 
	 * @return data element contained in the iColl
	 */
	def getElement(){
		if(kColl){
			return kColl
		}else if(field){
			return field
		}
	}


	@Override
	public List getChildren() {
		def list = []
		if(kColl){
			list.add(kColl)
		}else if(field){
			list.add(field)
		}
		return list
	}

	@Override
	public String getJavaFieldStatement() {
		def iCollChild = getElement()
		SimpleMappers mapper = new SimpleMappers()
		if(iCollChild instanceof KeyedCollectionModel){
			return "private List<${iCollChild.getDataModelAnnotationName()}> ${getDataModelObjectName()}"
		}else if(iCollChild instanceof FieldModel){
			return "private List<${iCollChild.dataType ? mapper.getWrapper(iCollChild.dataType) : 'String'}> ${getDataModelObjectName()}"
		}
	}

	def obtainAttributes(){
		Set props = [
			'kColl',
			'field',
			'refData',
			'element'
		]
		super.obtainAttributes(props)
	}
}
