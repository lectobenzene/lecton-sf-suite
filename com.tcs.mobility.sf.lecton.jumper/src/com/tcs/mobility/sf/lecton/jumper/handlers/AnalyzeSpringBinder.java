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
import com.tcs.mobility.sf.lecton.jumper.models.FileInformation;

public class AnalyzeSpringBinder extends AbstractHandler {

	private IProject project;
	
	private List<String> servicesDeclared;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Expecting analyze spring binding...");
		
		Pattern patternService = Pattern.compile(AbstractHyperlink.SF_SERVICE);
		Pattern patternReverseService = Pattern.compile(AbstractHyperlink.SF_REVERSE_SERVICE);
		
		servicesDeclared = new ArrayList<String>();
		
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if(selection instanceof IStructuredSelection){
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IJavaElement) {
				project = ((IJavaElement) firstElement).getJavaProject().getProject();
			}
		}
		IFolder folder = project.getFolder(AbstractHyperlink.LOCATION_SPRING_FILES);
		
		// Get the list of service names provided in the Spring XML files
		try {
			for (IResource iResource : folder.members()) {
				if (iResource instanceof IFile && iResource.getName().endsWith(".xml")) {

					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
					bufferManager.connect(iResource.getFullPath(), LocationKind.IFILE, null);
					ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(iResource.getFullPath(), LocationKind.IFILE);
					IDocument document = textFileBuffer.getDocument();

					String matchLine = document.get();

					Matcher matcher = patternReverseService.matcher(matchLine);
					
					while(matcher.find()){
						servicesDeclared.add(matcher.group(1));
					}
					
					// Dispose the buffers
					textFileBuffer = null;
					bufferManager.disconnect(iResource.getFullPath(), LocationKind.IFILE, null);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(servicesDeclared);
		
		// Iterate through all the java classes that has a "@Service" tag and check if the service name is
		// contained in the array list of service names obtained.
		
		folder = project.getFolder(AbstractHyperlink.LOCATION_JAVA_FILES);
		
		return null;
	}

	
	/**
	 * Get the file containing the Service declaration
	 * 
	 * @param folder
	 *            The folder to search recursively
	 */
	private void getFileInformation(IFolder folder) {
		try {
			for (IResource iResource : folder.members()) {
				if (iResource instanceof IFile && iResource.getName().endsWith(".java")) {
					System.out.println("NAME : " + iResource.getName());

					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
					bufferManager.connect(iResource.getFullPath(), LocationKind.IFILE, null);
					ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(iResource.getFullPath(), LocationKind.IFILE);
					IDocument document = textFileBuffer.getDocument();

					// Dispose the buffers
					textFileBuffer = null;
					bufferManager.disconnect(iResource.getFullPath(), LocationKind.IFILE, null);

				} else if (iResource instanceof IFolder) {
					getFileInformation((IFolder) iResource);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
