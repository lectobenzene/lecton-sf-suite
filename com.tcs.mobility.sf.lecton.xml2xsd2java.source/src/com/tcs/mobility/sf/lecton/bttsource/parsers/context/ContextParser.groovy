package com.tcs.mobility.sf.lecton.bttsource.parsers.context

import groovy.xml.MarkupBuilder
import groovy.xml.dom.DOMCategory

import org.w3c.dom.Element
import org.w3c.dom.Node

import com.tcs.mobility.sf.lecton.bttsource.models.context.ContextModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.childelements.IniValueModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.FieldModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.IndexedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.elements.RefDataModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.reference.RefKCollModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.reference.RefServiceModel
import com.tcs.mobility.sf.lecton.bttsource.models.parent.elements.Comment
import com.tcs.mobility.sf.lecton.bttsource.parsers.parent.SuperParentParser
import com.tcs.mobility.sf.lecton.bttsource.parsers.utils.NodeUtil

/**
 * Currently using/ Development phase
 * 
 * Planned to use when CONTEXT editor is ready
 * 
 * @author Saravana
 *
 */
class ContextParser extends SuperParentParser{

	Node contextRootNode
	Map contextRootMap
	Set contextRootMapKeySet

	def parse(root){

		contextRootNode = root

		// Create the Processor Model
		ContextModel model = new ContextModel()
		use(DOMCategory){
			// Adds the Children of the context
			NodeUtil nodeUtil = new NodeUtil()
			Node commentedRootNode = nodeUtil.getFullyCommentCombinedNodeList(root)
			def contextRoot
			contextRootMap = [:]
			commentedRootNode.childNodes.each { Node child->
				switch(getCommentNodeName(child.nodeName)){
					case "context":
						println 'Context Found'
						contextRoot = child
						break
					case "kColl":
						println 'KColl Found'
						contextRootMap.put(child.getLastChild()."@id", child)
						break
					case "iColl":
						println 'iColl found'
						contextRootMap.put(child.getLastChild()."@id", child)
						break
				}

			}
			parseRootContext(contextRoot, model)
		}
		return model
	}

