package com.tcs.mobility.sf.lecton.bttsource.models.flow.elements

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel


class StateModel extends SuperParentModel{

	String id;
	String type;

	EntryActionsModel entryActions;
	TransitionsModel transitions;
	
	def obtainAttributes(){
		Set props = ['entryActions','transitions']
		super.obtainAttributes(props)
	}
	
}
