package com.tcs.mobility.sf.lecton.bttsource.files.xml

import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

class WSCreator {

	public WSCreator() {
	}

	static void main(def args){
		WSCreator creator = new WSCreator()
		creator.createSkeletonService()
	}

	private createSkeletonService(){
		def writer = new StringWriter()
		def builder = new MarkupBuilder(writer)


		def serviceName = "GetUserInfoOp"
		def serviceDescription = "Get the user service info for various purpose that doesn't get anything that you want when you want in any way that you like to have. It is the greatest thingt that this service can do. Get the user service info for various purpose that doesn't get anything that you want when you want in any way that you like to have. It is the greatest thingt that this service can do. Get the user service info for various purpose that doesn't get anything that you want when you want in any way that you like to have. It is the greatest thingt that this service can do."


		def processorPackageName = "com.fortis.be.tfal.operations.automation"
		def processorName = "GetUserInfoOpProcessor"

		def flowId = "automatonFlow"
		def contextId = "${serviceName}Ctx"
		def dataId = "${serviceName}Data"

		def xmlResponseFormatId = "${serviceName}XMLResponseFmt"

		def contextName = "GetUserInfoOpContext"
		def contextPackageName = "com.fortis.be.tfal.operations.context"


		builder."${serviceName}.xml"{
			mkp.comment "${serviceDescription}"
			mkp.yield '\n\n'
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

			flow(id:"$flowId"){ mkp.comment 'Add new States here' 
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

			kColl(id:"$dataId"){ mkp.comment 'Add new Fields here' 
				refData(refId:'CommonJDBCData')
				refData(refId:'CommonJournalData')
				refData(refId:'SEWI-LIST')
				field(id:'xml-response-encoding', value:'ISO-8859-1')
				field(id:'global-error-indicator')
				field(id:'was-return-code')
				field(id:'text')
				
			}

			addFormatter(delegate, xmlResponseFormatId,'XML Output Formatter')
		}

		println XmlUtil.serialize(writer.toString())
	}

	private addNewLine(parent){
		parent.mkp.yield '\n\n'
	}



	public addFormatter(parent, fmtId, comment){
		addNewLine(parent)
		if(comment){
			parent.mkp.comment "$comment"
		}
		parent.fmtDef(id:"$fmtId"){
			mkp.comment 'Add fields here'
			recXML(show:"false"){
				fieldXML(dataName:"global-error-indicator")
				fieldXML(dataName:"was-return-code")
				fieldXML(dataName:"text")
				
			}
		}
	}
}
