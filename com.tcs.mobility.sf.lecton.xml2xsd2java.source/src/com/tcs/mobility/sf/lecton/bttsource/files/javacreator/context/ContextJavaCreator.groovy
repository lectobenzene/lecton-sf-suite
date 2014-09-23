package com.tcs.mobility.sf.lecton.bttsource.files.javacreator.context

import java.util.regex.Pattern

import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.FieldModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.IndexedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.KeyedCollectionModel
import com.tcs.mobility.sf.lecton.bttsource.models.context.elements.dataelements.parent.DataElementModel

class ContextJavaCreator {

	KeyedCollectionModel root
	String rootPackageName
	String currentPackageName
	String serviceName
	String typeName

	public ContextJavaCreator(root, rootPackageName, serviceName) {
		this.root = root
		this.rootPackageName = rootPackageName
		this.serviceName = serviceName
	}

	static void main(def args){
		KeyedCollectionModel rootKColl = new KeyedCollectionModel("root")
		rootKColl.addField(new FieldModel("first-name"))
		rootKColl.addField(new FieldModel("last-name"))
		FieldModel brachName = new FieldModel("branch-name")
		brachName.dataType = 'int'
		rootKColl.addField(brachName)
		IndexedCollectionModel dailyValuesList = new IndexedCollectionModel("DAILY-VALUES-LIST")
		KeyedCollectionModel dailyValues = new KeyedCollectionModel("DAILY-VALUES")
		dailyValuesList.addElement(dailyValues)
		rootKColl.addIColl(dailyValuesList)
		rootKColl.addKColl(new KeyedCollectionModel("USER-SETTINGS"))
		IndexedCollectionModel accountBalanceList = new IndexedCollectionModel("ACCOUNT-BALANCE-LIST")
		FieldModel accountBalance = new FieldModel("ACCOUNT-BALANCE")
		accountBalance.dataType = "int"
		accountBalanceList.addElement(accountBalance)
		rootKColl.addIColl(accountBalanceList)

		//rootKColl = new KeyedCollectionModel("blank");

		ContextJavaCreator creator = new ContextJavaCreator(rootKColl, "com.fortis.be.ebcing.mib.service", "GetCountryList")
	}

