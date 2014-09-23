package com.tcs.mobility.sf.lecton.xml2xsd2java.xml2xsd.wizards.pages;

import java.beans.Introspector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;
import com.tcs.mobility.sf.lecton.utility.utils.UtilResource;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller.JavaGenerator;

public class PrimaryInputWizardPage extends WizardPage {
	private Label lblServiceName;
	private Text txtServiceName;
	private Label lblRootFolder;
	private Text txtRootFolder;
	private Button btnBrowse;
	private ISelection selection;
	private Group grpPackageDetails;
	private Label lblRRPackageName;
	private Label lblCommonPackageName;
	private Text txtTxtpackageName;
	private Text txtCommonPackageName;
	private Label lblModulePackageName;
	private Text txtModulePackageName;
	private Button btnOnlyGenerateXsd;

	private boolean isOnlyXsd;
	private Label lblNewLabel;

	public PrimaryInputWizardPage() {
		super("PrimaryInputWizardPage");
		setTitle("Create Data Model");
		setDescription("This wizard creates Data Model for the given Web Service");
	}

	/**
	 * @wbp.parser.constructor
	 */
	public PrimaryInputWizardPage(ISelection selection) {
		this();
		this.selection = selection;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gl_container = new GridLayout(3, false);
		container.setLayout(gl_container);

		lblServiceName = new Label(container, SWT.NONE);
		lblServiceName.setText("Service Name :");

		txtServiceName = new Text(container, SWT.BORDER);
		txtServiceName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPackageDetails();
				dialogChanged();
			}
		});
		GridData gd_txtServiceName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtServiceName.widthHint = 413;
		txtServiceName.setLayoutData(gd_txtServiceName);
		new Label(container, SWT.NONE);

		lblRootFolder = new Label(container, SWT.NONE);
		lblRootFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblRootFolder.setText("Root Folder :");

		txtRootFolder = new Text(container, SWT.BORDER);
		txtRootFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPackageDetails();
				dialogChanged();
			}
		});
		txtRootFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleModuleLocationBrowse();
			}
		});
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse...");
		
		lblNewLabel = new Label(container, SWT.NONE);

		grpPackageDetails = new Group(container, SWT.NONE);
		GridLayout gl_grpPackageDetails = new GridLayout(2, false);
		gl_grpPackageDetails.marginBottom = 2;
		gl_grpPackageDetails.marginRight = 5;
		gl_grpPackageDetails.marginTop = 2;
		gl_grpPackageDetails.marginLeft = 10;
		grpPackageDetails.setLayout(gl_grpPackageDetails);
		grpPackageDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		grpPackageDetails.setText("Package Details");

		lblRRPackageName = new Label(grpPackageDetails, SWT.NONE);
		lblRRPackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblRRPackageName.setText("Req/Resp Package Name :");

		txtTxtpackageName = new Text(grpPackageDetails, SWT.BORDER);
		txtTxtpackageName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtTxtpackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblCommonPackageName = new Label(grpPackageDetails, SWT.NONE);
		lblCommonPackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblCommonPackageName.setText("Common Package Name :");

		txtCommonPackageName = new Text(grpPackageDetails, SWT.BORDER);
		txtCommonPackageName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtCommonPackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblModulePackageName = new Label(grpPackageDetails, SWT.NONE);
		lblModulePackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblModulePackageName.setText("Module Package Name :");

		txtModulePackageName = new Text(grpPackageDetails, SWT.BORDER);
		txtModulePackageName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		txtModulePackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpPackageDetails.setTabList(new Control[] { txtTxtpackageName, txtCommonPackageName });

		btnOnlyGenerateXsd = new Button(container, SWT.CHECK);
		btnOnlyGenerateXsd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnOnlyGenerateXsd.getSelection()) {
					enableGrpPackageDetails(false);
				} else {
					enableGrpPackageDetails(true);
				}
			}
		});
		btnOnlyGenerateXsd.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 3, 1));
		btnOnlyGenerateXsd.setText("Only generate XSD File");
		container.setTabList(new Control[] { txtServiceName, txtRootFolder, btnBrowse, grpPackageDetails });

		setSelectionData(selection);
		setPackageDetails();
	}

	protected void enableGrpPackageDetails(boolean status) {
		txtCommonPackageName.setEnabled(status);
		txtModulePackageName.setEnabled(status);
		txtTxtpackageName.setEnabled(status);
		grpPackageDetails.setEnabled(status);
	}

	/**
	 * 
	 */
	private void setPackageDetails() {
		// TODO : possible null pointer exception
		IResource resource = UtilResource.getResource(getTxtRootFolder());
		if (resource != null) {
			// TODO : possible null pointer exception
			IProject project = resource.getProject();
			String projectName = project.getName();

			// Get Application Code
			String appCode = JavaGenerator.PROJECT_DEFAULT_CODE;
			if (projectName.startsWith(JavaGenerator.PROJECT_PREFIX)) {
				appCode = projectName.substring(JavaGenerator.PROJECT_PREFIX.length(), JavaGenerator.PROJECT_PREFIX.length() + 4)
						.toLowerCase();
			}

			// Contruct package name
			String packagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_DOMAIN_MODEL + "."
					+ Introspector.decapitalize(getTxtServiceNameText());
			String commonPackagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_DOMAIN_MODEL + "."
					+ JavaGenerator.PACKAGE_COMMON;
			String modulePackagePath = JavaGenerator.PACKAGE_PREFIX + "." + appCode + "." + JavaGenerator.PACKAGE_DOMAIN_MODEL + "."
					+ JavaGenerator.PACKAGE_MODULE;

			setTxtTxtpackageName(packagePath);
			setTxtCommonPackageName(commonPackagePath);
			setTxtModulePackageName(modulePackagePath);
		}
	}

	/**
	 * Method to handle the Browse button action of Service location
	 */
	private void handleModuleLocationBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select the DataModel Jar Project");
		int result = dialog.open();
		if (result == ContainerSelectionDialog.OK) {
			Object[] selections = dialog.getResult();
			if (selections.length == 1) {
				setTxtRootFolder(((Path) selections[0]).toOSString());
			}
		}
	}

	/**
	 * If a package is selected while creating a new Data Model, even then, the
	 * Source root folder location is set in the Root Folder text box.
	 * 
	 * @param selection
	 *            The selection made
	 */
	private void setSelectionData(ISelection selection) {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() <= 1) {
				Object obj = sSelection.getFirstElement();
				String path = null;
				if (obj instanceof IJavaProject) {
					// If a java project is selected, then select the
					// src/main/java source root folder
					IPath packageRootPath = ((IJavaProject) obj).getPath().append(JavaGenerator.PACKAGEROOT_SRC);
					try {
						IPackageFragmentRoot packageFragmentRoot = ((IJavaProject) obj).findPackageFragmentRoot(packageRootPath);
						if (packageFragmentRoot != null) {
							path = packageFragmentRoot.getPath().toOSString();
						}
					} catch (JavaModelException e) {
						WSConsole.e(e.getMessage());
						WSConsole.e(e);
					}
				} else if (obj instanceof IPackageFragmentRoot) {
					// If a Root folder is selected, then set the location in
					// the text box
					final IPackageFragmentRoot pkgFragmentRoot = (IPackageFragmentRoot) obj;
					path = pkgFragmentRoot.getPath().toOSString();
				} else if (obj instanceof IPackageFragment) {
					// If a package is selected, then set the location of the
					// parent root folder
					final IPackageFragment pkgFragment = (IPackageFragment) obj;
					IJavaElement packageParent = pkgFragment.getParent();
					if (packageParent instanceof IPackageFragmentRoot) {
						path = packageParent.getPath().toOSString();
					}

				} else if (obj instanceof ICompilationUnit) {
					// If a java file is selected, the set the location of the
					// parent root folder
					final ICompilationUnit cmpUnit = (ICompilationUnit) obj;
					IJavaElement cmpUnitParent = cmpUnit.getParent();
					if (cmpUnitParent instanceof IPackageFragment) {
						IJavaElement packageParent = cmpUnitParent.getParent();
						if (packageParent instanceof IPackageFragmentRoot) {
							path = packageParent.getPath().toOSString();
						}
					}
				}
				if (path != null) {
					setTxtRootFolder(path);
				}
			}
		}
	}

	/**
	 * The method that is called when any field is changed. This method acts as
	 * a Validation method. The 'Next' button is enabled when all validation are
	 * satisfied.
	 */
	private void dialogChanged() {
		String serviceName = getTxtServiceNameText();
		if ("".equalsIgnoreCase(serviceName.trim())) {
			updateStatus("Service Name should not be empty");
			return;
		}
		if (serviceName.contains(".")) {
			updateStatus("Provide proper name for the service");
			return;
		}
		if (getTxtRootFolder().length() == 0) {
			updateStatus("The root folder location should not be empty");
			return;
		}
		if (getTxtTxtpackageName().length() == 0) {
			updateStatus("The package name should not be empty");
			return;
		}
		if (getTxtCommonPackageName().length() == 0) {
			updateStatus("The Common package name should not be empty");
			return;
		}
		if (getTxtModulePackageName().length() == 0) {
			updateStatus("The Module package name should not be empty");
			return;
		}
		updateStatus(null);
	}

	/**
	 * Displays an error message in the title area and enables/disables the NEXT
	 * button
	 * 
	 * @param message
	 *            Error message to display in the title area
	 */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getTxtServiceNameText() {
		return txtServiceName.getText();
	}

	public void setTxtServiceNameText(String text) {
		txtServiceName.setText(text);
	}

	public String getTxtRootFolder() {
		return txtRootFolder.getText();
	}

	public void setTxtRootFolder(String text) {
		txtRootFolder.setText(text);
	}

	public String getTxtTxtpackageName() {
		return txtTxtpackageName.getText();
	}

	public void setTxtTxtpackageName(String text) {
		txtTxtpackageName.setText(text);
	}

	public String getTxtCommonPackageName() {
		return txtCommonPackageName.getText();
	}

	public void setTxtCommonPackageName(String text) {
		txtCommonPackageName.setText(text);
	}

	public String getTxtModulePackageName() {
		return txtModulePackageName.getText();
	}

	public void setTxtModulePackageName(String text) {
		txtModulePackageName.setText(text);
	}

	public boolean isOnlyXsd() {
		return btnOnlyGenerateXsd.getSelection();
	}

}
