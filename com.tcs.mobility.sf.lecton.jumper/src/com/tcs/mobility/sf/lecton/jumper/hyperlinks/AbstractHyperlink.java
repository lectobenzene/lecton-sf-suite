package com.tcs.mobility.sf.lecton.jumper.hyperlinks;

import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public abstract class AbstractHyperlink implements IHyperlink {

	// Location of mandatory folders
	public static final String LOCATION_SPRING_FILES = "src/main/resources/META-INF/spring";
	public static final String LOCATION_JAVA_FILES = "src/main/java";
	public static final String LOCATION_RESOURCES_FILES = "src/main/resources";
	public static final String LOCATION_PART_DEPLOY_ALL= "deploy/all/";
	public static final String LOCATION_PART_SSC_CONFIG= "ssc/config";

	public static final String SERVICE_REF_XML = "service-ref=\"";
	public static final String SERVICE_REF_JAVA = "@Service(\"";
	public static final String CONNECTOR_ID = "id=\"";

	public static final String PROJECT_NAME_PART_CONFIG = "config";

	public static final String FILE_MASTER_CONFIG = "masterConfig.xml";
	
	/** Pattern to go from Java to XML */
	public static final String SF_SERVICE = "@Service\\(\"([^\"]*)\"\\)";
	
	/** Pattern to go from XML to Java */
	public static final String SF_REVERSE_SERVICE = "service-ref=\"([^\"]*)\"";

	/** Pattern to detect url with 'config' as a protocol */
	public static final String SF_CONFIG_URL = "config://([^\"]+)";
	
	/** Pattern to detect connector-ref from client-context file */
	public static final String SF_CONNECTOR_REF = "connector-ref=\"([^\"]*)\"";

	/** Pattern to detect jrfConfigBundle from client-context file */
	public static final String SF_JRF_CONFIG_BUNDLE = "jrfConfigBundle=\"([^\"]*)\"";
	
	/** Pattern to match 'section' tag in masterConfig file */
	public static final String SF_MASTER_SECTION_OPEN = "<(?:\\s)*section(?:\\s)+path(?:\\s)*=\"([^\"]*)\"";
	
	public static final String SF_MASTER_SECTION_CLOSE = "</section>";

	// <section path="sf">
	
	/** Pattern to match 'configElement' tag in masterConfig file */
	
	
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

		openFileInEditor(file, map);
	}

	protected void openFileInEditor(IResource file, int startIndex, int endIndex) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(IMarker.CHAR_START, startIndex);
		map.put(IMarker.CHAR_END, endIndex);

		openFileInEditor(file, map);
	}
	
	protected void openFileInEditor(IResource file, HashMap<String, Object> map) {
		IMarker marker = null;

		try {
			marker = file.createMarker(IMarker.TEXT);
			marker.setAttributes(map);
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker);
			} catch (PartInitException e) {
				WSConsole.e(e);
			}
		} catch (CoreException e1) {
			WSConsole.e(e1);
		} finally {
			try {
				if (marker != null)
					marker.delete();
			} catch (CoreException e) {
				WSConsole.e(e);
			}
		}
	}
}
