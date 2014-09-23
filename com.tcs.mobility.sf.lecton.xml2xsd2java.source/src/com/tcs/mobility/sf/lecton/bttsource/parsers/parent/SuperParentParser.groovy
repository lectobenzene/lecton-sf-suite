package com.tcs.mobility.sf.lecton.bttsource.parsers.parent

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.Element
import org.w3c.dom.Node

import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel
import com.tcs.mobility.sf.lecton.bttsource.parsers.parent.support.DynamicSupport

class SuperParentParser  extends DynamicSupport {

	final static String ROOT_BUILD_TAG = 'rootwolverine'

	final static String PROCESSOR = 'PROCESSOR'
	final static String FLOW = 'FLOW'
	final static String CONTEXT = 'CONTEXT'
	final static String FORMATTER = 'FORMATTER'

	/**
	 * @deprecated Use {@link #buildProcessedUnknownElements(def, def)}
	 * 
	 * @param parent Parent node
	 * @param model The respective model
	 */
	def buildUnknownElements(def parent, def model){
		model.nodes.each{ Element node ->
			parent.mkp.yieldUnescaped "${getNodeInString(node)}"
		}
	}

	def buildProcessedUnknownElements(def parent, def model){
		model.nodes.each{ Element node ->
			String temp = getNodeInString(node)
			parent.mkp.yieldUnescaped "${removeTagFromString(temp)}"
		}
	}

	def removeTagFromString(String input){
		return input.replaceAll(/<[\/]?comment-[^>]*>/, "")
	}

	def buildComments(def parent, def model){
		model.comments.each{
			parent.mkp.yield '\n'
			parent.mkp.comment "${it.text}"
		}
	}

	def removeRootNode(def input){
		return input.replaceFirst("<$ROOT_BUILD_TAG>", "").replaceAll("</$ROOT_BUILD_TAG>", "")
	}

	def getNodeInString(Node node){
		TransformerFactory transFactory = TransformerFactory.newInstance()
		Transformer transformer = transFactory.newTransformer()
		StringWriter buffer = new StringWriter()
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
		transformer.transform(new DOMSource(node),
				new StreamResult(buffer))
		return buffer.toString()
	}

	def addAttributes(Element element, SuperParentModel model){
		def attribMap = element.attributes
		attribMap.length.times {
			// println "${attribMap.item(it).nodeName} = ${attribMap.item(it).nodeValue}"
			model."${attribMap.item(it).nodeName}" = attribMap.item(it).nodeValue
		}
	}

	def getCommentNodeName(String name){
		// COMMENT- Start from 8th Char
		return name.substring(8)
	}
}
