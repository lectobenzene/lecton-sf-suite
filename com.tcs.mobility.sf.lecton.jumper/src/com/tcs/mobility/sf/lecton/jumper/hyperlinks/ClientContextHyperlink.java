package com.tcs.mobility.sf.lecton.jumper.hyperlinks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

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

	private static final Pattern PATTERN_MASTER_SECTION_OPEN = Pattern.compile(AbstractHyperlink.SF_MASTER_SECTION_OPEN);
	private static final Pattern PATTERN_MASTER_SECTION_CLOSE = Pattern.compile(AbstractHyperlink.SF_MASTER_SECTION_CLOSE);

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

		if (hyperlinkType == HyperlinkDetectorsClientContext.HYPERLINK_TYPE_CONNECTOR) {
			// Remove the project name segment
			IPath path = filePath.removeFirstSegments(1);

			// Check if the path is valid and a file exists at the path, if so
			// open it.
			IResource file = project.findMember(path);
			if (file != null && file.exists() && file instanceof IFile) {
				System.out.println("File exists");
				FileInformation info = getFileInformation((IFile) file);
				openFileInEditor(info.getFile(), info.getOffset(), CONNECTOR_ID, tag);
			}
		} else if (hyperlinkType == HyperlinkDetectorsClientContext.HYPERLINK_TYPE_JRFCONFIG) {

			IFile masterConfigFile = getMasterConfigFile();

			if (masterConfigFile != null) {

				System.out.println("MasterConfig File obtained");
				FileInformation fileInformation = getFileInformation(masterConfigFile);

				try {
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), masterConfigFile);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * Obtain the MasterConfig.xml file
	 * 
	 * @return the masterConfig.xml file, or null if not found
	 */
	private IFile getMasterConfigFile() {
		String projectName = project.getName();
		String projectNameConfig = projectName.substring(0, projectName.lastIndexOf("-") + 1) + AbstractHyperlink.PROJECT_NAME_PART_CONFIG;

		IProject projectConfig = ResourcesPlugin.getWorkspace().getRoot().getProject(projectNameConfig);

		IPath path = projectConfig.getFullPath().removeLastSegments(1);
		path = path.append(AbstractHyperlink.LOCATION_RESOURCES_FILES).append(AbstractHyperlink.LOCATION_PART_DEPLOY_ALL);

		IFolder folder = projectConfig.getFolder(path);

		IFile masterConfigFile = null;

		try {
			for (IResource iResource : folder.members()) {
				if (iResource instanceof IFolder) {
					IResource findMember = ((IFolder) iResource).findMember((new Path(AbstractHyperlink.LOCATION_PART_SSC_CONFIG)).append(AbstractHyperlink.FILE_MASTER_CONFIG));
					System.out.println("Found member");
					if (findMember.exists()) {
						masterConfigFile = (IFile) findMember;
						break;
					}
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return masterConfigFile;
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

			String content = document.get();
			
			System.out.println("Inside file information");
			Matcher sectionMatcher = PATTERN_MASTER_SECTION_OPEN.matcher(content);

			getValue(content, sectionMatcher);

			int index = content.indexOf(CONNECTOR_ID + tag + "\"");

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

	private void getValue(String content, Matcher sectionMatcher) {
		while (sectionMatcher.find()) {
			System.out.println("Section Found");
			System.out.println(sectionMatcher.group());
			System.out.println(sectionMatcher.group(1));
			String group = sectionMatcher.group();

			int indexOf = content.indexOf(group);
			String sectionContent = obtainSectionContent(indexOf, content);

			System.out.println("SECTION CONTENT = \n" + sectionContent);

		}
	}

	private String obtainSectionContent(int beginIndex, String content) {
		int noOfSectionsFound = 0;
		int endIndex = -1;
		
		Matcher matcherClose = PATTERN_MASTER_SECTION_CLOSE.matcher(content);
		Matcher matcherOpen = PATTERN_MASTER_SECTION_OPEN.matcher(content);
		
		// if 'section' open tag is found, there has to be a corresponding close
		// tag, hence increment the 'noOfSectionsFound' variable
		while (matcherOpen.find()) {
			System.out.println("Inner Section found");
			System.out.println("MATCHER OPEN start= "+matcherOpen.start());
			System.out.println("MATCHER OPEN end= "+matcherOpen.end());
			noOfSectionsFound++;
		}

		// if 'section' close tag is found, get the end index 
		while(matcherClose.find() && noOfSectionsFound > 0){
			System.out.println("Sections Close tag found");
			System.out.println("MATCHER CLOSE start= "+matcherClose.start());
			System.out.println("MATCHER CLOSE end= "+matcherClose.end());
			endIndex = content.indexOf(matcherClose.group()) + matcherClose.group().length();
		}
		System.out.println("Begin index = "+ beginIndex);
		System.out.println("End index = "+ endIndex);

		// find the string within startIndex and endIndex
		return content.substring(beginIndex, endIndex);
	}

}
