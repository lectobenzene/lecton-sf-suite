package com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes;

import java.util.Random;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ExpressionStatement;

public abstract class PrimaryDataType {

	/**
	 * Returns the statement of the method for the given object
	 * 
	 * @param ast
	 * @param objectName
	 *            the name of the object for which the method has to be created
	 * @param fieldName
	 *            the name of the field
	 * @return the ExpressionStatement
	 */
	public abstract ExpressionStatement getExpressionStatement(AST ast, String objectName, String fieldName);

	/**
	 * returns a random value from the array
	 * 
	 * @param array
	 * @return random value for the array
	 */
	protected Object getRandomValue(Object[] array) {
		Random random = new Random();
		int index = random.nextInt(array.length);
		return array[index];
	}
}
