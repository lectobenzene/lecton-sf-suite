package com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.progress.UIJob;

import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;
import com.tcs.mobility.sf.lecton.xml2xsd.source.parser.XsdParser;
import com.tcs.mobility.sf.lecton.xml2xsd2java.Activator;
import com.tcs.mobility.sf.lecton.xml2xsd2java.listeners.JobChangeListener;
import com.tcs.mobility.sf.lecton.xml2xsd2java.preferences.PreferenceConstants;
import com.tcs.mobility.sf.lecton.xml2xsd2java.utils.Utility;
import com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.ui.dialogs.JavaGeneratorDialog;

public class JavaGenerator {

	public static final String ANNOTATION_MEMEBERPAIR_NAME = "name";
	public static final String DOMAIN = "domain";
	public static final String ID_PACKAGE_EXPLORER = "org.eclipse.jdt.ui.PackageExplorer";
	public static final String JOB_MOVING_JAVA_FILES = "Moving Java Files";
	public static final String JOB_GENERATING_JAVA_FILES = "Generating Java Files";
	public static final String JOB_FAMILY_GENERATE_JAVA = "GenerateJavaJobFamily";

	public static final String PACKAGEROOT_SRC = "src/main/java";
	public static final String PACKAGEROOT_RESOURCE = "src/main/resources";
	public static final String PACKAGEROOT_XSD_LOCATION = "extract/xsd";

	public static final String PACKAGE_PREFIX = "com.bnppf.adm.easybanking";
	public static final String PACKAGE_DOMAIN_MODEL = "al.domain.message";
	public static final String PACKAGE_SIL = "al.message";
	public static final String PACKAGE_COMMON = "common";
	public static final String PACKAGE_MODULE = "module";

	public static final String PROJECT_PREFIX = "BE_FORTIS_";

	public static final String PROJECT_DEFAULT_CODE = "XXXX";

	public static final String XSD_NAME_REQUEST = "Request";
	public static final String XSD_NAME_RESPONSE = "Response";

	public static final String EXTENSION_XSD = ".xsd";
	public static final String EXTENSION_JAVA = ".java";

	private boolean isXMLWrapperEnabled = false;
	private boolean isNamespaceEnabled = false;

	private ICompilationUnit[] javaFilesAlreadyPresent;
	private List<ICompilationUnit> unitsForPropOrdering;

	private String directoryPath;
	private String packagePath;
	private String commonPackagePath;
	private String xsdPath;

	private List<ICompilationUnit> unitsToNotMove;
	private IWorkbenchSite site;

