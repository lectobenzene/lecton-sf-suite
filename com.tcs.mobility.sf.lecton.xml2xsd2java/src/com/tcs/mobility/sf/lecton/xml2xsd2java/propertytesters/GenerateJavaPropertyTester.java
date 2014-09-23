package com.tcs.mobility.sf.lecton.xml2xsd2java.propertytesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import com.tcs.mobility.sf.lecton.xml2xsd2java.commands.GenerateJavaCommand;
import com.tcs.mobility.sf.lecton.xml2xsd2java.utils.Utility;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller.JavaGenerator;

public class GenerateJavaPropertyTester extends PropertyTester {

	public static final String PROPERTY_FILE_TYPE = "filetype";
	public static final String PROPERTY_NO_JOBS_PENDING = "nojobspending";

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		System.out.println("Something Happening");
		if (PROPERTY_FILE_TYPE.equals(property)) {
			// Get the file
			final IFile file = (IFile) receiver;
			// Make it visible only if the expected extension is satisfied. The extension should be 'xsd'
			if (((String)expectedValue).equals(Utility.getFileExtension(file.getName()))) {
				System.out.println("VISIBLE");
				return true;
			}
		}
		
		if(PROPERTY_NO_JOBS_PENDING.equals(property)){
			IJobManager jobManager = Job.getJobManager();
			if(jobManager.find(JavaGenerator.JOB_FAMILY_GENERATE_JAVA).length == 0){
				System.out.println("NO JOBS");
				return true;
			}
		}
		return false;
	}
}
