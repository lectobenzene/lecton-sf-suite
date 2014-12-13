package com.tcs.mobility.sf.lecton.groovy.source.jumper.models

class FileInformation {

	public FileInformation() {
		// TODO Auto-generated constructor stub
	}

	int startOffset
	int endOffset
	private int length

	int getLength(){
		return endOffset - startOffset
	}
}
