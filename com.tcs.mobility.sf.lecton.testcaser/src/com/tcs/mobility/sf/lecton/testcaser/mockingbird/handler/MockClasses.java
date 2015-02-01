package com.tcs.mobility.sf.lecton.testcaser.mockingbird.handler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.tcs.mobility.sf.lecton.testcaser.mockingbird.core.ASTProcessor;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.models.JavaInfo;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;
import com.tcs.mobility.sf.lecton.utility.utils.UtilResource;

public class MockClasses extends AbstractHandler {

	public static final int AST_LEVEL = AST.JLS3;

	private List<ICompilationUnit> javaClassesToMockList;

	private ICompilationUnit cmpUnitSource;

	private IPath pkgSource;
	private IPath pkgDestination;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Mock Classes selected");

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		// Get the file that is selected [the source java file]. This can be
		// used for the selection suggestion
		ISelectionService selectionService = window.getSelectionService();
		IStructuredSelection selection = (IStructuredSelection) selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ICompilationUnit) {
			cmpUnitSource = ((ICompilationUnit) firstElement);
		}

		// Show dialog to map the package where to create the Mock Java file
		// DialogPackageSelection selectionDialog = new
		// DialogPackageSelection(window.getShell(), pkgSource);
		// selectionDialog.open();
		//
		// if (selectionDialog.getReturnCode() == Dialog.OK) {
		// System.out.println("Dialog Shown");
		//
		// // Get destination package from the dialog
		// pkgDestination = selectionDialog.getPkgDestination();
		// } else {
		// return null;
		// }

		// Selection Dialog to pick the package where the mock java has to be
		// created
		ContainerSelectionDialog containerDialog = new ContainerSelectionDialog(window.getShell(), null, false,
				"Select Package where Mock java has to be created:");
		containerDialog.open();

		if (containerDialog.getReturnCode() == Dialog.OK) {
			System.out.println("Browsing Done");

			Object[] result = containerDialog.getResult();
			System.out.println(result);
			if (result != null && result.length > 0 && result[0] instanceof Path) {
				pkgDestination = (Path) result[0];
			}
		}

		System.out.println("PKG to create the Java classes : " + pkgDestination.toOSString());

		javaClassesToMockList = getJavaClassesToMockFromSelection();
		if (javaClassesToMockList == null) {
			return null;
		}

		// Get Info about the Java files
		for (ICompilationUnit javaCls : javaClassesToMockList) {
			JavaInfo classInformation = ASTProcessor.getClassInformation(javaCls);
			System.out.println(classInformation.getTypeName());

			IFolder folder = (IFolder) UtilResource.getResource(pkgDestination.toOSString());
			IJavaElement jFolder = JavaCore.create(folder);
			System.out.println(jFolder.getElementName());

			AST ast = AST.newAST(AST.JLS3);
			
			CompilationUnit unit = ASTProcessor.createCompilationUnit(ast, jFolder, classInformation);
			
			TypeDeclaration typeName = (TypeDeclaration) unit.types().get(0);
			
			System.out.println(unit);

			String unitName = typeName.getName().getFullyQualifiedName()+".java";
			writeUnitToFile(unit, unitName, pkgDestination);

		}

		return null;
	}

	/**
	 * 
	 * @param unit
	 * @param unitName
	 */
	public void writeUnitToFile(CompilationUnit unit, String fileName, IPath fileLocation) {
		IFolder folder = (IFolder) UtilResource.getResource(fileLocation.toOSString());
		try {
			folder.getFile(fileName).create(new ByteArrayInputStream(unit.toString().getBytes()), false, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns all the ICompilationUnits that has to be mocked
	 * 
	 * @return List of javaFilesToMock
	 */
	private List<ICompilationUnit> getJavaClassesToMockFromSelection() {

		IJavaElement element = getSelectionJavaElement();
		if (element == null) {
			return null;
		}

		List<ICompilationUnit> javaFilesToMockList = new ArrayList<ICompilationUnit>();

		// The selection can be a single java file or a package
		if (element instanceof ICompilationUnit) {
			javaFilesToMockList.add((ICompilationUnit) element);
		} else if (element instanceof IPackageFragment) {
			try {
				javaFilesToMockList.addAll(Arrays.asList(((IPackageFragment) element).getCompilationUnits()));
			} catch (JavaModelException e) {
				WSConsole.e(e);
			}
		}

		return javaFilesToMockList;
	}

	/**
	 * Returns the selected object
	 * 
	 * @return the JavaElement selected
	 */
	private IJavaElement getSelectionJavaElement() {
		final ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		final ISelection selection = selectionService.getSelection();
		if (selection instanceof IStructuredSelection) {
			final Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IJavaElement) {
				return (IJavaElement) firstElement;
			}
		}
		return null;
	}
}
