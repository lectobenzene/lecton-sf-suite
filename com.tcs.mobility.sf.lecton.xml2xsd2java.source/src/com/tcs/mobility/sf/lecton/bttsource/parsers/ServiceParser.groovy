package com.tcs.mobility.sf.lecton.bttsource.parsers

import groovy.xml.DOMBuilder
import groovy.xml.XmlUtil

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

import com.tcs.mobility.sf.lecton.bttsource.models.context.ContextModel
import com.tcs.mobility.sf.lecton.bttsource.models.flow.FlowModel
import com.tcs.mobility.sf.lecton.bttsource.models.processor.ProcessorModel
import com.tcs.mobility.sf.lecton.bttsource.parsers.context.ContextParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.flow.FlowParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.parent.SuperParentParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.processor.ProcessorParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.utils.NodeUtil

class ServiceParser extends SuperParentParser{
	private Document doc

	def processorElement
	def flowElement
	def contextElement
	def formattersElement
	def unknownElement

	/**
	 * This is the root node name. This is usually the Service Name with .xml as extension
	 */
	def rootNodeName

	ProcessorModel processorModel
	ContextModel contextModel
	FlowModel flowModel

	ProcessorParser processorParser
	ContextParser contextParser
	FlowParser flowParser

	ServiceParser(){
		processorParser = new ProcessorParser()
		contextParser = new ContextParser()
		flowParser = new FlowParser()
	}

	def parse(def content){
		def reader  = new StringReader(content)
		doc = DOMBuilder.parse(reader)
		Element root = doc.documentElement

		// Sets the root name, so that this is can be used while building the xml
		rootNodeName = root.getNodeName()

		// Create the models and fill the data
		generateModels(root)
	}

	def build(){
		def writer = new StringWriter()
		writer << "<${rootNodeName}>"

		def content = processorParser.build(processorModel)
		if(content){
			writer << content
		}else{
			writer << removeRootNode(getNodeInString(processorElement))
		}

		content = flowParser.build(flowModel)
		if(content){
			writer << content
		}else{
			writer << removeRootNode(getNodeInString(flowElement))
		}

		content = contextParser.build(contextModel)
		if(content){
			writer << content
		}else{
			writer << removeRootNode(getNodeInString(contextElement))
		}

		content = null
		if(content){
			writer << content
		}else{
			writer << removeRootNode(getNodeInString(formattersElement))
		}

		// All the unidentified items are pushed to the last
		writer << removeRootNode(getNodeInString(unknownElement))
		writer << "</${rootNodeName}>"
		return XmlUtil.serialize(writer.toString())
	}

	def generateModels(Element root){

		NodeUtil nodeUtil = new NodeUtil()
		def nodeList = nodeUtil.getCommentCombinedNodeList(root)

		processorElement = nodeUtil.createNode(ROOT_BUILD_TAG, doc)
		flowElement = nodeUtil.createNode(ROOT_BUILD_TAG, doc)
		contextElement = nodeUtil.createNode(ROOT_BUILD_TAG, doc)
		formattersElement = nodeUtil.createNode(ROOT_BUILD_TAG, doc)
		unknownElement = nodeUtil.createNode(ROOT_BUILD_TAG, doc)

		nodeList.each { element ->
			switch(getNodeType(element)){
				case 'processor':
					appendChildren(processorElement, element)
					break
				case 'flow':
					appendChildren(flowElement, element)
					break
				case 'context':
					appendChildren(contextElement, element)
					break
				case 'formatters':
					appendChildren(formattersElement, element)
					break
				default:
					appendChildren(unknownElement, element)
					break
			}
		}
		// ProcessorModel is created
		processorModel = processorParser.parse(processorElement)

		// ContextModel is created
		contextModel = contextParser.parse(contextElement)

		// FlowModel is created
		flowModel = flowParser.parse(flowElement)

		// TODO: FormattersModel is to be created

	}

	def appendChildren(Element srcElement, Element element){
		def children = element.childNodes
		children.each {
			Node node = it.cloneNode(true)
			srcElement.appendChild(node)
		}
	}

	def String getNodeType(element){
		String type = 'type unknown'
		element.each { item ->
			switch(item.nodeName.toLowerCase()){
				case 'processor':
					type =  'processor'
					break
				case 'icoll':
				case 'kcoll':
				case 'context':
					type = 'context'
					break
				case 'flow':
					type = 'flow'
					break
				case 'fmtdef':
					type = 'formatters'
					break
			}
		}
		return type
	}
}