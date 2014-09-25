package com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.views;

import java.beans.Introspector;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;
import com.tcs.mobility.sf.lecton.xml2xsd.source.parser.XsdParser;
import com.tcs.mobility.sf.lecton.xml2xsd2java.listeners.JobChangeListener;
import com.tcs.mobility.sf.lecton.xml2xsd2java.utils.Utility;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller.JavaRefactor;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller.XsdToJavaGenerator;

public class CreateJavaView extends ViewPart {

	public static final String PACKAGEROOT_SRC = "src/main/java";
	public static final String PACKAGE_PREFIX = "com.bnppf.adm.easybanking";
	public static final String PACKAGE_DOMAIN_MODEL = "al.domain.message";
	public static final String PACKAGE_SIL = "al.message";

	public static final String PROJECT_PREFIX = "BE_FORTIS_";

	public static final String PROJECT_DEFAULT_CODE = "XXXX";

	public static final String XSD_NAME_REQUEST = "Request";
	public static final String XSD_NAME_RESPONSE = "Response";

	private ICompilationUnit[] javaFilesAlreadyPresents;

	private Label lblDirectoryPath;
	private Label lblPackagePath;
	private Label lblXsdPath;
	private Text txtDirPath;
	private Text txtPackPath;
	private Text txtXsdPath;
	private Button btnGenerate;
	private Text txtOldPath;
	private Text txtNewPath;
	private Label lblOldPath;
	private Label lblNewPath;
	private Button btnMove;
	private Text txtFileToMove;
	private Label lblFileToMove;
	private Label lblCommonPackage;
	private Label lblCurrentPackage;
	private Text txtCommonPackage;
	private Text txtCurrentPackage;
	private Button btnObtainPath;

	public CreateJavaView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		lblDirectoryPath = new Label(parent, SWT.NONE);
		lblDirectoryPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectoryPath.setText("Directory path");

		txtDirPath = new Text(parent, SWT.BORDER);
		txtDirPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblPackagePath = new Label(parent, SWT.NONE);
		lblPackagePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPackagePath.setText("package path");

		txtPackPath = new Text(parent, SWT.BORDER);
		txtPackPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblXsdPath = new Label(parent, SWT.NONE);
		lblXsdPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblXsdPath.setText("xsd path");

		txtXsdPath = new Text(parent, SWT.BORDER);
		txtXsdPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(parent, SWT.NONE);

