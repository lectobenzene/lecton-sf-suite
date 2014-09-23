package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.childelements

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel


class IniValueModel extends SuperParentModel{

	public String name;
	public String value;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
