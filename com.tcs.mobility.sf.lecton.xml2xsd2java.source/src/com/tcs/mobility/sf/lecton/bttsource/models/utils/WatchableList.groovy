package com.tcs.mobility.sf.lecton.bttsource.models.utils

class WatchableList extends ArrayList {

	ArrayList<IWatchableListListener> listeners = new ArrayList<IWatchableListListener>()
	@Override
	public boolean add(Object e) {
		boolean isSuccess = super.add(e)
		listeners.each { it.elementAdded(e) }
		return isSuccess
	}
	@Override
	public boolean remove(Object o) {
		boolean isSuccess = super.remove(o)
		listeners.each { it.elementRemoved(o) }
		return isSuccess
	}

	def addListListener(IWatchableListListener listener){
		listeners.add(listener)
	}
}
