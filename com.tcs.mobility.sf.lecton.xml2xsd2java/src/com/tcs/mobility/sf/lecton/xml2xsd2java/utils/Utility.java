package com.tcs.mobility.sf.lecton.xml2xsd2java.utils;

public class Utility {

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, lastIndex);
	}

	public static String getFileExtension(String fileName) {
		String[] split = fileName.split("\\.");
		return split[split.length - 1];
	}

}
