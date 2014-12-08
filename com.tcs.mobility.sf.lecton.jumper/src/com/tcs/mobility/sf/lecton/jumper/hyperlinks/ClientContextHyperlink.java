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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.tcs.mobility.sf.lecton.jumper.models.FileInformation;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class ClientContextHyperlink extends AbstractHyperlink {

	private final IRegion targetRegion;

	/** The path where the file to be opened is located */
	private final String connectorId;

	/** The path of the file from which the hyperlink was clicked */
	private final IPath filePath;

	private final IProject project;

	public ClientContextHyperlink(IRegion targetRegion, String connectorId, IPath filePath, IProject project) {
		this.targetRegion = targetRegion;
		this.project = project;
		this.connectorId = connectorId;
		this.filePath = filePath;
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
		return "Open Declaration";
	}

	@Override
	public void open() {
		System.out.println("ClientContext Hyperlink clicked");

		// File Path
		System.out.println("File Path = " + filePath);

		// Remove the project name segment
		IPath path = filePath.removeFirstSegments(1);

		// Check if the path is valid and a file exists at the path, if so open
		// it.
		IResource file = project.findMember(path);
		if (file != null && file.exists() && file instanceof IFile) {
			try {
				FileInformation info = getFileInformation((IFile) file);
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), (IFile) file);
			} catch (PartInitException e) {
				WSConsole.e(e);
			}
		}

	}

	/**
	 * Get the file containing the Service declaration
	 * 
	 * @param file
	 *            The file to open
	 * @return file information if file is found, else null
	 */
	private FileInformation getFileInformation(IFile file) {
		try {

			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
			bufferManager.connect(file.getFullPath(), LocationKind.IFILE, null);
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			IDocument document = textFileBuffer.getDocument();

			int index = document.get().indexOf(SERVICE_REF_XML + connectorId + "\"");

			// Dispose the buffers
			textFileBuffer = null;
			bufferManager.disconnect(file.getFullPath(), LocationKind.IFILE, null);

			if (index != -1) {
				return new FileInformation(file, index);
			}
		} catch (CoreException e) {
			WSConsole.e(e);
		}
		return null;
	}

}
