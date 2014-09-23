package com.tcs.mobility.sf.lecton.xml2xsd.source.parser



class XsdParser {

	public static final String TAG_ELEMENT = 'element'
	public static final String ATTRIBUTE_NAME = 'name'

	public XsdParser() {
		// TODO Auto-generated constructor stub
	}

	public static void main(def args){
		File file = new File("test/GetAccountListRequest.xsd")
		def primaryElements = new XsdParser().getPrimaryElements(file)
		println primaryElements
	}

	/**
	 * Returns the list of ELEMENT present in the XSD file
	 * 
	 * @param file The XSD file to be parsed
	 * @return List of 'element'
	 */
	public List<String> getPrimaryElements(File file){
		XmlParser parser = new XmlParser()
		Node root = parser.parse(file)

		List<String> elementList = new ArrayList<String>()
		root.children().findAll{
			String childName = it.name()
			childName.endsWith(TAG_ELEMENT)
		}.each {
			elementList.add(it."@${ATTRIBUTE_NAME}")
		}
		return elementList
	}
}
