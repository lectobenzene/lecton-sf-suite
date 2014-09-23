package com.tcs.mobility.sf.lecton.bttsource.models.parent.support



class DynamicPropertiesSupport extends DataBindingSupport{

	def dynamicProperties= [:]

	def propertyMissing(String name, value) {
		dynamicProperties[name] = value
	}
	def propertyMissing(String name) {
		dynamicProperties[name]
	}
}
