package com.tcs.mobility.sf.lecton.jumper.models;

import org.eclipse.core.resources.IFile;

public class FileInformation {

	/** The file to be opened */
	private IFile file;

	/** The index in the file where the given string is found */
	private int offset;

	private int startIndex;
	private int endIndex;

	public FileInformation(IFile file, int offset) {
		this.file = file;
		this.offset = offset;
	}

	public FileInformation(IFile file, int start, int end) {
		this.file = file;
		startIndex = start;
		endIndex = end;
	}

	public IFile getFile() {
		return file;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

}
