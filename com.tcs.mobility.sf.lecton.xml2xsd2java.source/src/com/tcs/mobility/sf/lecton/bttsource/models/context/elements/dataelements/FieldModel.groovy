package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel


/**
 * Model to represent DataField in BTT
 * 
 * @author Saravana
 *
 */
class FieldModel extends DataElementModel{

	/** The Value of the Field. This represents the 'value' (initial value) in the
	 * case of Context parsing from the service xml.
	 */
	String value

	/**
	 * Represents the DataType in the case
	 * of Data Model creation. Defaults to String dataType
	 */
	String dataType = 'String'

	public FieldModel(def id){
		this.id = id
		this.type = "FIELD"
	}

	public FieldModel(def id, def parent) {
		this(id)
		this.parent = parent
	}

	@Override
	public List getChildren() {
		// Field doesn't not contain any children
		return null
	}

	@Override
	public String getJavaFieldStatement() {
		return "private ${dataType ? dataType : 'String'} ${getDataModelObjectName()}"
	}
	
	def obtainAttributes(){
		Set props = ['dataType']
		super.obtainAttributes(props)
	}
}
