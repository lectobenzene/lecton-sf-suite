package com.tcs.mobility.sf.lecton.xml2xsd2java.commands;

import java.beans.Introspector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

import com.tcs.mobility.sf.lecton.xml2xsd2java.utils.Utility;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller.JavaGenerator;

public class GenerateJavaCommand extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPartSite site = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();

		// Get selection from Package Explorer
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection(JavaGenerator.ID_PACKAGE_EXPLORER);
		if (selection instanceof IStructuredSelection) {

			// Get the first element of the selection
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IResource) {

				// Get Project Name
				final IProject project = ((IResource) firstElement).getProject();
				String projectName = project.getName();

				// Get Application Code
				String appCode = JavaGenerator.PROJECT_DEFAULT_CODE;
				if (projectName.startsWith(JavaGenerator.PROJECT_PREFIX)) {
					appCode = projectName.substring(JavaGenerator.PROJECT_PREFIX.length(), JavaGenerator.PROJECT_PREFIX.length() + 4).toLowerCase();
				}

				// Construct the inputs for XsdToJavaGenerator
				String directoryPath = project.getLocation().append(JavaGenerator.PACKAGEROOT_SRC).toOSString();
				String xsdPath = ((IResource) firstElement).getLocation().toOSString();

				// Calculate the last segment of the package name
				String packageEndName =getPackageEndName(firstElement);

				// Construct package name based on SIL or Data Model
				String packagePath = null;
				String commonPackagePath = null;
				
				if (projectName.toLowerCase().contains(JavaGenerator.DOMAIN)) {
					packagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_DOMAIN_MODEL + "." + Introspector.decapitalize(packageEndName);
					commonPackagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_DOMAIN_MODEL + "." + JavaGenerator.PACKAGE_COMMON;
				} else {
					packagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_SIL + "." + Introspector.decapitalize(packageEndName);
					commonPackagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_SIL + "." + JavaGenerator.PACKAGE_COMMON;
				}

				new JavaGenerator().generateJava(project, directoryPath, xsdPath, packagePath, commonPackagePath, site, false);
			}
		}
		return null;
	}


	

	
	/**
	 * Returns that last segment of the package name
	 * 
	 * @param firstElement
	 *            The selection node in package explorer
	 * @return The last segment of the package name
	 */
	private String getPackageEndName(Object firstElement) {
		String xsdName = Utility.getFileNameWithoutExtension(((IResource) firstElement).getName());
		String packageEndName;
		if (xsdName.endsWith(JavaGenerator.XSD_NAME_REQUEST)) {
			packageEndName = xsdName.substring(0, xsdName.indexOf(JavaGenerator.XSD_NAME_REQUEST));
		} else if (xsdName.endsWith(JavaGenerator.XSD_NAME_RESPONSE)) {
			packageEndName = xsdName.substring(0, xsdName.indexOf(JavaGenerator.XSD_NAME_RESPONSE));
		} else {
			packageEndName = xsdName;
		}
		return packageEndName;
	}
}
