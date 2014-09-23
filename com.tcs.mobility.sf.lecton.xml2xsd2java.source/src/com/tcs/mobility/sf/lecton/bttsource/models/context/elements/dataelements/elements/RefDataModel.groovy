package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.elements

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel


class RefDataModel extends DataElementModel{

	String refId
	DataElementModel model

	public RefDataModel(String refId) {
		this.refId = refId
		this.type = "REFDATA"
	}

	public RefDataModel(def refId, def parent) {
		this(refId)
		this.parent = parent
	}

	def addModel(def model){
		this.model = model
	}
	
	@Override
	public List getChildren() {
		def list = []
		if(model){
			list.add(model)
			return list
		}else{
			return null
		}
	}

	@Override
	public String getJavaFieldStatement() {
		// This is not used for Data Model Jar creation. Hence not used.
		return null
	}
	
	def obtainAttributes(){
		Set props = []
		super.obtainAttributes(props)
	}
}
