package com.tcs.mobility.sf.lecton.testcaser.mockingbird.ui;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.tcs.mobility.sf.lecton.utility.utils.UtilResource;

public class DialogPackageSelection extends Dialog {
	
	private Text txtPkgdestination;
	private String pkgDestination;
	
	public DialogPackageSelection(Shell parent) {
		super(parent);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblPackage = new Label(composite, SWT.NONE);
		lblPackage.setText("Package :");

		txtPkgdestination = new Text(composite, SWT.BORDER);
		GridData gd_txtPkgdestination = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPkgdestination.minimumWidth = 300;
		txtPkgdestination.setLayoutData(gd_txtPkgdestination);

		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(getShell(), (IContainer) UtilResource.getResource("/BE_FORTIS_DBIA-ap01-war/src/main/java/com/bnppf/adm/easybanking/dbia/al/common"), false, "Select Package");

				// Selection Suggestion
				// Keep a switch for this and add this to the preference
				UtilResource.getResource("/BE_FORTIS_DBIA-ap01-war/src/main/java/com/bnppf/adm/easybanking/dbia/al/common");
				Path[] paths = new Path[] {(Path) Path.fromOSString("/BE_FORTIS_DBIA-ap01-war/src/main/java/com/bnppf/adm/easybanking/dbia/al/common")}; 
				containerDialog.setInitialSelections(paths);
				containerDialog.create();
				containerDialog.setInitialSelections(paths);
				containerDialog.open();

				if(containerDialog.getReturnCode() == Dialog.OK){
					System.out.println("Browsing Done");
					Object[] result = containerDialog.getResult();
					System.out.println(result);
					
					
				}
				
			}
		});
		btnBrowse.setText("Browse...");

		return container;
	}

	
	@Override
	protected void okPressed() {
		pkgDestination = txtPkgdestination.getText();
		super.okPressed();
	}

	public String getPkgDestination() {
		return pkgDestination;
	}

}
