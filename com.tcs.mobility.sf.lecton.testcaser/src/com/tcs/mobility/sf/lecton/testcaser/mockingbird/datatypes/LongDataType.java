package com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;

import com.tcs.mobility.sf.lecton.utility.utils.UtilText;

public class LongDataType extends PrimaryDataType {

	@Override
	public ExpressionStatement getExpressionStatement(AST ast, String objectName, String fieldName) {
		MethodInvocation methodInvocation = ast.newMethodInvocation();

		methodInvocation.setExpression(ast.newSimpleName(objectName));
		methodInvocation.setName(ast.newSimpleName("set" + UtilText.getFirstUpperName(fieldName)));

		NumberLiteral literal = ast.newNumberLiteral("12345L");
		
		methodInvocation.arguments().add(literal);

		ExpressionStatement expressionStatement = ast.newExpressionStatement(methodInvocation);
		return expressionStatement;
	}

}
