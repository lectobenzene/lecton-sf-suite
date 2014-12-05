package com.tcs.mobility.sf.lecton.jumper.hyperlinks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class ConfigHyperlink extends AbstractHyperlink {

	private final IRegion targetRegion;
	
	/** The path where the file to be opened is located */
	private final String urlPath;
	
	/** The path of the file from which the hyperlink was clicked */
	private final IPath filePath;
	
	private final IProject project;

	public ConfigHyperlink(IRegion region, String urlPath, IPath filePath, IProject project) {
		targetRegion = region;
		this.project = project;
		this.urlPath = urlPath;
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
		WSConsole.i("Config Hyperlink Clicked");
		
		WSConsole.d("Project = "+project.getFullPath());
		WSConsole.d("urlPath = "+urlPath);
		WSConsole.d("FilePath = "+filePath.toOSString());

		// Remove the project name segment
		IPath path = filePath.removeFirstSegments(1);
		// Remove the file name segment
		path = path.removeLastSegments(1);

		/*
		 *  'config' comes as a part of the Url. So remove it from the end.
		 *  This also confirms if 'config' is the last segment. It has to be.
		 */
		
		if ("config".equals(path.lastSegment())) {
			path = path.removeLastSegments(1);
			path = path.append(urlPath);
		}

		// Check if the path is valid and a file exists at the path, if so open it.
		IResource file = project.findMember(path);
		System.out.println(file.getClass());
		if (file != null && file.exists() && file instanceof IFile) {
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), (IFile) file);
			} catch (PartInitException e) {
				WSConsole.e(e);
			}
		}
	}
}
