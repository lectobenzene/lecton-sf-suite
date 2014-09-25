package com.tcs.mobility.sf.lecton.xml2xsd2java.utils;

public class Utility {

	/**
	 * Returns the file name without the extension part
	 * 
	 * @param fileName
	 *            Name of the file
	 * @return
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, lastIndex);
	}

	/**
	 * Returns the extension of the file
	 * 
	 * @param fileName
	 *            name of the file
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		String[] split = fileName.split("\\.");
		return split[split.length - 1];
	}

}
