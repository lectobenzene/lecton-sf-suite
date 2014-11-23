package com.tcs.mobility.sf.lecton.jumper.hyperlinks;

import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public abstract class AbstractHyperlink implements IHyperlink {

	/**
	 * Opens the file in the editor
	 * 
	 * @param file
	 *            the file to open
	 * @param index
	 *            used to calculate the location of the cursor
	 */
	protected void openFileInEditor(IResource file, int index, String serviceRef, String serviceName) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.CHAR_START, index + serviceRef.length());
		map.put(IMarker.CHAR_END, index + serviceRef.length() + serviceName.length());

		IMarker marker = null;

		try {
			marker = file.createMarker(IMarker.TEXT);
			marker.setAttributes(map);
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker);
			} catch (PartInitException e) {
				// TODO log
			}
		} catch (CoreException e1) {
			// TODO log
		} finally {
			try {
				if (marker != null)
					marker.delete();
			} catch (CoreException e) {
				// TODO log
			}
		}
	}
}
