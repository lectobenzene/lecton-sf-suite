package com.tcs.mobility.sf.lecton.testcaser.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;

import com.tcs.mobility.sf.lecton.utility.utils.UtilResource;

public class MockClassesPropertyTester extends PropertyTester {

	public static final String PROPERTY_FILE_TYPE = "filetype";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROPERTY_FILE_TYPE.equals(property)) {
			if(receiver instanceof ICompilationUnit){
				// Make it visible only if the expected extension is satisfied. The
				// extension should be 'java'
				if (((String) expectedValue).equals(UtilResource.getFileExtension(((ICompilationUnit)receiver).getElementName()))) {
					return true;
				}
			}else if(receiver instanceof IPackageFragment){
				// show command for an entire package
				return true;
			}
		}
		return false;
	}

}
