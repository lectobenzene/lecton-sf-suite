package com.tcs.mobility.sf.lecton.bttsource.parsers.flow

import groovy.xml.MarkupBuilder
import groovy.xml.dom.DOMCategory

import org.w3c.dom.Element
import org.w3c.dom.Node

import com.tcs.mobility.sf.lecton.bttsource.models.flow.FlowModel
import com.tcs.mobility.sf.lecton.bttsource.models.flow.elements.EntryActionsModel
import com.tcs.mobility.sf.lecton.bttsource.models.flow.elements.StateModel
import com.tcs.mobility.sf.lecton.bttsource.models.flow.elements.TransitionModel
import com.tcs.mobility.sf.lecton.bttsource.models.flow.elements.TransitionsModel
import com.tcs.mobility.sf.lecton.bttsource.models.parent.elements.Comment
import com.tcs.mobility.sf.lecton.bttsource.parsers.parent.SuperParentParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.utils.NodeUtil

class FlowParser extends SuperParentParser{

	Node flowRootNode

	def parse(root){

		flowRootNode = root

		// Create the Flow Model
		FlowModel model = new FlowModel()
		use(DOMCategory){
			// Adds the Children of the Flow
			NodeUtil nodeUtil = new NodeUtil()
			Node commentedRootNode = nodeUtil.getFullyCommentCombinedNodeList(root)
			def flowRoot

			commentedRootNode.childNodes.each { Node child->
				println child
				switch(getCommentNodeName(child.nodeName)){
					case "flow":
						println 'Flow Found'
						model = parseRootFlow(child, model)
						break
				}
			}
		}
		return model
	}


	def parseRootFlow(Node rootNode, FlowModel model){
		FlowModel flowModel
		rootNode.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// parse the context
				flowModel = parseFlow(child, model)
			}
		}
		return flowModel
	}


	def parseFlow(Node parent, FlowModel flowModel){
		// Adds the Attributes of the context
		addAttributes(parent, flowModel)

		// Create the RefKColl Model
		parent.childNodes.each { Node child ->

			switch(getCommentNodeName(child.nodeName)){
				case 'state':
					println 'State Found'
					StateModel stateModel = parseState(child)
					flowModel.addState(stateModel)
					break

				default:
				// add the unknown element to model
					flowModel.addNode(child)
					break
			}
		}
		return flowModel
	}

	def parseState(Node parent){
		StateModel model = new StateModel()
		parent.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the refKColl
				addAttributes(child, model)

				// Child is optional for refKColl
				child.childNodes.each { Node subChild ->
					switch(getCommentNodeName(subChild.nodeName)){
						case 'entryActions':
							println 'Entry Actions Found'
							EntryActionsModel entryActionsModel = "parseEntryActions"(subChild)
							model.setEntryActions(entryActionsModel)
							break
						case 'transitions':
							println 'Transitions Found'
							TransitionsModel transitionsModel = "parseTransitions"(subChild)
							model.setTransitions(transitionsModel)
							break
						default:
						// add the unknown element to model
							model.addNode(subChild)
							break
					}
				}
			}
		}
		return model
	}

	def parseTransitions(Node parent){
		TransitionsModel model = new TransitionsModel()
		parent.childNodes.each { Node child ->

			if(child.nodeType == Node.COMMENT_NODE){
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				addAttributes(child, model)

				child.childNodes.each { Node subChild ->
					switch(getCommentNodeName(subChild.nodeName)){
						case 'transition':
							println 'Transition Found'
							TransitionModel transitionModel = parseTransition(subChild)
							model.addTransition(transitionModel)
							break
						default:
							model.addNode(subChild)
							break
					}
				}
			}
		}
		return model
	}

	def parseTransition(Node parent){
		TransitionModel model = new TransitionModel()
		parent.childNodes.each { Node child ->

			if(child.nodeType == Node.COMMENT_NODE){
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				addAttributes(child, model)
			}
		}
		return model
	}

	def parseEntryActions(Node parent){
		EntryActionsModel model = new EntryActionsModel()
		parent.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the refKColl
				addAttributes(child, model)

				// Child is optional for refKColl
				child.childNodes.each { Node subChild ->

					// TODO : Handle for different Types of states
					switch(getCommentNodeName(subChild.nodeName)){
						case 'UINEKNEH':
							println 'Entry Actions Found'
							break
						default:
						// add the unknown element to model
							println 'ADDDING UNKNOWN ENTRY ACTIONS'
							model.addNode(subChild)
							break
					}
				}
			}
		}
		return model
	}

	def build(FlowModel model){

		StringWriter writer = new StringWriter()
		MarkupBuilder builder = new MarkupBuilder(writer)

		builder."$ROOT_BUILD_TAG"{
			mkp.yield '\n'

			// add comments
			buildComments(delegate, model)

			flow(model.obtainAttributes()){
				model.getStates().each { StateModel stateModel ->
					// add comments
					buildComments(delegate, stateModel)
					state(stateModel.obtainAttributes()){
						if(stateModel.entryActions){
							// add comments
							buildComments(delegate, stateModel.entryActions)
							entryActions(stateModel.entryActions.obtainAttributes()){
								// add unknown elements at the end
								buildProcessedUnknownElements(delegate, stateModel.entryActions)
							}
						}
						if(stateModel.transitions){
							// add comments
							buildComments(delegate, stateModel.transitions)
							transitions(stateModel.transitions.obtainAttributes()){
								stateModel.transitions.transitions.each { TransitionModel transitionModel ->
									// add comments
									buildComments(delegate, transitionModel)
									transition(transitionModel.obtainAttributes())
								}
								// add unknown elements at the end
								buildProcessedUnknownElements(delegate, stateModel.transitions)
							}
						}
						// add unknown elements at the end
						buildProcessedUnknownElements(delegate, stateModel)
					}
				}
				// add unknown elements at the end
				buildProcessedUnknownElements(delegate, model)
			}
		}
		return removeRootNode(writer.toString())
	}
}
