package com.tcs.mobility.sf.lecton.bttsource.parsers.utils

import org.w3c.dom.Element
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.Node


class NodeUtil {
	/**
	 * Used only by PROCESSOR PARSER. Do not modify.
	 *
	 * @param root
	 * @return
	 */
	def getCommentCombinedNodeList(Element root){
		NodeList children = root.childNodes

		Element commentedElement = null

		def rootNodeList = []
		children.each {
			if(!commentedElement){
				commentedElement = createNode("root", root.ownerDocument)
			}
			Node node = it.cloneNode(true)
			if(it.nodeType == Node.COMMENT_NODE){
				commentedElement.appendChild(node)
			} else if(it instanceof Element){
				commentedElement.appendChild(node)
				rootNodeList.add(commentedElement)
				// made null, so a new CommentedElement can be created in the next iteration
				commentedElement = null
			}
		}
		return rootNodeList
	}


	def getCommentCombinedNode(Element root){
		NodeList children = root.childNodes
		Document doc = root.ownerDocument
		Element commentedElement = null

		def rootNodeList = []
		children.each {
			if(!commentedElement){
				commentedElement = createNode("root", doc)
			}
			Node node = it.cloneNode(true)
			if(it.nodeType == Node.COMMENT_NODE){
				commentedElement.appendChild(node)
			} else if(it instanceof Element){
				commentedElement.appendChild(node)
				rootNodeList.add(doc.renameNode(commentedElement, null, "comment-${it.getNodeName()}"))
				// made null, so a new CommentedElement can be created in the next iteration
				commentedElement = null
			}
		}
		// This root will be removed in method removeAllChildAndAppend. This is just a temporary root
		def finalRoot = createNode("root", doc)
		rootNodeList.each { 
			Node node = it.cloneNode(true)
			finalRoot.appendChild(node) 	
		}
		return finalRoot
	}

	
	def getFullyCommentCombinedNodeList(Node root){
		NodeList children = root.childNodes

		// Do nothing if there are no children
		if(children.length == 0){
			return
		}else{
			for(Node node in children){
				// Perform for only Element nodes which has children
				if(node.hasChildNodes()){
					if(hasFirstLevelChildren(node)){
						getFullyCommentCombinedNodeList(node)
					}else{
						// Perform operation for all the children
						def child = getCommentCombinedNode(node)
						def newChild = removeAllChildAndAppend(node, child)
						node.getParentNode().replaceChild(newChild, node)
					}
				}
			}
			def tempRoot
			if(root.getParentNode()){
				// Perform operation for the parent node
				tempRoot = getCommentCombinedNode(root)
				def newChild = removeAllChildAndAppend(root, tempRoot)
				root.getParentNode().replaceChild(newChild, root)
			}
			else{
				// For the the root node, the PARENT is null. So handle it this way
				// I am expecting some bug in this. 
				tempRoot = getCommentCombinedNode(root)
				def newChild = removeAllChildAndAppend(root, tempRoot)
				return newChild
			}
		}
		return root
	}
	
	def removeAllChildAndAppend(Node parentNode, Node child){
		parentNode = removeAllNodes(parentNode)
		child.childNodes.each {
			Node node = it.cloneNode(true)
			parentNode.appendChild(node)
		}
		return parentNode
	}

	/**
	 * Removes all child nodes
	 * @param node
	 * @return
	 */
	def removeAllNodes(Node node){
		node.childNodes.each{ Node child ->
			node.removeChild(child)
		}
		if(node.hasChildNodes()){
			removeAllNodes(node)
		}
		return node
	}

	def hasFirstLevelChildren(Node root){
		def hasChildren = false
		NodeList children = root.childNodes
		if(children.length == 0){
			return false
		}
		children.each { Node child ->
			if(child.hasChildNodes()){
				hasChildren = true
			}
		}
		return hasChildren
	}

	def Element createNode(def name, Document doc){
		return doc.createElement(name)
	}
}
