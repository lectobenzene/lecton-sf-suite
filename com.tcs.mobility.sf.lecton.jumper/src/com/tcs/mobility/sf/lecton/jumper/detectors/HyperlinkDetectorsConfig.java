package com.tcs.mobility.sf.lecton.jumper.detectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.tcs.mobility.sf.lecton.jumper.hyperlinks.AbstractHyperlink;
import com.tcs.mobility.sf.lecton.jumper.hyperlinks.ConfigHyperlink;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class HyperlinkDetectorsConfig extends AbstractHyperlinkDetector {

	private static final Pattern patternConfig = Pattern.compile(AbstractHyperlink.SF_CONFIG_URL);

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IFile file = ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput()).getFile();
		IProject project = file.getProject();

		// Detect the hyperlink only for 'masterConfig.xml'
		if (AbstractHyperlink.FILE_MASTER_CONFIG.equals(file.getName())) {
			IRegion lineRegion = null;
			String matchLine = null;

			try {
				lineRegion = document.getLineInformationOfOffset(offset);
				matchLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
			} catch (BadLocationException e) {
				WSConsole.e(e);
			}

			/*
			 * Search for the 'config' url
			 */
			Matcher matcher = patternConfig.matcher(matchLine);
			return createHyperlink(document, offset, project, lineRegion, matchLine, matcher, file.getFullPath());

		}
		return null;
	}

	private IHyperlink[] createHyperlink(IDocument document, int offset, IProject project, IRegion lineRegion, String matchLine, Matcher matcher, IPath filePath) {
		while (matcher.find()) {

			// Show the hyperlink for the entire URL
			String url = matcher.group();
			String urlPath = matcher.group(1);
			WSConsole.d("URL Path" + urlPath);

			int index = matchLine.indexOf(url);
			IRegion targetRegion = new Region(lineRegion.getOffset() + index, url.length());
			if (targetRegion != null) {
				if ((targetRegion.getOffset() <= offset) && (targetRegion.getOffset() + targetRegion.getLength()) > offset) {
					IHyperlink hyperlink = new ConfigHyperlink(targetRegion, urlPath, filePath, project);
					return new IHyperlink[] { hyperlink };
				}
			}
		}
		return null;
	}

}
