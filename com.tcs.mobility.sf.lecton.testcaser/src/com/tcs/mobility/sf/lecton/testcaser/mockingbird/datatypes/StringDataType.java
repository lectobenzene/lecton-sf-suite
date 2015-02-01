package com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes;

import java.util.Random;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;

import com.tcs.mobility.sf.lecton.utility.utils.UtilText;

public class StringDataType extends PrimaryDataType {
	
	private static String[] firstNames = {"James", "William", "Jack", "Bernard", "Carl"};
	private static String[] lastNames = {"Watson", "Turner", "Nicolson", "Shaw", "Marx"};
	private static String[] languages = {"EN","FR","DE","NL"};
	private static String[] bics = {"BIC123456789","BIC3453445445","BIC8765445321","BIC8756382947"};
	private static String[] ibans = {"BE122342998788","BE490039884746","BE99845223433","BE009334438833"};
	private static String[] phoneNumbers = {"0489263484","0489263967","0489456484"};
	private static String[] userids = {"E1234567","E2345223","E8988767"};
	
	
	@Override
	public ExpressionStatement getExpressionStatement(AST ast, String objectName, String fieldName) {

		MethodInvocation methodInvocation = ast.newMethodInvocation();

		methodInvocation.setExpression(ast.newSimpleName(objectName));
		methodInvocation.setName(ast.newSimpleName("set" + UtilText.getFirstUpperName(fieldName)));

		StringLiteral literal = ast.newStringLiteral();
		literal.setLiteralValue(getStringLiteral(fieldName.toLowerCase()));

		methodInvocation.arguments().add(literal);

		ExpressionStatement expressionStatement = ast.newExpressionStatement(methodInvocation);
		return expressionStatement;
	}

	private String getStringLiteral(String fieldName) {
		// Name
		if(fieldName.contains("name")){
			if(fieldName.contains("first")){ // First Name
				return (String)getRandomValue(firstNames);
			}else if(fieldName.contains("last")){ // Last Name
				return (String)getRandomValue(lastNames);
			}else{ // Full Name
				return (String)getRandomValue(firstNames)+" "+(String)getRandomValue(lastNames);
			}
		}else if(fieldName.contains("language")){
			return (String)getRandomValue(languages);
		}else if(fieldName.contains("bic")){
			return (String)getRandomValue(bics);
		}else if(fieldName.contains("iban")){
			return (String)getRandomValue(ibans);
		}else if(fieldName.contains("number")){
			if(fieldName.contains("phone")){
				return (String)getRandomValue(phoneNumbers);
			}else{
				return "123456789";
			}
		}else if(fieldName.contains("userid")){
			return (String)getRandomValue(userids);			
		}
		return "UNKNOWN";
	}

}
