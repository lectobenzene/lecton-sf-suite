package com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.elements.RefDataModel
import com.tcs.mobility.sf.lecton.bttsource.models.parent.SuperParentModel


/**
 * Parent class for the Other Data Elements
 * 
 * @author Saravana
 *
 */
abstract class DataElementModel extends SuperParentModel{

	/** denotes the type of dataElement */
	String type

	/** id of the DataElement. Mandatory!*/
	String id

	/** Description of the DataElement. Optional!*/
	String description

	/** Returns the compound id of the element */
	String obtainCompoundId

	/** Denotes if this the root element */
	boolean isRoot = false

	/** DataElement that is the parent this DataElement */
	def parent

	/** Parameter denoting whether the dataElement is modifiable */
	boolean readOnly

	/**
	 * Returns the list of children contained within the data element 
	 * 
	 * @return list of all the children
	 */
	abstract List getChildren()

	/**
	 * Returns the JavaStatement of the form 'private DATA_TYPE object' (without the ending semicolon)
	 * @return a Java Statement
	 */
	abstract String getJavaFieldStatement()

	/**
	 * Converts id of type 'user-settings-info' into 'userSettingsInfo'
	 * This method is used to format the id into Java Object name convention
	 *
	 * @return Formatted id
	 */
	public String getDataModelObjectName(){
		def lowerCasedId = id.toLowerCase()
		String[] idPieces = lowerCasedId.split('-')
		def joiner = new StringWriter()
		// Capitalizes the first char of word and appends it to the StringWriter
		idPieces.each { word->
			word = word.replaceFirst(word[0], word[0].toUpperCase())
			joiner.append(word)
		}
		// Make the first char of the word into lowerCase
		def joinerString = joiner.toString()
		return joinerString.replaceFirst(joinerString[0], joinerString[0].toLowerCase())
	}

	/**
	 * Converts id of type 'user-settings-info' into 'UserSettingsInfo'
	 * This method is used to format the id into Java Annotation name convention
	 *
	 * @return Formatted id
	 */
	public String getDataModelAnnotationName(){
		def lowerCasedId = id
		String[] idPieces = lowerCasedId.split('-')
		def joiner = new StringWriter()
		// Capitalizes the first char of word and appends it to the StringWriter
		idPieces.each { word->
			word = word.replaceFirst(word[0], word[0].toUpperCase())
			joiner.append(word)
		}
		return joiner.toString()
	}

	def getCompositeId(){
		String id = obtainCompoundId(this)
		if(id.startsWith('.')){
			id = id.substring(1)
		}
		return id
	}

	def obtainCompoundId(DataElementModel element){
		String id

		// if element is null, then empty
		if(!element){
			return ""
		}else{
			// if element is the root, them empty
			if(element.isRoot){
				return ""
			}else{
				DataElementModel parent = element.getParent()
				/*
				 * The elements are created in such a way that all RefData tagged elements have
				 * RefDataModel as their parents, and their parents happens to be either an iColl
				 * or kColl. Hence, if parent is a RefDataModel, its parent is considered for calculation
				 */
				/*
				 * This is a mistake in the ContextParser.createDataElements method. This was corrected there.
				 * So, this code is no longer required. Remove this in the future when Context is stabilized.
				 */
				//if(parent instanceof RefDataModel){
				//parent = parent.getParent()
				//}
				id = obtainCompoundId(parent)
				String tempId
				// If refData then use refId, else id
				if(element instanceof RefDataModel){
					tempId =  element.refId
				}else{
					tempId = element.id
				}
				id = id + "." +tempId
			}
		}
		return id
	}



	def obtainAttributes(Set props){
		if(!props){
			props = []
		}
		props.addAll([
			'type',
			'parent',
			'readOnly',
			'dataModelObjectName',
			'children',
			'dataModelAnnotationName',
			'javaFieldStatement',
			'compositeId',
			'isRoot'
		])
		super.obtainAttributes(props)
	}
}
