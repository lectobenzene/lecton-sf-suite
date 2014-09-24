package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.context.providers.proposalproviders;

public class XSDInfo {

	private String label;
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
