package com.tcs.mobility.sf.lecton.testcaser.mockingbird.core;

import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.BooleanDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.DoubleDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.FloatDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.IntDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.LongDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.NullDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.PrimaryDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.StringDataType;

public class ParentProcessor {

	/**
	 * Factory method to create proper dataType objects
	 * 
	 * @param input
	 *            data type of the field
	 * @return PrimaryDataType objects
	 */
	protected static PrimaryDataType getDataTypeFactory(String input) {
		if (input.equalsIgnoreCase("String")) {
			return new StringDataType();
		} else if (input.equalsIgnoreCase("boolean")) {
			return new BooleanDataType();
		} else if (input.equalsIgnoreCase("int")) {
			return new IntDataType();
		} else if (input.equalsIgnoreCase("long")) {
			return new LongDataType();
		} else if (input.equalsIgnoreCase("float")) {
			return new FloatDataType();
		} else if (input.equalsIgnoreCase("double")) {
			return new DoubleDataType();
		}

		// If the input dataType is unknown, then assign the value as null
		return new NullDataType();
	}
}
