package com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveDescriptor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class JavaRefactor {

	public void moveClass(IPackageFragmentRoot root, String destinationPackage, ICompilationUnit[] filesToMove) {

		RefactoringContribution refactoringContribution = RefactoringCore.getRefactoringContribution(IJavaRefactorings.MOVE);
		RefactoringDescriptor desc = refactoringContribution.createDescriptor();
		MoveDescriptor moveDes = (MoveDescriptor) desc;
		moveDes.setComment("Moving Unit");
		moveDes.setDescription("Moving Unit");
		moveDes.setDestination(root.getPackageFragment(destinationPackage));
		moveDes.setProject(root.getJavaProject().getElementName());
		moveDes.setMoveResources(new IFile[0], new IFolder[0], filesToMove);
		moveDes.setUpdateReferences(true);

		RefactoringStatus status = new RefactoringStatus();
		try {
			Refactoring refactoring = moveDes.createRefactoring(status);

			IProgressMonitor monitor = new NullProgressMonitor();
			refactoring.checkInitialConditions(monitor);
			refactoring.checkFinalConditions(monitor);
			Change change = refactoring.createChange(monitor);
			change.perform(monitor);

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
