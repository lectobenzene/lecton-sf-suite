package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.providers.proposalproviders;

public class XSDInfo {

	/**
	 * Label to be shown during the proposal is being popped up
	 */
	private String label;
	/**
	 * The description to be shown during the proposal is being popped up
	 */
	private String description;

	public XSDInfo(String label, String description) {
		this.label = label;
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
