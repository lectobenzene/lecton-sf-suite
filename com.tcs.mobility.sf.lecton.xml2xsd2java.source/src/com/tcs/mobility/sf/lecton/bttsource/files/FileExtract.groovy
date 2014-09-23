package com.tcs.mobility.sf.lecton.bttsource.files

import java.util.ArrayList;


class FileExtract {

	public FileExtract() {
	}

	static void main(def args){
		
	}

	/**
	 * Returns an ArrayList of keys contained in the given .PROPERTIES file
	 * 
	 * @param file The .PROPERTIES file
	 * @return An array list of keys in the file
	 */
	public ArrayList<String> getPropertyKeys(File file){
		ArrayList<String> propertiesKeyList = new ArrayList<String>()

		file.eachLine { line ->
			line = line.trim()

			// Do not consider empty lines and comments
			if(line && !line.startsWith('#')){
				propertiesKeyList.add(line.split('=')[0])
			}
		}
		return propertiesKeyList;
	}

}
