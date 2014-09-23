package com.tcs.mobility.sf.lecton.bttsource.parsers.processor

import groovy.xml.MarkupBuilder
import groovy.xml.dom.DOMCategory

import org.w3c.dom.Element
import org.w3c.dom.Node

import com.tcs.mobility.sf.lecton.bttsource.models.parent.elements.Comment
import com.tcs.mobility.sf.lecton.bttsource.models.processor.ProcessorModel
import com.tcs.mobility.sf.lecton.bttsource.models.processor.elements.RefFormatModel
import com.tcs.mobility.sf.lecton.bttsource.parsers.parent.SuperParentParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.utils.NodeUtil

class ProcessorParser extends SuperParentParser{

	def parse(root){
		// Create the Processor Model
		ProcessorModel model = new ProcessorModel()
		use(DOMCategory){
			root.childNodes.each {
				if(it.nodeType == Node.COMMENT_NODE){
					// Add comments to Model
					model.addComment(new Comment(it.data))
				}else if(it instanceof Element){
					// parse the processor
					parseProcessor(it, model)
				}
			}
		}
		return model
	}

	def parseProcessor(Element element, ProcessorModel processorModel){
		// Adds the Attributes of the processor
		addAttributes(element, processorModel)

		// Adds the Children of the processor
		NodeUtil nodeUtil = new NodeUtil()
		def nodeList = nodeUtil.getCommentCombinedNodeList(element)

		// Create the RefFormat Model
		RefFormatModel refFormatModel = null

		nodeList.each { rootNode ->
			rootNode.childNodes.each{
				if(!refFormatModel){
					refFormatModel = new RefFormatModel()
				}
				if(it.nodeType == Node.COMMENT_NODE){
					// Add comments to Model
					refFormatModel.addComment(new Comment(it.data))
				}else if(it instanceof Element){
					switch(it.nodeName){
						case 'refFormat':
							parseRefFormat(it, refFormatModel)
							processorModel.addRefFormat(refFormatModel)
						// Make this null, so that new RefFormat is created in next iteration
							refFormatModel = null
							break
						default:
						// add the unknown element to model
							processorModel.addNode(it)
							break
					}
				}
			}
		}
	}

	def parseRefFormat(Element element, RefFormatModel refFormatModel){
		// Adds the Attributes of the refFormat
		addAttributes(element, refFormatModel)

		// No child for RefFormat
	}

	def build(ProcessorModel model){

		StringWriter writer = new StringWriter()
		MarkupBuilder builder = new MarkupBuilder(writer)

		builder."$ROOT_BUILD_TAG"{
			mkp.yield '\n'

			// add comments
			buildComments(delegate, model)
			
			processor(model.obtainAttributes()){
				mkp.yield '\n'

				model.refFormats.each { refFormatModel ->
					buildComments(delegate, refFormatModel)
					refFormat(refFormatModel.obtainAttributes())
				}

				// add unknown elements at the end
				buildUnknownElements(delegate, model)
			}
		}

		return removeRootNode(writer.toString())
	}


}
