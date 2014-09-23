package com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JavaGeneratorDialog extends Dialog {
	private Text txtCurrentPackage;
	private Text txtCommonPackage;
	private String currentPackage;
	private String commonPackage;
	private boolean xmlWrapperEnabled;
	private boolean nameSpaceFieldEnabled;
	private Button btnAddNamespaceTo;
	private Button btnEnableXmlelementwrapper;
	
	public JavaGeneratorDialog(Shell parentShell, String currentPackage, String commonPackage) {
		super(parentShell);
		this.currentPackage = currentPackage;
		this.commonPackage = commonPackage;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Configuration");
	}

	@Override
	protected void okPressed() {
		setCurrentPackage(txtCurrentPackage.getText());
		setCommonPackage(txtCommonPackage.getText());
		setXmlWrapperEnabled(btnEnableXmlelementwrapper.getSelection());
		setNameSpaceFieldEnabled(btnAddNamespaceTo.getSelection());
		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) composite.getLayout();
		gridLayout.verticalSpacing = 9;
		gridLayout.marginLeft = 4;
		gridLayout.numColumns = 2;
		composite.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Current Package :");

		txtCurrentPackage = new Text(composite, SWT.BORDER);
		txtCurrentPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblCommonPackage = new Label(composite, SWT.NONE);
		lblCommonPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblCommonPackage.setText("Common Package : ");

		txtCommonPackage = new Text(composite, SWT.BORDER);
		txtCommonPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite cmpButtonBar = new Composite(composite, SWT.NONE);
		GridLayout gl_cmpButtonBar = new GridLayout(1, false);
		cmpButtonBar.setLayout(gl_cmpButtonBar);
		GridData gd_cmpButtonBar = new GridData(SWT.FILL, SWT.TOP, true, true, 2, 1);
		gd_cmpButtonBar.verticalIndent = 5;
		gd_cmpButtonBar.heightHint = 55;
		cmpButtonBar.setLayoutData(gd_cmpButtonBar);

		btnEnableXmlelementwrapper = new Button(cmpButtonBar, SWT.CHECK);
		btnEnableXmlelementwrapper.setText("Enable XmlElementWrapper");
		
		btnAddNamespaceTo = new Button(cmpButtonBar, SWT.CHECK);
		btnAddNamespaceTo.setText("Add namespace to each Field");
		
		txtCurrentPackage.setText(currentPackage);
		txtCommonPackage.setText(commonPackage);
		btnAddNamespaceTo.setSelection(nameSpaceFieldEnabled);
		btnEnableXmlelementwrapper.setSelection(xmlWrapperEnabled);
		return composite;
	}

	public Text getTxtCurrentPackage() {
		return txtCurrentPackage;
	}

	public void setTxtCurrentPackage(Text txtCurrentPackage) {
		this.txtCurrentPackage = txtCurrentPackage;
	}

	public Text getTxtCommonPackage() {
		return txtCommonPackage;
	}

	public void setTxtCommonPackage(Text txtCommonPackage) {
		this.txtCommonPackage = txtCommonPackage;
	}

	public String getCurrentPackage() {
		return currentPackage;
	}

	public void setCurrentPackage(String currentPackage) {
		this.currentPackage = currentPackage;
	}

	public String getCommonPackage() {
		return commonPackage;
	}

	public void setCommonPackage(String commonPackage) {
		this.commonPackage = commonPackage;
	}

	public boolean isXmlWrapperEnabled() {
		return xmlWrapperEnabled;
	}

	public void setXmlWrapperEnabled(boolean xmlWrapperEnabled) {
		this.xmlWrapperEnabled = xmlWrapperEnabled;
	}

	public boolean isNameSpaceFieldEnabled() {
		return nameSpaceFieldEnabled;
	}

	public void setNameSpaceFieldEnabled(boolean nameSpaceFieldEnabled) {
		this.nameSpaceFieldEnabled = nameSpaceFieldEnabled;
	}

}
