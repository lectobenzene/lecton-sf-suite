package com.tcs.mobility.sf.lecton.jumper.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import com.tcs.mobility.sf.lecton.jumper.hyperlinks.AbstractHyperlink;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class AnalyzeSpringBinder extends AbstractHandler {

	private static final String MARKER_SERVICE_DECLARATION = "com.tcs.mobility.sf.lecton.jumper.servicedeclarationmarker";
	private static final String ANNOTATION_MODULE = "@Module";

	private IProject project;

	/** All the services declared in the spring xml files */
	private List<String> servicesDeclared;

	/** Pattern for '@Service' tag */
	private Pattern patternService;
	
	/** Pattern for 'service-ref' tag */
	private Pattern patternReverseService;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		patternService = Pattern.compile(AbstractHyperlink.SF_SERVICE);
		patternReverseService = Pattern.compile(AbstractHyperlink.SF_REVERSE_SERVICE);

		servicesDeclared = new ArrayList<String>();

		// Get project from the active view (package explorer)
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IJavaElement) {
				project = ((IJavaElement) firstElement).getJavaProject().getProject();
			}
		}
		
		// Location of the Spring xml files
		IFolder folder = project.getFolder(AbstractHyperlink.LOCATION_SPRING_FILES);

		// Get the list of service names provided in the Spring XML files
		try {
			for (IResource iResource : folder.members()) {
				if (iResource instanceof IFile && iResource.getName().endsWith(".xml")) {

					// Get the document content
					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
					bufferManager.connect(iResource.getFullPath(), LocationKind.IFILE, null);
					ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(iResource.getFullPath(), LocationKind.IFILE);
					IDocument document = textFileBuffer.getDocument();

					String matchLine = document.get();
					Matcher matcher = patternReverseService.matcher(matchLine);

					// Get the list of services
					while (matcher.find()) {
						servicesDeclared.add(matcher.group(1));
					}

					// Dispose the buffers
					textFileBuffer = null;
					bufferManager.disconnect(iResource.getFullPath(), LocationKind.IFILE, null);
				}
			}
		} catch (CoreException e) {
			WSConsole.e(e);
		}
		
		// Logging
		WSConsole.i("Service list from XML files obtained");
		WSConsole.d("Service list from XML files :");
		for (String service : servicesDeclared) {
			WSConsole.d("Service : "+service);
		}
		
		// Iterate through all the java classes that has a "@Service" tag and
		// check if the service name is
		// contained in the array list of service names obtained.
		folder = project.getFolder(AbstractHyperlink.LOCATION_JAVA_FILES);
		checkSpringBinding(folder);

		return null;
	}

	/**
	 * Get the file containing the Service declaration
	 * 
	 * @param folder
	 *            The folder to search recursively
	 */
	private void checkSpringBinding(IFolder folder) {
		try {
			for (IResource iResource : folder.members()) {
				if (iResource instanceof IFile && iResource.getName().endsWith(".java")) {

					// Get document content
					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
					bufferManager.connect(iResource.getFullPath(), LocationKind.IFILE, null);
					ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(iResource.getFullPath(), LocationKind.IFILE);
					IDocument document = textFileBuffer.getDocument();

					String matchLine = document.get();
					
					/* Check for module tag. Then proceed with searching for the service tag. 
					 * Only java files with module tag should be processed.
					 */
					if (matchLine.contains(ANNOTATION_MODULE)) {

						WSConsole.d("File Name : "+iResource.getName());
						WSConsole.d("@Module tag found");

						Matcher matcher = patternService.matcher(matchLine);
						while (matcher.find()) {
							String serviceName = matcher.group(1);
							System.out.println("Service Name : " + serviceName);

							// Get marker information for the service found
							String textToFind = AbstractHyperlink.SERVICE_REF_JAVA + serviceName + "\")";
							System.out.println("TextToFind" + textToFind);
							int index = matchLine.indexOf(textToFind);

							if (servicesDeclared.contains(serviceName)) {
								System.out.println("Service Found");
								// Delete the marker if present
								iResource.deleteMarkers(MARKER_SERVICE_DECLARATION, false, IResource.DEPTH_ZERO);
							} else {
								System.out.println("Service Not Found");
								// create a marker, if not already created
								IMarker[] existingMarkers = iResource.findMarkers(MARKER_SERVICE_DECLARATION, false, IResource.DEPTH_ZERO);
								if (existingMarkers.length == 0) {
									IMarker marker = iResource.createMarker(MARKER_SERVICE_DECLARATION);
									marker.setAttribute(IMarker.TEXT, serviceName);
									marker.setAttribute(IMarker.MESSAGE, "Service Declaration Missing");
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
									// marker.setAttribute(IMarker.LINE_NUMBER,
									// document.getLineOffset(index));
									marker.setAttribute(IMarker.CHAR_START, index + AbstractHyperlink.SERVICE_REF_JAVA.length());
									marker.setAttribute(IMarker.CHAR_END, index + AbstractHyperlink.SERVICE_REF_JAVA.length() + serviceName.length());
								}
							}
						}
					}

					// Dispose the buffers
					textFileBuffer = null;
					bufferManager.disconnect(iResource.getFullPath(), LocationKind.IFILE, null);

				} else if (iResource instanceof IFolder) {
					checkSpringBinding((IFolder) iResource);
				}
			}
		} catch (CoreException e) {
			WSConsole.e(e);
		}
	}

}
