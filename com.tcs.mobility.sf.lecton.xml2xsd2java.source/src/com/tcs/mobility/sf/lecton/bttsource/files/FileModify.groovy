package com.tcs.mobility.sf.lecton.bttsource.files

class FileModify {

	public FileModify() {
		
	}
	
	static void main(def args){
		FileModify modifier = new FileModify()
	}

	public String updateDseIniFile(def file, final String webServiceEntry){
		return insertInFile(file, '<kColl id="files">', webServiceEntry)
	}
	
	private String insertInFile(def file, final String keyString, final String contentToAppend){
		StringWriter fileContents = new StringWriter()
		file.eachLine{
			fileContents.append(it.toString()).append("\n")
			if(it.trim().contains(keyString)){
				fileContents.append("\n").append(contentToAppend).append("\n")
			}
		}
		return fileContents.toString()
	}
}
