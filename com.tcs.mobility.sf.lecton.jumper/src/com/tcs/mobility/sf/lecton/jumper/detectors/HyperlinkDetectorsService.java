package com.tcs.mobility.sf.lecton.jumper.detectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.tcs.mobility.sf.lecton.jumper.hyperlinks.ReverseServiceHyperlink;
import com.tcs.mobility.sf.lecton.jumper.hyperlinks.ServiceHyperlink;

public class HyperlinkDetectorsService extends AbstractHyperlinkDetector {

	/** Pattern to go from Java to XML */
	public static final String SF_SERVICE = "@Service\\(\"([^\"]*)\"\\)";

	/** Pattern to go from XML to Java */
	public static final String SF_REVERSE_SERVICE = "service-ref=\"([^\"]*)\"";

	/** Flag for Jumping from Java to XML */
	public static final int HYPERLINK_TYPE_NORMAL = 1;

	/** Flag for Jumping from XML to Java */
	public static final int HYPERLINK_TYPE_REVERSE = 2;

	/** Name of the service */
	private String serviceRef;

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		Pattern patternService = Pattern.compile(SF_SERVICE);
		Pattern patternReverseService = Pattern.compile(SF_REVERSE_SERVICE);

		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IFile file = ((IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput())
				.getFile();
		IProject project = file.getProject();

		IRegion lineRegion = null;
		String matchLine = null;

		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			matchLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IHyperlink[] hyperlinks = null;

		/*
		 * Search for normal service string
		 */
		Matcher matcher = patternService.matcher(matchLine);
		hyperlinks = createHyperlink(document, offset, project, lineRegion, matchLine, matcher, HYPERLINK_TYPE_NORMAL);

		/*
		 * If hyperlinks is null, then check for reverse service string.
		 */
		if (hyperlinks == null) {
			matcher = patternReverseService.matcher(matchLine);
			hyperlinks = createHyperlink(document, offset, project, lineRegion, matchLine, matcher, HYPERLINK_TYPE_REVERSE);
		}

		return hyperlinks;
	}

	/**
	 * Creates the Hyperlink
	 * 
	 * @param document
	 *            the document of the textViewer
	 * @param offset
	 *            the mouse pointer position
	 * @param project
	 *            the project to which the document belongs to
	 * @param lineRegion
	 *            the line under study
	 * @param matchLine
	 *            the content of the lineRegion
	 * @param matcher
	 *            the matcher object
	 * @param hyperlinkType
	 *            the type of hyperlink
	 * @return array of hyperlinks
	 */
	private IHyperlink[] createHyperlink(IDocument document, int offset, IProject project, IRegion lineRegion, String matchLine, Matcher matcher,
			int hyperlinkType) {
		while (matcher.find()) {
			serviceRef = matcher.group(1);

			int index = matchLine.indexOf(serviceRef);
			IRegion targetRegion = new Region(lineRegion.getOffset() + index, serviceRef.length());
			if (targetRegion != null) {
				int offset2 = targetRegion.getOffset();
				try {
					String serviceName = document.get(offset2, targetRegion.getLength());

					if ((targetRegion.getOffset() <= offset) && (targetRegion.getOffset() + targetRegion.getLength()) > offset) {
						IHyperlink hyperlink = null;
						if (hyperlinkType == HYPERLINK_TYPE_NORMAL) {
							hyperlink = new ServiceHyperlink(targetRegion, serviceName, project);
						} else if (hyperlinkType == HYPERLINK_TYPE_REVERSE) {
							hyperlink = new ReverseServiceHyperlink(targetRegion, serviceName, project);
						}
						return new IHyperlink[] { hyperlink };
					}
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
