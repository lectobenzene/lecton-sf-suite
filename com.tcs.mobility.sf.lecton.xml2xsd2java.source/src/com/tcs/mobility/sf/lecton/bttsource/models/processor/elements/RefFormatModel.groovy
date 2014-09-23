package com.tcs.mobility.sf.lecton.bttsource.models.processor.elements

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel


class RefFormatModel extends SuperParentModel {


	public String name
	public String refId

	public String getName() {
		return name
	}

	public void setName(String name) {
		notifyListeners(this,
				'name',
				this.name,
				this.name = name)
	}

	public String getRefId() {
		return refId
	}

	public void setRefId(String refId) {
		notifyListeners(this,
				'refId',
				this.refId,
				this.refId = refId)
	}





	def obtainAttributes(){
		def map = [:]
		this.properties.each{ key, value ->
			if(key in [
				'class',
				'metaclass',
				'comments',
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