		btnGenerate = new Button(parent, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				XsdToJavaGenerator generator = new XsdToJavaGenerator();
				generator.generate(txtDirPath.getText(), txtPackPath.getText(), txtXsdPath.getText(), XsdToJavaGenerator.TYPE_ADVANCED);
			}
		});
		btnGenerate.setText("Generate");

		lblOldPath = new Label(parent, SWT.NONE);
		lblOldPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOldPath.setText("Old Path");

		txtOldPath = new Text(parent, SWT.BORDER);
		txtOldPath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		lblNewPath = new Label(parent, SWT.NONE);
		lblNewPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewPath.setText("New Path");

		txtNewPath = new Text(parent, SWT.BORDER);
		txtNewPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblFileToMove = new Label(parent, SWT.NONE);
		lblFileToMove.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFileToMove.setText("File To Move");

		txtFileToMove = new Text(parent, SWT.BORDER);
		txtFileToMove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(parent, SWT.NONE);

		btnMove = new Button(parent, SWT.NONE);
		btnMove.setText("Move");
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		lblCommonPackage = new Label(parent, SWT.NONE);
		lblCommonPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCommonPackage.setText("Common Package");

		txtCommonPackage = new Text(parent, SWT.BORDER);
		txtCommonPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblCurrentPackage = new Label(parent, SWT.NONE);
		lblCurrentPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCurrentPackage.setText("Current Package");

		txtCurrentPackage = new Text(parent, SWT.BORDER);
		txtCurrentPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		btnObtainPath = new Button(parent, SWT.NONE);
		btnObtainPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
						.getSelection("org.eclipse.jdt.ui.PackageExplorer");
				if (selection instanceof IStructuredSelection) {
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof IResource) {
						IPath projectRelativePath = ((IResource) firstElement).getProjectRelativePath();
						final IProject project = ((IResource) firstElement).getProject();
						String projectName = project.getName();
						String appCode = PROJECT_DEFAULT_CODE;
						if (projectName.startsWith(PROJECT_PREFIX)) {
							appCode = projectName.substring(PROJECT_PREFIX.length(), PROJECT_PREFIX.length() + 4).toLowerCase();
						}

						IPath location = project.getLocation();
						location = location.append(PACKAGEROOT_SRC);
						txtDirPath.setText(location.toOSString());
						txtXsdPath.setText(((IResource) firstElement).getLocation().toOSString());

						String xsdName = ((IResource) firstElement).getName().split("\\.")[0];
						String packageEndName;
						if (xsdName.endsWith(XSD_NAME_REQUEST)) {
							packageEndName = xsdName.substring(0, xsdName.indexOf(XSD_NAME_REQUEST));
						} else if (xsdName.endsWith(XSD_NAME_RESPONSE)) {
							packageEndName = xsdName.substring(0, xsdName.indexOf(XSD_NAME_RESPONSE));
						} else {
							// TODO : Warning
							// XSD Name should end with Request or Response OR
							// the XSD
							// name is used as package name
							WSConsole.d("XSD Name did not end with Request or Response, hence name is used as package name");
							packageEndName = xsdName;
						}

						txtPackPath.setText(PACKAGE_PREFIX + "." + appCode + "." + PACKAGE_SIL + "." + Introspector.decapitalize(packageEndName));

						final IJavaProject javaProject = JavaCore.create(project);
						IPath packageRootPath = javaProject.getPath().append(PACKAGEROOT_SRC);
						IPackageFragmentRoot packageRoot;
						try {
							packageRoot = javaProject.findPackageFragmentRoot(packageRootPath);
							IPackageFragment packageFragment = packageRoot.getPackageFragment(txtPackPath.getText());
							if (packageFragment.exists()) {
								javaFilesAlreadyPresents = packageFragment.getCompilationUnits();
							}

						} catch (JavaModelException e1) {
							WSConsole.e(e1.getMessage());
							WSConsole.e(e1);
						}

						final Job job = new UIJob("Generate Java") {

							@Override
							public IStatus runInUIThread(IProgressMonitor monitor) {
								XsdToJavaGenerator generator = new XsdToJavaGenerator();
								generator.generate(txtDirPath.getText(), txtPackPath.getText(), txtXsdPath.getText(),
										XsdToJavaGenerator.TYPE_ADVANCED);
								return Status.OK_STATUS;
							}
						};

						job.schedule();

						final Job secondJob = new UIJob("Move Java") {

							@Override
							public IStatus runInUIThread(IProgressMonitor monitor) {
								// Adding SOURCE FOLDER to classpath

								IClasspathEntry[] entries = null;
								try {
									entries = javaProject.getRawClasspath();
								} catch (JavaModelException e) {
									WSConsole.e(e.getMessage());
									WSConsole.e(e);
								}

								// Check if src/main/java is in the ClassPath
								boolean srcFolderMissing = true;

								IPath srcTrailingPath = new Path(PACKAGEROOT_SRC);
								IPath srcOriginalTrailingPath;

								for (IClasspathEntry entry : entries) {
									if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && entry.getPath().segmentCount() > 3) {
										srcOriginalTrailingPath = entry.getPath().removeFirstSegments(entry.getPath().segmentCount() - 3);
										if (srcTrailingPath.equals(srcOriginalTrailingPath)) {
											srcFolderMissing = false;
											break;
										}
									}
								}

								// if src/main.java is not in classpath, add it
								// as a
								// Source Folder
								if (srcFolderMissing) {
									IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
									System.arraycopy(entries, 0, newEntries, 0, entries.length);

									IPath srcPath = javaProject.getPath().append(PACKAGEROOT_SRC);
									IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcPath, null);

									newEntries[entries.length] = JavaCore.newSourceEntry(srcEntry.getPath());
									try {
										javaProject.setRawClasspath(newEntries, null);
									} catch (JavaModelException e) {
										WSConsole.e(e.getMessage());
										WSConsole.e(e);
									}
								}

								// Get the list of all the ICompilation units
								// created
								try {
									IPath packageRootPath = javaProject.getPath().append(PACKAGEROOT_SRC);
									IPackageFragmentRoot packageRoot = javaProject.findPackageFragmentRoot(packageRootPath);
									packageRoot.getResource().refreshLocal(IResource.DEPTH_INFINITE, monitor);
									IPackageFragment packageFragment = packageRoot.getPackageFragment(txtPackPath.getText());

									final ICompilationUnit[] javaFilesCreated = packageFragment.getCompilationUnits();
									final List<String> primaryElements = new XsdParser().getPrimaryElements(new File(txtXsdPath.getText()));

									final List<ICompilationUnit> unitsToMove = new ArrayList<ICompilationUnit>();
									for (ICompilationUnit javaFile : javaFilesCreated) {
										String fileNameWithoutExtension = Utility.getFileNameWithoutExtension(javaFile.getElementName());
										for (String element : primaryElements) {
											if (!element.equalsIgnoreCase(fileNameWithoutExtension)) {
												unitsToMove.add(javaFile);
											}
										}
									}

									if (javaFilesAlreadyPresents != null) {
										unitsToMove.removeAll(Arrays.asList(javaFilesAlreadyPresents));
									}
									String destinationPackage = txtNewPath.getText();
									ICompilationUnit[] filesToMove = {};
									new JavaRefactor().moveClass(packageRoot, destinationPackage, unitsToMove.toArray(filesToMove));

								} catch (JavaModelException e) {
									WSConsole.e(e.getMessage());
									WSConsole.e(e);
								} catch (CoreException e) {
									WSConsole.e(e.getMessage());
									WSConsole.e(e);
								}
								return Status.OK_STATUS;
							}
						};

						final IJobManager manager = Job.getJobManager();
						final IJobChangeListener listener = new JobChangeListener() {
							@Override
							public void done(IJobChangeEvent event) {
								if (event.getJob().getName().equalsIgnoreCase(job.getName())) {
									secondJob.schedule();
									manager.removeJobChangeListener(this);
								}
							}
						};
						manager.addJobChangeListener(listener);

					} else if (firstElement instanceof IJavaElement) {
						IPath path = ((IJavaElement) firstElement).getPath();
						txtXsdPath.setText(((IJavaElement) firstElement).getResource().getLocation().toOSString());
					}
				}

			}
		});
		btnObtainPath.setText("Obtain Path");
		new Label(parent, SWT.NONE);

	}

	@Override
	public void setFocus() {

	}

}
