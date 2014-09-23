package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.reference

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel
import com.tcs.mobility.sf.lecton.bttsource.models.utils.WatchableList


class RefKCollModel extends SuperParentModel{

	public String refId

	// Child element. May or may not exist
	public WatchableList iniValues = []
	
	// Parent kColl that contains the full depth elements(all elements refereed by this kColl)
	public KeyedCollectionModel rootKColl

	
	def addIniValue(def iniValue){
		iniValues.add(iniValue)
	}
	
	def removeIniValue(def iniValue){
		iniValues.remove(iniValue)
	}
	

	public String getRefId() {
		return refId
	}

	public void setRefId(String refId) {
		this.refId = refId
	}

	public WatchableList getIniValues() {
		return iniValues
	}

	public void setIniValues(WatchableList iniValues) {
		this.iniValues = iniValues
	}

	public KeyedCollectionModel getRootKColl() {
		return rootKColl
	}

	public void setRootKColl(KeyedCollectionModel rootKColl) {
		this.rootKColl = rootKColl
	}
	
	def obtainAttributes(){
		def map = [:]
		this.properties.each{ key, value ->
			if(key in [
				'class',
				'metaclass',
				'comments',
				'nodes',
				'iniValues',
				'rootKColl',
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
