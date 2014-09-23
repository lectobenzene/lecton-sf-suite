package com.tcs.mobility.sf.lecton.xml2xsd.source.builder

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.FieldModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.IndexedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel
import com.tcs.mobility.sf.lecton.bttsource.parsers.context.DataModelParser

class XsdBuilder {

	List buildList

	public static void main(def arg){
		DataModelParser parser = new DataModelParser()
		File file = new File('test/why.xml')
		KeyedCollectionModel rootElement = parser.getRootNodeFromFile(file.text)

		rootElement.children.each {
			if(it instanceof IndexedCollectionModel){
				((IndexedCollectionModel)it).getChildren()
			}
		}
		String content = new XsdBuilder().makeXSD(rootElement)
		println content
	}

	public String makeXSD(KeyedCollectionModel rootElement) {
		buildList = new ArrayList()
		checkForJavaClassExistence(rootElement)
		List finalList = fullBuild(rootElement).readLines()
		buildList.each { List it ->
			it.each {
				finalList.add(finalList.size()-1, it)
			}
		}

		StringWriter writer = new StringWriter()
		finalList.each { writer << it << '\n' }
		return writer.toString()
	}


	private void checkForJavaClassExistence(KeyedCollectionModel parent){

		List<DataElementModel> kCollChildren = parent.getChildren()

		for (DataElementModel child in kCollChildren) {
			if (child instanceof KeyedCollectionModel) {
				KeyedCollectionModel kCollChild = (KeyedCollectionModel) child
				checkForJavaClassExistence(kCollChild)
				buildList.add(checkForExistence(kCollChild))
			} else if (child instanceof IndexedCollectionModel) {
				IndexedCollectionModel iCollChild = (IndexedCollectionModel) child
				DataElementModel childElement = (DataElementModel) iCollChild.getElement()
				if (childElement instanceof KeyedCollectionModel) {
					KeyedCollectionModel kCollChild = (KeyedCollectionModel) childElement
					checkForJavaClassExistence(kCollChild)
					buildList.add(checkForExistence(kCollChild))
				}
				buildList.add(checkForExistence(iCollChild))
			}
		}
	}

	private def checkForExistence(DataElementModel model){
		String xml =  build(model)
		def readLines = xml.readLines()
		readLines.remove(0)
		readLines.remove(readLines.size()-1)
		return readLines
	}

	private String build(DataElementModel model){
		StringWriter writer = new StringWriter()
		MarkupBuilder builder = new MarkupBuilder(writer)
		builder.setDoubleQuotes(true)
		builder.'xs:schema'('xmlns:xs':'http://www.w3.org/2001/XMLSchema'){
			'xs:complexType'(name:"${model.getDataModelAnnotationName()}"){
				'xs:sequence'(){
					if(model instanceof IndexedCollectionModel){
						model.children.each { DataElementModel child ->
							if(child instanceof FieldModel){
								FieldModel fieldModel = (FieldModel)child
								String type = fieldModel.getDataType()
								type = type == 'String' ? 'string' : type
								'xs:element'(name:"${fieldModel.getDataModelAnnotationName()}", type:"xs:${type}")
							} else {
								'xs:element'(name:"${child.getDataModelAnnotationName()}", type:"${child.getDataModelAnnotationName()}", maxOccurs:'unbounded')
							}
						}
					}else{
						model.children.each { DataElementModel child ->
							if(child instanceof FieldModel){
								FieldModel fieldModel = (FieldModel)child
								String type = fieldModel.getDataType()
								type = type == 'String' ? 'string' : type
								'xs:element'(name:"${fieldModel.getDataModelAnnotationName()}", type:"xs:${type}")
							}else {
								'xs:element'(name:"${child.getDataModelAnnotationName()}", type:"${child.getDataModelAnnotationName()}")
							}
						}
					}
				}
			}
		}
		return XmlUtil.serialize(writer.toString())
	}

	private String fullBuild(DataElementModel model){
		StringWriter writer = new StringWriter()
		MarkupBuilder builder = new MarkupBuilder(writer)
		builder.setDoubleQuotes(true)
		builder.'xs:schema'('xmlns:xs':'http://www.w3.org/2001/XMLSchema'){
			'xs:element'(name:"${model.getId()}"){
				'xs:complexType'{
					'xs:sequence'{
						if(model instanceof IndexedCollectionModel){
							model.children.each { DataElementModel child ->
								if(child instanceof FieldModel){
									FieldModel fieldModel = (FieldModel)child
									'xs:element'(name:"${fieldModel.getDataModelAnnotationName()}", type:"xs:${fieldModel.getDataType()}")
								} else {
									'xs:element'(name:"${child.getDataModelAnnotationName()}", type:"${child.getDataModelAnnotationName()}", maxOccurs:'unbounded')
								}
							}
						} else {
							model.children.each { DataElementModel child ->
								if(child instanceof FieldModel){
									FieldModel fieldModel = (FieldModel)child
									'xs:element'(name:"${fieldModel.getDataModelAnnotationName()}", type:"xs:${fieldModel.getDataType()}")
								}else {
									'xs:element'(name:"${child.getDataModelAnnotationName()}", type:"${child.getDataModelAnnotationName()}")
								}
							}
						}
					}
				}
			}
		}
		return XmlUtil.serialize(writer.toString())
	}
}
