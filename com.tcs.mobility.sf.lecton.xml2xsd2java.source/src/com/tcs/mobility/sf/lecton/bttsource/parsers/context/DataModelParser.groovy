package com.tcs.mobility.sf.lecton.bttsource.parsers.context

import org.xml.sax.SAXException

import com.tcs.mobility.sf.lecton.bttsource.errorhandler.IMessageInjector
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.FieldModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.IndexedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel



class DataModelParser {

	IMessageInjector messageIndicator

	public DataModelParser() {
	}

	public void setMessageInjectorListener(def injector){
		this.messageIndicator = injector
	}

	static void main(def args){
		File file = new File('temp/response.txt')
		DataModelParser parser = new DataModelParser()
		String xmlContent = parser.readFromFile(file)
		println xmlContent.trim()

		def slurper = null
		try{
			slurper = new XmlSlurper().parseText(xmlContent)
		}catch(SAXException e){
			println "Sax Exception occured - ${e.message}"
			println "Sax Exception occured - ${e.getStackTrace()}"

			e.printStackTrace()
			//parser.messageIndicator.showErrorMessage("Parsing is improper")
		}catch(IOException e){
			e.printStackTrace()
			//parser.messageIndicator.showErrorMessage("IO exception occured")
		}
		if(slurper){
			def childs = slurper.childNodes()

			KeyedCollectionModel parentKColl = new KeyedCollectionModel("response", null)
			def resultKColl = parser.getKColl(slurper, parentKColl)

			println resultKColl.id
			parser.convertAllIColl(resultKColl)

			/*println resultKColl.indexedCollectionModels[0].getElement().id
			 println resultKColl.keyedCollectionModels[0].id
			 println resultKColl.keyedCollectionModels[0].indexedCollectionModels[0].id
			 println resultKColl.getChildren()*/
		}
	}

	/**
	 * Returns the root kColl object after parsing the given text
	 * 
	 * @param xmlContent text to be parsed
	 * 
	 * @return the root kColl object
	 */
	public KeyedCollectionModel getRootNodeFromFile(def xmlContent){

		def slurper = null
		try{
			slurper = new XmlSlurper().parseText(xmlContent)
		}catch(SAXException e){
			messageIndicator.showErrorMessage("Improper Parsing : ${e.message} \n\nRefer console for detailed message")
			messageIndicator.showConsoleMessage("Improper Parsing : ${e.getStackTrace()}")
		}catch(IOException e){
			messageIndicator.showErrorMessage("IO exception occured : ${e.message} \n\nRefer console for detailed message")
			messageIndicator.showConsoleMessage("IO exception occured : ${e.getStackTrace()}")
		}

		if(slurper){
			def childs = slurper.childNodes()
			// Create the root kColl, this doesn't not have any parent
			KeyedCollectionModel parentKColl = new KeyedCollectionModel("ROOT", null)
			// Obtains hierarchy of child fields and kColls for the parent
			def resultKColl = getKColl(slurper, parentKColl)

			// Analyse all the kColls and if copies are found, converts into an iColl
			convertAllIColl(resultKColl)
			return resultKColl
		}
		return null
	}

	/**
	 * Initial parsing only creates child Fields and KColls. Then, the hierarchy is
	 * analysed for duplicate kColls. If found, it is converted to an iColl<br/>
	 * <b>This is a recursive method</b>
	 * @param parent The parent kColl
	 */
	private void convertAllIColl(parent){
		if(parent.keyedCollectionModels.size()==0){
			return
		}
		for(child in parent.keyedCollectionModels){
			convertAllIColl(child)
		}
		convertToIndexedCollection(parent)
	}

	/**
	 * Sub method that converts the immediate childs of a kColl into iColl if duplicates
	 * are found.
	 * 
	 * @param parent parent kColl
	 */
	private void convertToIndexedCollection(parent){
		def list = []
		for(kCollModel in parent.keyedCollectionModels){
			list.add(kCollModel.id)
		}
		list.intersect(list.toSet()).each{ list.remove(it) }
		list.each{
			// Remove all the duplicate kColls and get One
			KeyedCollectionModel kColl = parent.removeKColl(it)
			// Add the IColl with the kColl inside
			IndexedCollectionModel iColl = new IndexedCollectionModel("${kColl.id}-LIST", parent)
			kColl.changeParent(iColl)
			iColl.addElement(kColl)
			parent.addIColl(iColl)
		}
	}


	/**
	 * Iterates through the entire nodes recursively and create Fields, KColls  and IColls and returns
	 * the root kColl
	 * 
	 * <b>This is a recursive method</b>
	 * 
	 * @param parent the parent of the given DataElement
	 * @param kColl the parent kColl
	 * @return the resulting root kColl
	 */
	private KeyedCollectionModel getKColl(parent, kColl){
		def childNode = null
		for(child in parent.childNodes()){
			if(child.childNodes().size() == 0){
				FieldModel field = new FieldModel(child.name(), parent)
				if(child.text()){
					field.setDataType(child.text())
				}
				kColl.addField(field)
			}else if(checkIfIColl(child)){
				childNode = checkIfIColl(child)
				IndexedCollectionModel iColl = new IndexedCollectionModel(child.name(), parent)
				if(childNode.childNodes().size()==0){
					FieldModel field = new FieldModel(childNode.name(), iColl)
					if(childNode.text()){
						field.setDataType(childNode.text())
					}
					iColl.addElement(field)
				}else{
					KeyedCollectionModel innerKColl = new KeyedCollectionModel(childNode.name(), iColl)
					iColl.addElement(getKColl(childNode, innerKColl))
				}
				kColl.addIColl(iColl)
			} else {
				KeyedCollectionModel innerKColl = new KeyedCollectionModel(child.name(), parent)
				kColl.addKColl(getKColl(child, innerKColl))
			}
		}
		return kColl
	}

	/**
	 * Checks if the given root node is an IColl, if so returns the element inside 
	 * the IColl
	 * 
	 * @param root The parent node
	 * @return the child node contained within the IColl
	 */
	private def checkIfIColl(def root){
		def childName = null
		// Only consider if 2 or more children
		if(root.childNodes().size() < 2){return null}
		for(child in root.childNodes()){
			if(!childName){
				childName = child.name()
			}
			if(childName != child.name()){
				return null
			}
		}
		return root.childNodes()[0]
	}


	/**
	 * Returns a string read from the given file
	 * @param file a file object
	 * @return string content of the file
	 */
	public String readFromFile(File file){
		def writer = new StringWriter()
		file.eachLine {
			writer.append(it).append('\n')
		}
		return writer.toString()
	}
}
