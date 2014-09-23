package com.tcs.mobility.sf.lecton.bttsource.models.processor

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel
import com.tcs.mobility.sf.lecton.bttsource.models.utils.WatchableList


class ProcessorModel extends SuperParentModel{

	public String id
	public String refFlow
	public String operationContext
	public String implClass
	public WatchableList refFormats = []

	public List getRefFormats() {
		return refFormats;
	}

	public void setRefFormats(List refFormats) {
		notifyListeners(this,
				'refFormats',
				this.refFormats,
				this.refFormats = refFormats)
	}
	
	public String getRefFlow() {
		return refFlow
	}

	public void setRefFlow(String refFlow) {
		notifyListeners(this,
				'refFlow',
				this.refFlow,
				this.refFlow = refFlow)
	}

	public String getOperationContext() {
		return operationContext
	}

	public void setOperationContext(String operationContext) {
		notifyListeners(this,
				'operationContext',
				this.operationContext,
				this.operationContext = operationContext)
	}

	public String getImplClass() {
		return implClass
	}

	public void setImplClass(String implClass) {
		notifyListeners(this,
				'implClass',
				this.implClass,
				this.implClass = implClass)
	}

	public String getId() {
		return id
	}

	public void setId(String id) {
		notifyListeners(this,
				'id',
				this.id,
				this.id = id)
	}



	def addRefFormat(def refFormat){
		refFormats.add(refFormat)
	}
	
	def removeRefFormat(def refFormat){
		refFormats.remove(refFormat)
	}

	def obtainAttributes(){
		def map = [:]
		this.properties.each{ key, value ->
			if(key in [
				'class',
				'metaclass',
				'listener',
				'comments',
				'refFormats',
				'nodes',
				'dynamicProperties'
			]){
				return
			}
			map.put(key, value)
		}
		this.dynamicProperties.each{ key, value ->
			map.put(key, value)
		}
		return map
	}
}
