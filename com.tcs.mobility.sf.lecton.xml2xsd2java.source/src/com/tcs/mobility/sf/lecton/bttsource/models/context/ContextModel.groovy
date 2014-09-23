package com.tcs.mobility.sf.lecton.bttsource.models.context

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel
import com.tcs.mobility.sf.lecton.bttsource.models.utils.WatchableList


/**
 * Not used so far
 * @author Saravana
 *
 */
class ContextModel extends SuperParentModel {

	public String id;
	public String implClass;
	public String type;
	
	public WatchableList refKColls = [];
	public WatchableList refServices = [];
	
	
	def addRefKColl(def refKColl){
		refKColls.add(refKColl)
	}
	
	def removeRefKColl(def refKColl){
		refKColls.remove(refKColl)
	}
	
	def addRefService(def refService){
		refServices.add(refService)
	}
	
	def removeRefService(def refService){
		refServices.remove(refService)
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		notifyListeners(this,
			'id',
			this.id,
			this.id = id)
	}
	
	public String getImplClass() {
		return implClass;
	}
	public void setImplClass(String implClass) {
		notifyListeners(this,
			'implClass',
			this.implClass,
			this.implClass = implClass)
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public WatchableList getRefKColls() {
		return refKColls;
	}
	public void setRefKColls(WatchableList refKColls) {
		this.refKColls = refKColls;
	}
	public WatchableList getRefServices() {
		return refServices;
	}
	public void setRefServices(WatchableList refServices) {
		this.refServices = refServices;
	}
	
	def obtainAttributes(){
		def map = [:]
		this.properties.each{ key, value ->
			if(key in [
				'class',
				'metaclass',
				'listener',
				'comments',
				'refKColls',
				'refServices',
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
