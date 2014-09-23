package com.tcs.mobility.sf.lecton.bttsource.files.xml

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

class SkeletonService {

	def String serviceName
	def String serviceDescription
	def String processorPackageName
	def String processorName
	def String contextPackageName
	def String contextName

	public SkeletonService() {
	}

	static void main(def args){
		SkeletonService service = new SkeletonService()
		service.setServiceName("GetUserInfoOp")
		service.setServiceDescription("Get the user service info for various purpose that doesn't get anything that you want when you want in any way that you like to have. It is the greatest thingt that this service can do. Get the user service info for various purpose that doesn't get anything that you want when you want in any way that you like to have. It is the greatest thingt that this service can do. Get the user service info for various purpose that doesn't get anything that you want when you want in any way that you like to have. It is the greatest thingt that this service can do.")
		service.setProcessorName("GetUserInfoOpProcessor")
		service.setProcessorPackageName("com.fortis.be.tfal.operations.automation")
		service.setContextName("GetUserInfoOpContext")
		service.setContextPackageName("com.fortis.be.tfal.operations.context")
		print service.createSkeletonService()

		//println service.createDseIniWebServiceEntry("GetHelloWorldOp", "DBAL-ap01/server/accounts/GetAccountListOp.xml", "%com.fortis.be.dbal.jarPath%")
		//println service.createDseIniWebServiceEntry("GetHelloWorldOp", "GetHistoryOp.xml", "dbal/movement")
	}

	public String createSkeletonService(){
		def writer = new StringWriter()
		def creator = new WSCreator()
		def builder = new MarkupBuilder(writer)

		def flowId = "automatonFlow"
		def contextId = "${serviceName}Ctx"
		def dataId = "${serviceName}Data"
		def xmlResponseFormatId = "${serviceName}XMLResponseFmt"


		if(!(serviceName && processorName && processorPackageName && contextName && contextPackageName)){
			return null
		}


		builder."${serviceName}.xml"{
			if(serviceDescription){
				mkp.comment "${serviceDescription}"
			}
			mkp.comment 'PROCESSOR'
			processor(implClass:"${processorPackageName}.${processorName}",
			id:"${serviceName}",
			refFlow:"${flowId}",
			operationContext:"${contextId}"){
				mkp.comment 'Mention are Formatters used in the Service here'
				refFormat(name:'xmlResponseFormat',
				refId:"$xmlResponseFormatId")
			}

			mkp.yield '\n'

			flow(id:"$flowId"){
				mkp.comment 'Add new States here'
				state(id:'finalOk', type:'final', typeIdInfo:'ok')
				state(id:'finalError', type:'final', typeIdInfo:'error')
			}

			mkp.yield '\n'

			context(id:"$contextId",
			type:'op',
			implClass:"${contextPackageName}.${contextName}"){
				refKColl(refId:"$dataId")
			}

			mkp.yield '\n'

			kColl(id:"$dataId"){
				mkp.comment 'Add new Fields here'
				refData(refId:'CommonJDBCData')
				refData(refId:'CommonJournalData')
				refData(refId:'SEWI-LIST')
				field(id:'xml-response-encoding', value:'ISO-8859-1')
				field(id:'global-error-indicator')
				field(id:'was-return-code')
				field(id:'text')
			}

			creator.addFormatter(delegate, xmlResponseFormatId,'XML Output Formatter')
		}

		return XmlUtil.serialize(writer.toString())
	}

	public byte[] getJavaContent(String packageName, String importStatement, String className, String superClassName) {

		def javaContent = """package ${packageName};

import ${importStatement};

public class ${className} extends ${superClassName} {

		private static final long serialVersionUID = 1L;

}

"""
		return javaContent.getBytes()
	}

	public String createDseIniWebServiceEntry(String id, String value, String path){
		def writer = new StringWriter()
		def builder = new MarkupBuilder(writer)
		builder.setDoubleQuotes(true)

		builder.procDef(id:"$id",value:"$value",path:"$path")
		return writer.toString()
	}
}
