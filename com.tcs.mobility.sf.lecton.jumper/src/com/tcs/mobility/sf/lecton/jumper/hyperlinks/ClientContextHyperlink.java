package com.tcs.mobility.sf.lecton.jumper.hyperlinks;

import java.io.File;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.tcs.mobility.sf.lecton.jumper.detectors.HyperlinkDetectorsClientContext;
import com.tcs.mobility.sf.lecton.jumper.models.FileInformation;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class ClientContextHyperlink extends AbstractHyperlink {

	private final IRegion targetRegion;

	/** The path where the file to be opened is located */
	private final String tag;

	/** The path of the file from which the hyperlink was clicked */
	private final IPath filePath;

	private final IProject project;

	private final int hyperlinkType;
	
	public ClientContextHyperlink(IRegion targetRegion, String tag, IPath filePath, IProject project, int hyperlinkType) {
		this.targetRegion = targetRegion;
		this.project = project;
		this.tag = tag;
		this.filePath = filePath;
		this.hyperlinkType = hyperlinkType;
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

		if(hyperlinkType ==  HyperlinkDetectorsClientContext.HYPERLINK_TYPE_CONNECTOR){
			// Remove the project name segment
			IPath path = filePath.removeFirstSegments(1);
			
			// Check if the path is valid and a file exists at the path, if so open it.
			IResource file = project.findMember(path);
			if (file != null && file.exists() && file instanceof IFile) {			
				System.out.println("File exists");
				FileInformation info = getFileInformation((IFile) file);
				openFileInEditor(info.getFile(), info.getOffset(), CONNECTOR_ID, tag);
			}			
		}else if(hyperlinkType == HyperlinkDetectorsClientContext.HYPERLINK_TYPE_JRFCONFIG){
			System.out.println(project.getLocation());
			String projectPath = project.getLocation().toOSString();
			String projectPathConfig = projectPath.substring(0, projectPath.lastIndexOf("-")+1) + AbstractHyperlink.PROJECT_NAME_PART_CONFIG;
			System.out.println(projectPathConfig);
			IPath path = new Path(projectPathConfig);
			File file = path.toFile();
			
			path = path.append(AbstractHyperlink.LOCATION_RESOURCES_FILES).append(AbstractHyperlink.LOCATION_PART_DEPLOY_ALL);
			System.out.println(path);
			System.out.println(file.exists());
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

			int index = document.get().indexOf(CONNECTOR_ID + tag + "\"");

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
