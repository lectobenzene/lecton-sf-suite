package com.tcs.mobility.sf.lecton.bttsource.models.flow.elements

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel
import com.tcs.mobility.sf.lecton.bttsource.models.utils.WatchableList


class TransitionsModel extends SuperParentModel{

	WatchableList transitions;
	
	TransitionsModel(){
		transitions = [];
	}
	
	def addTransition(TransitionModel model){
		transitions.add(model);
	}
	
	def removeTransition(TransitionModel model){
		transitions.remove(model)
	}
	
	def obtainAttributes(){
		Set props = ['transitions']
		super.obtainAttributes(props)
	}
}