	public String modifyResponseContents(File originalFile){
		def originalContents = originalFile.text
		def matchers = (originalContents =~ /@XmlSeeAlso(?:\s*)\((?:\s*)\{([^}]*)/)
		String matchedString = matchers[0][0]
		String subMatchedString = matchers[0][1]

		if(subMatchedString.trim()){
			subMatchedString = matchedString.replaceFirst(subMatchedString, "${serviceName}.class,${subMatchedString}")
		}else{
			subMatchedString = matchedString.replaceFirst(subMatchedString, "${serviceName}.class")
		}
		return originalContents.replaceFirst(Pattern.quote(matchedString), subMatchedString)
	}

	public String tempTestRun(){
		KeyedCollectionModel rootKColl = new KeyedCollectionModel("root")
		rootKColl.addField(new FieldModel("first-name"))
		rootKColl.addField(new FieldModel("last-name"))
		FieldModel brachName = new FieldModel("branch-name")
		brachName.dataType = 'int'
		rootKColl.addField(brachName)
		IndexedCollectionModel dailyValuesList = new IndexedCollectionModel("DAILY-VALUES-LIST")
		KeyedCollectionModel dailyValues = new KeyedCollectionModel("DAILY-VALUES")
		dailyValuesList.addElement(dailyValues)
		rootKColl.addIColl(dailyValuesList)
		rootKColl.addKColl(new KeyedCollectionModel("USER-SETTINGS"))
		IndexedCollectionModel accountBalanceList = new IndexedCollectionModel("ACCOUNT-BALANCE-LIST")
		FieldModel accountBalance = new FieldModel("ACCOUNT-BALANCE")
		accountBalance.dataType = "int"
		accountBalanceList.addElement(accountBalance)
		rootKColl.addIColl(accountBalanceList)

		ContextJavaCreator creator = new ContextJavaCreator(rootKColl, rootPackageName, serviceName)
		return creator.createInClass()
	}

	public String createClass(def type){
		switch(type){
			case 'in':
				typeName = "${serviceName}Request"
				return createInClass()

			case 'out':
				typeName = serviceName
				return createOutClass()

			case 'commons':
				typeName = root.getDataModelAnnotationName()
				return createOutClass()
				
			case 'module':
				typeName = "${serviceName}Module"
				return createModuleClass()
		}
	}

	public String createInClass(){
		def defaultImportStatements = getDefaultImportStatements()
		def defaultAnnotations = getDefaultAnnotations('in')
		def xmlTypeAnnotation = getXMLTypeAnnotation()
		def classTitleLine = "public class ${typeName}"
		def serialVersionUID = "private static final long serialVersionUID = 1L;"
		def getFieldElements = createFieldElements(root)
		def contents =
				"""package ${currentPackageName};\n\n
${defaultImportStatements}\n\n
${defaultAnnotations}
${xmlTypeAnnotation}
${classTitleLine} extends AbstractRequest {\n
	${serialVersionUID}\n
	
${getFieldElements}\n
}
"""

		return contents
	}

	public String createOutClass(){
		def defaultImportStatements = getDefaultImportStatements()
		def defaultAnnotations = getDefaultAnnotations('out')
		def xmlTypeAnnotation = getXMLTypeAnnotation()
		def classTitleLine = "public class ${typeName}"
		def serialVersionUID = "private static final long serialVersionUID = 1L;"
		def getFieldElements = createFieldElements(root)
		def contents =
				"""package ${currentPackageName};\n\n
${defaultImportStatements}\n\n
${defaultAnnotations}
${xmlTypeAnnotation}
${classTitleLine} implements Serializable, Cloneable {\n
	${serialVersionUID}\n
	
${getFieldElements}\n
}
"""

		return contents
	}

	public String createModuleClass(){
		def defaultImportStatements = getDefaultImportStatements()
		def classTitleLine = "public interface ${typeName}"
		def getFieldElements = "Response<${serviceName}> ${getLowerCased(serviceName)}(final ${serviceName}Request ${getLowerCased(serviceName+'Request')});"
		def contents =
				"""package ${currentPackageName};\n\n
${defaultImportStatements}\n\n
${classTitleLine} {\n
${getFieldElements}\n
}
"""

		return contents
	}

	private String getLowerCased(String word){
		return word.replaceFirst(word[0], word[0].toLowerCase())
	}


	private String createFieldElements(DataElementModel parentElement){
		StringWriter contents = new StringWriter()
		StringWriter gettersAndSetters = new StringWriter()

		parentElement.children.each { child->
			def javaStatement = child.getJavaFieldStatement()
			if(child instanceof FieldModel || child instanceof KeyedCollectionModel){
				contents.append('\t').append("@XmlElement(name = \"${child.getDataModelAnnotationName()}\")").append('\n')
			}else if(child instanceof IndexedCollectionModel){
				def iCollChild = child.getElement()
				contents.append('\t').append("@XmlElementWrapper(name = \"${child.getDataModelAnnotationName()}\" )").append('\n')
				contents.append('\t').append("@XmlElement(name = \"${iCollChild.getDataModelAnnotationName()}\")").append('\n')
			}
			contents.append('\t').append("${javaStatement};").append('\n\n')
			gettersAndSetters.append(generateGettersAndSetters(javaStatement))
		}
		return contents.toString() +"\n\n"+ gettersAndSetters.toString()
	}

	/**
	 * Returns a String that contains the Getter and Setter for the given JavaStatement
	 * 
	 * @param javaStatement The input string that happens to be a Java field declaration
	 * 
	 * @return String that represents the Getter and Setter
	 */
	private String generateGettersAndSetters(String javaStatement){
		StringWriter gettersAndSetters = new StringWriter()
		// Splits based on SPACE char
		String[] javaPiece = javaStatement.split(' ')
		String dataType = javaPiece[1]
		String objectName = javaPiece[2]
		String capObjectName = objectName.replaceFirst(objectName[0], objectName[0].toUpperCase())
		gettersAndSetters.append('\t').append("public ${dataType} get${capObjectName}() {").append('\n\t\t').append("return ${objectName};").append('\n\t').append('}').append('\n\n')
		gettersAndSetters.append('\t').append("public void set${capObjectName}(${dataType} ${objectName}) {").append('\n\t\t').append("this.${objectName} = ${objectName};").append('\n\t').append('}').append('\n\n')
		return gettersAndSetters.toString()
	}

	private String getXMLTypeAnnotation(){
		def propOrder = new StringBuilder()
		def rootChildren = root.getChildren()
		if(rootChildren){
			propOrder.append(", propOrder = { ")
			rootChildren.each { child ->
				propOrder.append("\"${child.getDataModelObjectName()}\",")
			}
			// Removes the last Comma
			propOrder.deleteCharAt(propOrder.size()-1)
			propOrder.append(" }")
		}

		def contents = "@XmlType(name = \"${typeName}\", namespace = \"http://www.bnpp.com/jdf/service\"${propOrder})"
		return contents
	}

	private String getDefaultAnnotations(def scenario){
		StringWriter defaultAnnotations = new StringWriter()
		switch(scenario){
			case 'in':
				defaultAnnotations.append('@XmlAccessorType(XmlAccessType.FIELD)\n')
				break

			case 'out':
				defaultAnnotations.append('@XmlRootElement\n')
				defaultAnnotations.append('@XmlAccessorType(XmlAccessType.FIELD)\n')
				break

			default:
				break
		}
		return defaultAnnotations.toString()
	}


	private String getDefaultImportStatements(){
		StringWriter importStatements = new StringWriter()
		importStatements.append('import javax.xml.bind.annotation.XmlAccessType;\n')
		importStatements.append('import javax.xml.bind.annotation.XmlAccessorType;\n')
		importStatements.append('import javax.xml.bind.annotation.XmlType;\n')
		importStatements.append('import javax.xml.bind.annotation.XmlElement;\n')
		importStatements.append('import javax.xml.bind.annotation.XmlElementWrapper;\n')
		importStatements.append('import javax.xml.bind.annotation.XmlRootElement;\n')
		importStatements.append('import java.util.List;\n')
		importStatements.append('import java.util.Date;\n')
		importStatements.append('import java.math.BigDecimal;\n')
		importStatements.append('import java.io.Serializable;\n')
		importStatements.append("import ${rootPackageName}.AbstractRequest;")
		importStatements.append("import ${rootPackageName}.Response;")
		importStatements.append("import ${rootPackageName}.${serviceName}.common.${typeName};")
		println "import ${rootPackageName}.${serviceName}.common.${typeName};"
		return importStatements.toString()
	}

	private String getPackageName(def lastElement){
		return "${rootPackageName}"
		//		return "${rootPackageName}.${serviceName}.${lastElement}"
	}
}
