package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.reference

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel


class RefServiceModel extends SuperParentModel{

	public String refId;
	public String alias;
	public String type;

	//Assumed that there will be no child for this node
	
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
