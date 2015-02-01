package com.tcs.mobility.sf.lecton.testcaser.mockingbird.models;

import java.util.List;

public class JavaInfo {

	private String packageName;
	private List<String> importList;
	private String typeName;
	private String typeNameFullyQualified;

	private List<FieldDeclarationInfo> fieldDeclarations;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<String> getImportList() {
		return importList;
	}

	public void setImportList(List<String> importInfo) {
		this.importList = importInfo;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getTypeLowerName(){
		return typeName.substring(0, 1).toLowerCase() + typeName.substring(1);
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeNameFullyQualified() {
		return typeNameFullyQualified;
	}

	public void setTypeNameFullyQualified(String typeNameFullyQualified) {
		this.typeNameFullyQualified = typeNameFullyQualified;
	}

	public List<FieldDeclarationInfo> getFieldDeclarations() {
		return fieldDeclarations;
	}

	public void setFieldDeclarations(List<FieldDeclarationInfo> fieldDeclarations) {
		this.fieldDeclarations = fieldDeclarations;
	}

}
