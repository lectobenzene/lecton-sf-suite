package com.tcs.mobility.sf.lecton.groovy.source.jumper.xmlparsing

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.w3c.dom.Document

import com.tcs.mobility.sf.lecton.groovy.source.jumper.models.FileInformation

class MasterConfigParser {

	public static final String SF_MASTER_SECTION_OPEN = "<(?:\\s)*section(?:\\s)+path(?:\\s)*=\"([^\"]*)\""
	public static final String SF_MASTER_CONFIG_ELEMENT = "<(?:\\s)*configElement(?:\\s)+path(?:\\s)*=\"([^\"]*)\""
	public static final String SF_MASTER_SECTION_CLOSE = "</section>"

	private static final Pattern PATTERN_MASTER_SECTION_OPEN = Pattern.compile(SF_MASTER_SECTION_OPEN)
	private static final Pattern PATTERN_MASTER_CONFIG_ELEMENT = Pattern.compile(SF_MASTER_CONFIG_ELEMENT)
	private static final Pattern PATTERN_MASTER_SECTION_CLOSE = Pattern.compile(SF_MASTER_SECTION_CLOSE)


	public static void main(String[] args) {
		def obj = new MasterConfigParser()
		File file = new File('test/masterConfig.xml')

		FileInformation info = null
								info = obj.getOffsetInfo(file.getText(), "sf.security.permission")
		//						info = obj.getOffsetInfo(file.getText(), "sil.getAccountListConnector")
		//						info = obj.getOffsetInfo(file.getText(), "sf.functionalLogger")
		//				 		info = obj.getOffsetInfo(file.getText(), "sf.connector")
		//						info = obj.getOffsetInfo(file.getText(), "security.permission")
		println info.getStartOffset()
		println info.getEndOffset()
		println info.getLength()
	}

	public FileInformation getOffsetInfo(String content, String key) {
		
		int indexa = content.indexOf("permission");
		System.out.println("INDOE : "+indexa);
		
		String[] keys = key.split("\\.")

		List<String> sectionKeys = keys.toList()
		String configKey = sectionKeys.pop()

		boolean found = false
		int sectionEnd = 0
		int index = 0

		FileInformation info = new FileInformation()
		int globalOffset = 0
		
		content.eachLine {

			Matcher sectionOpenMatcher = PATTERN_MASTER_SECTION_OPEN.matcher(it)
			Matcher sectionCloseMatcher = PATTERN_MASTER_SECTION_CLOSE.matcher(it)
			Matcher configElementMatcher = PATTERN_MASTER_CONFIG_ELEMENT.matcher(it)

			// If found, then don't process anything.
			if (!found) {

				// Check if line is a section header, only if configElement tag is not yet reached
				if (sectionOpenMatcher.find() && sectionKeys.size() != index) {
					String sectionPart = sectionOpenMatcher.group(1)

					/*
					 *  If the parent section header does not match, none of the child
					 *  section header should be considered. There is no use in processing them.
					 */
					if(sectionEnd == 0){
						if (sectionPart.equalsIgnoreCase(sectionKeys.get(index))) {
							// If found, then check the next section header
							index++
						}else{
							// If not found, then increment the section end counter
							sectionEnd++
						}
					}else{
						/*
						 *  Increment the section end counter for every child section header
						 */
						sectionEnd++
					}
				}

				// Decrement the counter for every section close tag found
				if(sectionCloseMatcher.find()){
					sectionEnd--
				}

				/*
				 *  If the index is pointing to the last one, then it means that no 
				 *  more section tag needs to be found. Its time to find the 
				 *  configElement tag. Check the configElement tag only 
				 *  within the respective section tag, not everywhere.
				 */
				if(sectionKeys.size() == index && sectionEnd == 0){
					if(configElementMatcher.find()){
						String configElementPart = configElementMatcher.group(1)
						if(configElementPart.equalsIgnoreCase(configKey)){
							// If found, gather useful information.
							found = true
							info.startOffset = configElementMatcher.start(1) + globalOffset
							info.endOffset = configElementMatcher.end(1) + globalOffset
						}
					}
				}
			}
			
			globalOffset += it.length() + 2 // 2 because of the 'return' character
			println globalOffset
		}
		if(found){
			return info
		}
		return null
	}
}