	public void generateJava(final IProject project, String directoryPath, String xsdPath, String packagePath, String commonPackagePath,
			IWorkbenchSite site, boolean override) {

		// Initialize global variables
		this.directoryPath = directoryPath;
		this.xsdPath = xsdPath;
		this.packagePath = packagePath;
		this.commonPackagePath = commonPackagePath;
		this.site = site;

		/*
		 * Get the list of Java files already present in the package. These java
		 * files should not be moved to common package
		 */
		final IJavaProject javaProject = JavaCore.create(project);
		IPath packageRootPath = javaProject.getPath().append(PACKAGEROOT_SRC);
		IPackageFragmentRoot packageRoot;
		try {
			packageRoot = javaProject.findPackageFragmentRoot(packageRootPath);
			IPackageFragment packageFragment = packageRoot.getPackageFragment(packagePath);
			if (packageFragment.exists()) {
				javaFilesAlreadyPresent = packageFragment.getCompilationUnits();
			}
		} catch (JavaModelException e) {
			WSConsole.e(e.getMessage());
			WSConsole.e(e);
		}

		// Create Jobs for Generating and Moving java
		final Job job = assignGenerateJavaJob();
		final Job secondJob = assignMoveJavaJob(javaProject);
		final Job thirdJob = reassignPropOrder(javaProject);

		// Set listener to determine if 1st job is finished. Only then,
		// the 2nd job should be executed.
		final IJobManager manager = Job.getJobManager();
		final IJobChangeListener listener = new JobChangeListener() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getJob().getName().equalsIgnoreCase(job.getName())) {
					secondJob.schedule();
				} else if (event.getJob().getName().equalsIgnoreCase(secondJob.getName())) {
					// Third job should run only if second job succeeds.
					thirdJob.schedule();
					manager.removeJobChangeListener(this);
				}
			}
		};

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		isXMLWrapperEnabled = preferenceStore.getBoolean(PreferenceConstants.PREF_BOOLEAN_XMLELEMENTWRAPPER);
		isNamespaceEnabled = preferenceStore.getBoolean(PreferenceConstants.PREF_BOOLEAN_ENABLEFIELDNAMESPACE);

		if (override) {
			job.schedule();
		} else {
			showDialog(packagePath, commonPackagePath, job);
		}

		manager.addJobChangeListener(listener);
	}

	private void showDialog(String packagePath, String commonPackagePath, final Job job) {
		// Create dialog to select configuration and destination
		JavaGeneratorDialog dialog = new JavaGeneratorDialog(Display.getDefault().getActiveShell(), packagePath, commonPackagePath);

		// Set checkbox preference
		dialog.setXmlWrapperEnabled(isXMLWrapperEnabled);
		dialog.setNameSpaceFieldEnabled(isNamespaceEnabled);

		if (dialog.open() == Dialog.OK) {
			commonPackagePath = dialog.getCommonPackage();
			packagePath = dialog.getCurrentPackage();
			isNamespaceEnabled = dialog.isNameSpaceFieldEnabled();
			isXMLWrapperEnabled = dialog.isXmlWrapperEnabled();
			job.schedule();
		}
	}

	private Job reassignPropOrder(final IJavaProject javaProject) {
		final Job thirdJob = new UIJob("Assigning propOrder") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				WSConsole.i("PropOrder correction Starting");
				if (unitsForPropOrdering != null) {
					new JavaPropOrderer().runPropOrder(unitsForPropOrdering);
				}
				WSConsole.i("PropOrder correction Finished");

				// Run Organise Imports on the file created in main package to
				// import the one's in common package
				WSConsole.i("Running Organize Imports");
				OrganizeImportsAction org = new OrganizeImportsAction(site);
				for (ICompilationUnit unit : unitsToNotMove) {
					if (unit.exists()) {
						org.run(unit);
					}
				}
				WSConsole.i("Organize Imports done");
				return Status.OK_STATUS;
			}
		};
		return thirdJob;
	}

	/**
	 * Constructs a job to move the java files to common folder
	 * 
	 * @param javaProject
	 *            The Current Java Project
	 * @return
	 */
	private Job assignMoveJavaJob(final IJavaProject javaProject) {
		final Job secondJob = new UIJob(JOB_MOVING_JAVA_FILES) {

			@Override
			public boolean belongsTo(Object family) {
				return (JOB_FAMILY_GENERATE_JAVA).equals(family);
			}

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {

				// Adding SOURCE FOLDER to classpath
				addSourceFolderToClasspath(javaProject);

				try {
					// Get the list of all the ICompilation units created
					IPath packageRootPath = javaProject.getPath().append(PACKAGEROOT_SRC);
					IPackageFragmentRoot packageRoot = javaProject.findPackageFragmentRoot(packageRootPath);
					packageRoot.getResource().refreshLocal(IResource.DEPTH_INFINITE, monitor);
					IPackageFragment packageFragment = packageRoot.getPackageFragment(packagePath);

					final ICompilationUnit[] javaFilesInPackage = packageFragment.getCompilationUnits();
					final List<String> primaryElements = new XsdParser().getPrimaryElements(new File(xsdPath));

					List<ICompilationUnit> javaFilesCreated = new LinkedList<ICompilationUnit>(Arrays.asList(javaFilesInPackage));
					for (ICompilationUnit iCompilationUnit : javaFilesCreated) {
					}
					if (javaFilesAlreadyPresent != null) {
						for (ICompilationUnit iCompilationUnit2 : javaFilesAlreadyPresent) {
						}
						List<ICompilationUnit> asList = Arrays.asList(javaFilesAlreadyPresent);
						javaFilesCreated.removeAll(asList);
					}

					// Delete package-info and ObjectFactory java files.
					for (ICompilationUnit javaFile : javaFilesCreated) {
						if ("package-info.java".equals(javaFile.getElementName()) || "ObjectFactory.java".equals(javaFile.getElementName())) {
							javaFile.delete(true, monitor);
						}
					}

					// Implement Serializable
					if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.PREF_BOOLEAN_IMPLEMENTSERIALIZABLE)) {
						for (ICompilationUnit unit : javaFilesCreated) {
							if (unit.exists()) {
								try {
									implementSerializable(unit);
								} catch (IllegalArgumentException e) {
									WSConsole.e(e.getMessage());
									WSConsole.e(e);
								} catch (MalformedTreeException e) {
									WSConsole.e(e.getMessage());
									WSConsole.e(e);
								} catch (BadLocationException e) {
									WSConsole.e(e.getMessage());
									WSConsole.e(e);
								}
							}
						}
					}

					// Update the namespace of java files created
					if (isNamespaceEnabled) {
						for (ICompilationUnit unit : javaFilesCreated) {
							IType type = unit.getType(Utility.getFileNameWithoutExtension(unit.getElementName()));
							if (type.exists()) {

								/*
								 * Obtain the annotation name for all the Fields
								 */
								Map<String, String> annotationsMap = new HashMap<String, String>();

								IField[] fields = type.getFields();
								for (IField field : fields) {
									String fieldName = field.getElementName();
									String annotationValue = getAnnotationValue(field, "XmlElement", ANNOTATION_MEMEBERPAIR_NAME);
									if (annotationValue != null) {
										annotationsMap.put(annotationValue, fieldName);
									}
								}

								/*
								 * Update the namespace for each Field (If not
								 * present) before moving the units.
								 */
								if (annotationsMap.size() != 0) {
									try {
										updateNamespace(type, annotationsMap);
									} catch (MalformedTreeException e) {
										WSConsole.e(e.getMessage());
										WSConsole.e(e);
									} catch (BadLocationException e) {
										WSConsole.e(e.getMessage());
										WSConsole.e(e);
									}
								}
							}
						}
					}

					final List<ICompilationUnit> unitsToMove = new ArrayList<ICompilationUnit>();
					unitsToNotMove = new ArrayList<ICompilationUnit>();

					for (ICompilationUnit javaFile : javaFilesCreated) {
						String fileNameWithoutExtension = Utility.getFileNameWithoutExtension(javaFile.getElementName());
						boolean isPresent = false;
						for (String element : primaryElements) {
							if (element.equalsIgnoreCase(fileNameWithoutExtension)) {
								isPresent = true;
								break;
							}
						}
						if (isPresent) {
							unitsToNotMove.add(javaFile);
						} else {
							unitsToMove.add(javaFile);
						}
					}

					/*
					 * If units with same name is present in both source and
					 * destination package, then do not move them directly to
					 * destination. Move all the new Fields and Methods from
					 * source to destination. Then delete the source unit
					 */
					final List<ICompilationUnit> finalUnitsToMove = new ArrayList<ICompilationUnit>();
					unitsForPropOrdering = new ArrayList<ICompilationUnit>();

					for (ICompilationUnit unit : unitsToMove) {
						IType type = unit.getType(Utility.getFileNameWithoutExtension(unit.getElementName()));
						if (type.exists()) {

							/*
							 * Move the java files that are not present in the
							 * destination package. If Java class with same name
							 * is present in the destination package, then just
							 * move the Fields and Methods
							 */

							IPackageFragment packageFragmentTarget = packageRoot.getPackageFragment(commonPackagePath);
							ICompilationUnit cuTarget = packageFragmentTarget.getCompilationUnit(unit.getElementName());

							// Check if the Unit is already present in the
							// Destination package
							if (cuTarget.exists()) {
								unitsForPropOrdering.add(cuTarget);

								IField[] fieldsSource = type.getFields();
								IMethod[] methodsSource = type.getMethods();

								IType typeTarget = cuTarget.getType(type.getElementName());
								IField[] fieldsTarget = typeTarget.getFields();
								IMethod[] methodsTarget = typeTarget.getMethods();

								List<IField> fieldsToMove = new ArrayList<IField>();
								List<IMethod> methodsToMove = new ArrayList<IMethod>();

								fieldsToMove.addAll(Arrays.asList(fieldsSource));
								methodsToMove.addAll(Arrays.asList(methodsSource));


								for (IField fieldSource : fieldsSource) {
									for (IField fieldTarget : fieldsTarget) {
										if (fieldTarget.getElementName().equals(fieldSource.getElementName())) {
											fieldsToMove.remove(fieldSource);
											break;
										}
									}
								}

								for (IMethod methodSource : methodsSource) {
									for (IMethod methodTarget : methodsTarget) {
										if (methodTarget.getElementName().equals(methodSource.getElementName())) {
											methodsToMove.remove(methodSource);
											break;
										}
									}
								}


								for (IField field : fieldsToMove) {
									field.move(typeTarget, null, null, false, monitor);
								}
								for (IMethod method : methodsToMove) {
									method.move(typeTarget, null, null, false, monitor);
								}

								IImportDeclaration[] importsSource = unit.getImports();
								for (IImportDeclaration importSource : importsSource) {

									IJavaElement[] findElements = cuTarget.findElements(importSource);
									if (findElements != null) {
										for (IJavaElement iJavaElement : findElements) {
											// TODO : Suspected error block
										}
									} else {
										importSource.move(cuTarget, null, null, false, monitor);
									}
								}

								// Delete the unit after moving the fields and
								// methods
								unit.delete(true, null);

							} else {
								finalUnitsToMove.add(unit);
							}
						}
					}
					ICompilationUnit[] filesToMove = {};
					new JavaRefactor().moveClass(packageRoot, commonPackagePath, finalUnitsToMove.toArray(filesToMove));

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
		return secondJob;
	}

	/**
	 * Constructs a job to generate Java files
	 * 
	 * @return Job to generate Java files
	 */
	private Job assignGenerateJavaJob() {
		final Job job = new UIJob(JOB_GENERATING_JAVA_FILES) {

			@Override
			public boolean belongsTo(Object family) {
				return (JOB_FAMILY_GENERATE_JAVA).equals(family);
			}

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				WSConsole.i("Java Files being created");
				new XsdToJavaGenerator().generate(directoryPath, packagePath, xsdPath, isXMLWrapperEnabled ? XsdToJavaGenerator.TYPE_ADVANCED
						: XsdToJavaGenerator.TYPE_STANDARD);
				WSConsole.i("Java Files created");
				return Status.OK_STATUS;
			}
		};
		return job;
	}

	/**
	 * If src/main/java is not in classpath, then make it a SourceFolder by
	 * adding it to the Classpath
	 * 
	 * @param javaProject
	 *            The Current Java project
	 */
	private void addSourceFolderToClasspath(final IJavaProject javaProject) {
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

		// if src/main/java is not in classpath, add it as a Source Folder
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
	}

	/**
	 * Implements Serializable interface
	 * 
	 * @param unit
	 *            the ICompilationUnit
	 * @throws JavaModelException
	 * @throws IllegalArgumentException
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 */
	private void implementSerializable(ICompilationUnit unit) throws JavaModelException, IllegalArgumentException, MalformedTreeException,
			BadLocationException {
		// parse compilation unit
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(unit);
		final CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);

		// create a ASTRewrite
		final AST ast = astRoot.getAST();
		final ASTRewrite rewriter = ASTRewrite.create(ast);

		Object object = astRoot.types().get(0);
		List<Type> superTypes = null;
		if (object instanceof TypeDeclaration) {
			superTypes = ((TypeDeclaration) object).superInterfaceTypes();
		} else if (object instanceof EnumDeclaration) {
			WSConsole.d("ENUM Declaration : propOrder not applicable");
		} else {
			WSConsole.d("Type class : " + object.getClass());
		}

		if (superTypes != null) {
			ListRewrite listRewrite = rewriter.getListRewrite((TypeDeclaration) object, TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY);
			SimpleType newSimpleType = ast.newSimpleType(ast.newSimpleName("Serializable"));
			listRewrite.insertLast(newSimpleType, null);

			final TextEdit edits = rewriter.rewriteAST();

			// apply the text edits to the compilation unit
			final Document document = new Document(unit.getSource());
			edits.apply(document);

			// this is the code for adding statements
			unit.getBuffer().setContents(document.get());
			unit.save(null, true);

			// Insert the IMPORT and SerialUID
			unit.getType(Utility.getFileNameWithoutExtension(unit.getElementName())).createField("private static final long serialVersionUID = 1L;",
					null, true, null);
			unit.createImport("java.io.Serializable", null, null);
		}
	}

	/**
	 * Updates the NameSpace of all the fields in the provided {@code type}
	 * 
	 * @param type
	 *            The java type whose field's namespace should be updated
	 * @param annotationsMap
	 *            Map of annotationElementName and their Namespaces
	 * @throws JavaModelException
	 * @throws MalformedTreeException
	 * @throws BadLocationException
	 */
	private void updateNamespace(IType type, Map<String, String> annotationsMap) throws JavaModelException, MalformedTreeException,
			BadLocationException {
		// parse compilation unit
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		ICompilationUnit unit = type.getCompilationUnit();
		parser.setSource(unit);
		final CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);

		// create a ASTRewrite
		final AST ast = astRoot.getAST();
		final ASTRewrite rewriter = ASTRewrite.create(ast);

		Object object = astRoot.types().get(0);

		Javadoc javaDoc = null;
		List<BodyDeclaration> bodyDeclarations = null;
		if (object instanceof TypeDeclaration) {
			javaDoc = ((TypeDeclaration) object).getJavadoc();
			bodyDeclarations = ((TypeDeclaration) object).bodyDeclarations();
		} else if (object instanceof EnumDeclaration) {
			WSConsole.d("ENUM Declaration : propOrder not applicable");
		} else {
			WSConsole.d("Type class : " + object.getClass());
		}

		Map<String, String> javaDocNamespaceMap = getJavaDocNamespaceMap(javaDoc);

		if (javaDocNamespaceMap.size() != 0) {
			if (bodyDeclarations != null) {
				for (BodyDeclaration bodyDeclaration : bodyDeclarations) {
					if (bodyDeclaration instanceof FieldDeclaration) {
						NormalAnnotation annotation = getAnnotation(bodyDeclaration, "XmlElement");
						boolean isNamespacePresent = false;
						String annotationName = null;
						if (annotation != null) {
							List<MemberValuePair> values = annotation.values();
							for (MemberValuePair pair : values) {
								String identifier = pair.getName().getIdentifier();
								if ("namespace".equals(identifier)) {
									// If namespace is already present,
									// then don't update
									isNamespacePresent = true;
									break;
								} else if (ANNOTATION_MEMEBERPAIR_NAME.equals(identifier)) {
									Expression value = pair.getValue();
									if (value instanceof StringLiteral) {
										String literalValue = ((StringLiteral) value).getLiteralValue();
										annotationName = literalValue;
									}
								}
							}
						}
						if (!isNamespacePresent && annotationName != null && javaDocNamespaceMap.get(annotationName) != null) {
							ListRewrite listRewrite = rewriter.getListRewrite(annotation, NormalAnnotation.VALUES_PROPERTY);
							MemberValuePair nameSpacePair = ast.newMemberValuePair();
							nameSpacePair.setName(ast.newSimpleName("namespace"));
							StringLiteral nameSpaceUrl = ast.newStringLiteral();
							nameSpaceUrl.setLiteralValue(javaDocNamespaceMap.get(annotationName));
							nameSpacePair.setValue(nameSpaceUrl);
							listRewrite.insertAt(nameSpacePair, -1, null);
						}
					}
				}
			}
		}
		final TextEdit edits = rewriter.rewriteAST();

		// apply the text edits to the compilation unit
		final Document document = new Document(unit.getSource());
		edits.apply(document);

		// this is the code for adding statements
		unit.getBuffer().setContents(document.get());
		unit.save(null, true);
	}

	/**
	 * Gets the NormalAnnotation corresponding to the {@code elementName}
	 * identifier from the {@code bodyDeclaration}
	 * 
	 * @param bodyDeclaration
	 *            the AST node to search
	 * @param elementName
	 *            the identifierName of the annotation
	 * @return the NormalAnnotation node
	 */
	private NormalAnnotation getAnnotation(BodyDeclaration bodyDeclaration, String elementName) {
		List<IExtendedModifier> modifiers = bodyDeclaration.modifiers();
		for (IExtendedModifier modifier : modifiers) {
			if (modifier instanceof NormalAnnotation) {
				Name typeName = ((NormalAnnotation) modifier).getTypeName();
				if (typeName instanceof SimpleName) {
					String identifier = ((SimpleName) typeName).getIdentifier();
					if (elementName.equals(identifier)) {
						return (NormalAnnotation) modifier;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Scans the JavaDoc and returns a map of annotation and their namespaces
	 * 
	 * @param javaDoc
	 *            The JavaDoc to parse
	 * @return a map of annotations and their namespaces
	 */
	private Map<String, String> getJavaDocNamespaceMap(Javadoc javaDoc) {
		Map<String, String> javaDocMap = new HashMap<String, String>();
		List<TagElement> tags = javaDoc.tags();
		for (TagElement tag : tags) {
			List<TextElement> tagElements = tag.fragments();
			for (TextElement textElement : tagElements) {
				Pattern pattern = Pattern.compile("element name=\"([^\"]*)\" type=\"\\{([^\\}]*)\\}");
				Matcher matcher = pattern.matcher(textElement.getText());
				if (matcher.find()) {
					javaDocMap.put(matcher.group(1), matcher.group(2));
				}
			}
		}
		return javaDocMap;
	}

	/**
	 * Gets the value of {@code annotationMemberName} of the given
	 * {@code asannotationName} for the required {@code field}
	 * 
	 * @param field
	 *            The field to scan
	 * @param annotationName
	 *            The annotation to scan
	 * @param annotationMemberName
	 *            The tag whose value should be returned
	 * @return the value of the required annotation tag
	 * @throws JavaModelException
	 */
	private String getAnnotationValue(IField field, String annotationName, String annotationMemberName) throws JavaModelException {
		IAnnotation annotation = field.getAnnotation(annotationName);
		if (annotation.exists()) {
			IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair pair : memberValuePairs) {
				if (annotationMemberName.equals(pair.getMemberName())) {
					return (String) pair.getValue();
				}
			}
		}
		return null;
	}

}
