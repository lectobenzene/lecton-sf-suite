package com.tcs.mobility.sf.lecton.jumper.hyperlinks;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.tcs.mobility.sf.lecton.jumper.models.FileInformation;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class ReverseServiceHyperlink extends AbstractHyperlink {

	private final IRegion targetRegion;
	private final String serviceName;
	private final IProject project;

	public ReverseServiceHyperlink(IRegion region, String serviceName, IProject project) {
		targetRegion = region;
		this.project = project;
		this.serviceName = serviceName;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return targetRegion;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return "Open Service Implementation";
	}

	@Override
	public void open() {
		System.out.println("Service Reverse Hyperlink Clicked");

		IFolder folder = project.getFolder(LOCATION_JAVA_FILES);
		FileInformation info = getFileInformation(folder);
		if (info != null) {
			openFileInEditor(info.getFile(), info.getOffset(), SERVICE_REF_JAVA, serviceName);
		}
	}

	/**
	 * Get the file containing the Service declaration
	 * 
	 * @param folder
	 *            The folder to search recursively
	 * @return file information if file is found, else null
	 */
	private FileInformation getFileInformation(IFolder folder) {
		try {
			for (IResource iResource : folder.members()) {
				if (iResource instanceof IFile && iResource.getName().endsWith(".java")) {
					System.out.println("NAME : " + iResource.getName());

					ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
					bufferManager.connect(iResource.getFullPath(), LocationKind.IFILE, null);
					ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(iResource.getFullPath(), LocationKind.IFILE);
					IDocument document = textFileBuffer.getDocument();

					int index = document.get().indexOf(AbstractHyperlink.SERVICE_REF_JAVA + serviceName + "\")");
					System.out.println(SERVICE_REF_JAVA + serviceName + "\")");

					// Dispose the buffers
					textFileBuffer = null;
					bufferManager.disconnect(iResource.getFullPath(), LocationKind.IFILE, null);

					if (index != -1) {
						return new FileInformation((IFile) iResource, index);
					}
				} else if (iResource instanceof IFolder) {
					FileInformation info = getFileInformation((IFolder) iResource);
					if (info != null) {
						return info;
					}
				}
			}
		} catch (CoreException e) {
			WSConsole.e(e);
		}
		return null;
	}
}
