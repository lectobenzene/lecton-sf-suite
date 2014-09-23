package com.tcs.mobility.sf.lecton.bttsource.models.flow.elements

import com.tcs.mobility.sf.lecton.bttsource.models.flow.elements.types.parent.StateType
import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel
import com.tcs.mobility.sf.lecton.bttsource.models.utils.WatchableList


class EntryActionsModel extends SuperParentModel{

	WatchableList entryActions;
	
	EntryActionsModel(){
		entryActions = []
	}
	
	def addEntryAction(StateType model){
		entryActions.add(model);
	}
	
	def removeTransition(StateType model){
		entryActions.remove(model)
	}
	
	def obtainAttributes(){
		Set props = ['entryActions']
		super.obtainAttributes(props)
	}
	
}
