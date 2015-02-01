package com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.tcs.mobility.sf.lecton.utility.utils.UtilText;

public class BooleanDataType extends PrimaryDataType {

	private static Boolean[] booleanArray = {true, false};
	
	@Override
	public ExpressionStatement getExpressionStatement(AST ast, String objectName, String fieldName) {
		MethodInvocation methodInvocation = ast.newMethodInvocation();

		methodInvocation.setExpression(ast.newSimpleName(objectName));
		methodInvocation.setName(ast.newSimpleName("set" + UtilText.getFirstUpperName(fieldName)));

		BooleanLiteral literal = ast.newBooleanLiteral((Boolean)getRandomValue(booleanArray));

		methodInvocation.arguments().add(literal);

		ExpressionStatement expressionStatement = ast.newExpressionStatement(methodInvocation);
		return expressionStatement;
	}

}