	def parseRootContext(Element rootNode, ContextModel model){
		ContextModel contextModel
		rootNode.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// parse the context
				contextModel = parseContext(child, model)
			}
		}
		return contextModel
	}

	def parseRefKColl(Node parent){
		RefKCollModel model = new RefKCollModel()
		parent.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the refKColl
				addAttributes(child, model)

				// Child is optional for refKColl
				child.childNodes.each { Node iniChild ->
					switch(getCommentNodeName(iniChild.nodeName)){
						case 'iniValue':
							IniValueModel iniValueModel = "parseIniValue"(iniChild)
							model.addIniValue(iniValueModel)
							break
						default:
						// add the unknown element to model
							model.addNode(iniChild)
							break
					}
				}
			}
		}
		return model
	}

	def parseRefService(Node parent){
		RefServiceModel model = new RefServiceModel()
		parent.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the refService
				addAttributes(child, model)
				// No child for refService
			}
		}
		return model
	}


	def parseIniValue(Node parent){
		IniValueModel model = new IniValueModel()
		parent.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the IniValue
				addAttributes(child, model)
				// No child for iniValue
			}
		}
		return model
	}

	def parseContext(Element element, ContextModel contextModel){
		// Adds the Attributes of the context
		addAttributes(element, contextModel)

		// Create the RefKColl Model
		element.childNodes.each { Node child ->

			switch(getCommentNodeName(child.nodeName)){
				case 'refKColl':
					RefKCollModel refKCollModel = "parseRefKColl"(child)
					refKCollModel = parseKCollAndIColl(refKCollModel)
					contextModel.addRefKColl(refKCollModel)
					break
				case 'refService':
					def refServiceModel = "parseRefService"(child)
					contextModel.addRefService(refServiceModel)
					break
				default:
				// add the unknown element to model

					contextModel.addNode(child)
					break
			}
		}
		return contextModel
	}


	def parseKCollAndIColl(RefKCollModel refKCollModel){
		String parentName = refKCollModel.refId
		contextRootMapKeySet =  contextRootMap.keySet()

		KeyedCollectionModel rootKColl = new KeyedCollectionModel("rootKColl", null)
		KeyedCollectionModel childKColl = createDataElements(parentName, rootKColl, null)

		/*
		 *  Denotes that this is the root element.
		 *  Required to calculate compositeId for data elements
		 */
		childKColl.isRoot = true
		childKColl.setParent(null)

		// add the rootKcoll to the refKcollModel
		refKCollModel.rootKColl = childKColl
		return refKCollModel
	}

	def DataElementModel createDataElements(String refId, DataElementModel parent, Node parentNode){
		if(!parentNode){
			Node node = contextRootMap.get(refId)
			if(!node){
				return parent
			}
			parentNode = node
		}
		switch(getCommentNodeName(parentNode.nodeName)){
			case 'field':
				FieldModel fieldModel = parseField(parent, parentNode)
				return fieldModel
				break
			case 'kColl':
				KeyedCollectionModel kCollModel = parseKColl(refId, parent, parentNode)
				parentNode.lastChild.childNodes.each { Node child ->
					DataElementModel model = createDataElements(refId, kCollModel, child)
					if(model){
						kCollModel.addModel(model)
					}
				}
				return kCollModel

				break
			case 'iColl':
				IndexedCollectionModel iCollModel = parseIColl(refId, parent, parentNode)
				parentNode.lastChild.childNodes.each { Node child ->
					DataElementModel model = createDataElements(refId, iCollModel, child)
					if(model){
						iCollModel.addModel(model)
					}
				}
				return iCollModel
				break
			case 'refData':
				RefDataModel refDataModel = parseRefData(parent, parentNode)
				def subRefId =  parentNode.lastChild."@refId"
				DataElementModel model = createDataElements(subRefId, refDataModel, null)
				//RefDataModel is currently the parent of model. Changing the parent
				model.setParent(parent)
				if(model){
					return model
				}else{
					return refDataModel
				}
				break
			default:
			// add the unknown node to the parent
				parent.addNode(parentNode)
				return null
				break
		}
	}


	def RefDataModel parseRefData(DataElementModel parent, Node parentNode){
		RefDataModel model = new RefDataModel(null, parent)
		parentNode.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the Field
				addAttributes(child, model)
			}
		}
		return model

	}


	def FieldModel parseField(DataElementModel parent, Node parentNode){
		FieldModel model = new FieldModel(null, parent)
		parentNode.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the Field
				addAttributes(child, model)
			}
		}
		return model

	}

	def KeyedCollectionModel parseKColl(String refId, DataElementModel parent, Node parentNode){
		KeyedCollectionModel model = new KeyedCollectionModel(refId, parent)
		parentNode.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the IniValue
				addAttributes(child, model)
				// No child for iniValue
			}
		}
		return model

	}

	def IndexedCollectionModel parseIColl(String refId, DataElementModel parent, Node parentNode){
		IndexedCollectionModel model = new IndexedCollectionModel(refId, parent)
		parentNode.childNodes.each { Node child ->
			if(child.nodeType == Node.COMMENT_NODE){
				// Add comments to Model
				model.addComment(new Comment(child.data))
			}else if(child instanceof Element){
				// Adds the Attributes of the IniValue
				addAttributes(child, model)
				// No child for iniValue
			}
		}
		return model

	}


	def build(ContextModel model){

		StringWriter writer = new StringWriter()
		MarkupBuilder builder = new MarkupBuilder(writer)

		builder."$ROOT_BUILD_TAG"{
			mkp.yield '\n'

			// add comments
			buildComments(delegate, model)

			context(model.obtainAttributes()){
				mkp.yield '\n'

				model.refKColls.each {RefKCollModel refKCollModel ->
					buildComments(delegate, refKCollModel)
					refKColl(refKCollModel.obtainAttributes()){
						refKCollModel.iniValues.each { IniValueModel iniValueModel ->
							buildComments(delegate, iniValueModel)
							iniValue(iniValueModel.obtainAttributes())
						}
						// add unknown elements at the end
						buildProcessedUnknownElements(delegate, refKCollModel)
					}
				}
				model.refServices.each {RefServiceModel refServiceModel ->
					buildComments(delegate, refServiceModel)
					refService(refServiceModel.obtainAttributes())
				}
				// add unknown elements at the end
				buildProcessedUnknownElements(delegate, model)
			}
			buildChildKIColls(delegate, model.refKColls)
		}
		return removeRootNode(writer.toString())
	}

	def buildChildKIColls(def parent, def refKColls){

		refKColls.each { RefKCollModel refKColl ->
			List kiColls = obtainAllKCollsAndIColls(refKColl.rootKColl)

			kiColls.each { DataElementModel model ->
				if(model instanceof KeyedCollectionModel){
					buildKColl(parent, model)
				}else if(model instanceof IndexedCollectionModel){
					buildIColl(parent, model)
				}
			}
		}
	}

	def buildKColl(def parent, def model){
		buildComments(parent, model)
		parent.kColl(model.obtainAttributes()){
			model.children.each{ DataElementModel child ->
				if(child instanceof FieldModel){
					buildField(parent, child)
				}else if(child instanceof RefDataModel){
					buildRefData(parent, child)
				}else if(child instanceof IndexedCollectionModel || child instanceof KeyedCollectionModel){
					buildChildRefData(parent, child)
				}
			}
			// add unknown elements at the end
			buildProcessedUnknownElements(parent, model)
		}
	}

	def buildIColl(def parent, def model){
		buildComments(parent, model)
		parent.iColl(model.obtainAttributes()){
			model.children.each{ DataElementModel child ->
				if(child instanceof FieldModel){
					buildField(parent, child)
				}else if(child instanceof RefDataModel){
					buildRefData(parent, child)
				}else if(child instanceof IndexedCollectionModel || child instanceof KeyedCollectionModel){
					buildChildRefData(parent, child)
				}
			}
			// add unknown elements at the end
			buildProcessedUnknownElements(parent, model)
		}
	}

	def buildField(def parent, def model){
		buildComments(parent, model)
		parent.field(model.obtainAttributes())
	}

	def buildChildRefData(def parent, def model){
		buildComments(parent, model)
		parent.refData('refId':model.id)
	}
	def buildRefData(def parent, def model){
		buildComments(parent, model)
		parent.refData(model.obtainAttributes())
	}

	def obtainAllKCollsAndIColls(KeyedCollectionModel root){
		List kiColls = []

		// add the parent to the list
		kiColls.add(root)
		// add all the indepth child KColls and IColls to the list
		kiColls = obtainChildKIColls(root, kiColls)

		// filter the obtained results. KColls and IColls must be unique
		List filteredKIColls = []
		Set tempSet = []
		kiColls.each { DataElementModel child ->
			if(!tempSet.contains(child.id)){
				filteredKIColls.add(child)
				tempSet.add(child.id)
			}
		}
		println 'OBTAIN KCOLLS FILTERED LIST ------------'
		filteredKIColls.each { println "ID:${it.id} ////" }
		println 'OBTAIN KCOLLS FILTERED LIST ------------'
		return filteredKIColls
	}

	def obtainChildKIColls(DataElementModel root, List kiColls){
		root.getChildren().each{ DataElementModel child->
			if(child instanceof KeyedCollectionModel || child instanceof IndexedCollectionModel){
				// recursively search indepth and add all Kcolls and Icolls to the list
				obtainChildKIColls(child, kiColls)
				// adds the parent Kcoll or Icoll to list
				kiColls.add(child)
			}
		}
		return kiColls
	}

}
