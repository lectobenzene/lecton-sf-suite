package com.tcs.mobility.sf.lecton.testcaser.mockingbird.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.PrimaryDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.models.FieldDeclarationInfo;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.models.JavaInfo;

public class ASTProcessor extends ParentProcessor {

	public static final int AST_LEVEL = AST.JLS3;

	/**
	 * Creates the CompilationUnit based on the model java file
	 * 
	 * @param ast
	 *            the AST
	 * @param jFolder
	 *            the folder where the modal java file exists
	 * @param classInformation
	 *            information about the modal class
	 * @return the unit created
	 */
	public static CompilationUnit createCompilationUnit(AST ast, IJavaElement jFolder, JavaInfo classInformation) {
		CompilationUnit unit = ast.newCompilationUnit();

		// Write the package name
		PackageDeclaration packageDeclaration = createPackageDeclaration(ast, jFolder.getElementName());
		unit.setPackage(packageDeclaration);

		// Write the list of imports
		for (String imports : classInformation.getImportList()) {
			ImportDeclaration importDeclaration = ast.newImportDeclaration();
			System.out.println(imports);
			importDeclaration.setName(ast.newName(imports));
			unit.imports().add(importDeclaration);
		}

		// Add the source file also to the list of imports
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		importDeclaration.setName(ast.newName(classInformation.getTypeNameFullyQualified()));
		unit.imports().add(importDeclaration);

		// Create the TYPE name
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setInterface(false);
		type.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		type.setName(ast.newSimpleName(classInformation.getTypeName() + "Mother"));

		// Create the Mock method
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		List modifiers = methodDeclaration.modifiers();
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(classInformation.getTypeLowerName() + "Mock"));
		methodDeclaration.setReturnType2(ast.newSimpleType(ast.newName(classInformation.getTypeName())));

		org.eclipse.jdt.core.dom.Block block = ast.newBlock();

		// Create the variable and initialize it
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		SimpleName variableName = ast.newSimpleName(classInformation.getTypeLowerName());
		fragment.setName(variableName);
		ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
		classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(classInformation.getTypeName())));
		fragment.setInitializer(classInstanceCreation);
		VariableDeclarationStatement variableDeclarationStatement = ast.newVariableDeclarationStatement(fragment);
		variableDeclarationStatement.setType(ast.newSimpleType(ast.newSimpleName(classInformation.getTypeName())));
		block.statements().add(variableDeclarationStatement);

		// Process each fields and add the new statements created to the block
		for (FieldDeclarationInfo fieldDeclarationInfo : classInformation.getFieldDeclarations()) {
			PrimaryDataType dataType = getDataTypeFactory(fieldDeclarationInfo.getTypeName());
			for (String fieldName : fieldDeclarationInfo.getFieldNames()) {
				ExpressionStatement expressionStatement = dataType.getExpressionStatement(ast, classInformation.getTypeLowerName(), fieldName);
				block.statements().add(expressionStatement);
			}
		}

		// Return the variable
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(ast.newSimpleName(variableName.getFullyQualifiedName()));
		block.statements().add(returnStatement);

		methodDeclaration.setBody(block);
		type.bodyDeclarations().add(methodDeclaration);
		unit.types().add(type);

		return unit;
	}

	
	/**
	 * Creates the CompilationUnit based on the model java file
	 * 
	 * @param ast
	 *            the AST
	 * @param jFolder
	 *            the folder where the modal java file exists
	 * @param classInformation
	 *            information about the modal class
	 * @return the unit created
	 */
	public static CompilationUnit createCompilationUnit(AST ast, IJavaElement jFolder, ICompilationUnit javaUnit) {
		CompilationUnit unit = ast.newCompilationUnit();

		JavaInfo classInformation = getClassInformation(javaUnit);
		
		// Write the package name
		PackageDeclaration packageDeclaration = createPackageDeclaration(ast, jFolder.getElementName());
		unit.setPackage(packageDeclaration);

		// Write the list of imports
		for (String imports : classInformation.getImportList()) {
			ImportDeclaration importDeclaration = ast.newImportDeclaration();
			System.out.println(imports);
			importDeclaration.setName(ast.newName(imports));
			unit.imports().add(importDeclaration);
		}

		// Add the source file also to the list of imports
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		importDeclaration.setName(ast.newName(classInformation.getTypeNameFullyQualified()));
		unit.imports().add(importDeclaration);

		// Create the TYPE name
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setInterface(false);
		type.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		type.setName(ast.newSimpleName(classInformation.getTypeName() + "Mother"));

		// Create the Mock method
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);
		List modifiers = methodDeclaration.modifiers();
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(classInformation.getTypeLowerName() + "Mock"));
		methodDeclaration.setReturnType2(ast.newSimpleType(ast.newName(classInformation.getTypeName())));

		org.eclipse.jdt.core.dom.Block block = ast.newBlock();

		// Create the variable and initialize it
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		SimpleName variableName = ast.newSimpleName(classInformation.getTypeLowerName());
		fragment.setName(variableName);
		ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
		classInstanceCreation.setType(ast.newSimpleType(ast.newSimpleName(classInformation.getTypeName())));
		fragment.setInitializer(classInstanceCreation);
		VariableDeclarationStatement variableDeclarationStatement = ast.newVariableDeclarationStatement(fragment);
		variableDeclarationStatement.setType(ast.newSimpleType(ast.newSimpleName(classInformation.getTypeName())));
		block.statements().add(variableDeclarationStatement);

		// Process each fields and add the new statements created to the block
		for (FieldDeclarationInfo fieldDeclarationInfo : classInformation.getFieldDeclarations()) {
			PrimaryDataType dataType = getDataTypeFactory(fieldDeclarationInfo.getTypeName());
			if(fieldDeclarationInfo.isSimpleType()){
				// Recursion

				System.out.println("SIMPLE TYPE");
				String stringPattern = fieldDeclarationInfo.getTypePackage()+"."+fieldDeclarationInfo.getTypeName();
				System.out.println("SearchPattern = "+stringPattern);
				// Search for the Unit
				SearchPattern searchPattern = SearchPattern.createPattern(stringPattern, IJavaSearchConstants.TYPE, IJavaSearchConstants.TYPE, SearchPattern.R_EXACT_MATCH);
				IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
				
				SearchRequestor requestor = new SearchRequestor() {
					
					@Override
					public void acceptSearchMatch(SearchMatch match) throws CoreException {
						// TODO Auto-generated method stub
						System.out.println("MATCH found");
						Object element = match.getElement();
						System.out.println(match.getResource().getFullPath());
					}
				};
				SearchEngine searchEngine = new SearchEngine();
				
				try {
					searchEngine.search(searchPattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope, requestor, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			for (String fieldName : fieldDeclarationInfo.getFieldNames()) {
				ExpressionStatement expressionStatement = dataType.getExpressionStatement(ast, classInformation.getTypeLowerName(), fieldName);
				block.statements().add(expressionStatement);
			}
		}

		// Return the variable
		ReturnStatement returnStatement = ast.newReturnStatement();
		returnStatement.setExpression(ast.newSimpleName(variableName.getFullyQualifiedName()));
		block.statements().add(returnStatement);

		methodDeclaration.setBody(block);
		type.bodyDeclarations().add(methodDeclaration);
		unit.types().add(type);

		return unit;
	}

	
	/**
	 * Extracts all needed info about the java file and returns the object
	 * 
	 * @param cmpUnit
	 *            the java ICompilationUnit
	 * @return class information object
	 */
	public static JavaInfo getClassInformation(ICompilationUnit cmpUnit) {
		ASTParser parser = ASTParser.newParser(AST_LEVEL);
		parser.setSource(cmpUnit);
		parser.setResolveBindings(true);
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		AST ast = astRoot.getAST();

		// Get packageName
		String packageName = getPackageName(astRoot);

		// Get list of imports
		List<String> importInfo = getImportInfo(astRoot);

		TypeDeclaration javaType = getTypeDeclaration(astRoot);

		// Get Class name
		String typeName = getTypeName(javaType);

		// Get Fully Qualified Class name
		String typeNameFullyQualified = getTypeNameFullyQualified(astRoot);

		List<FieldDeclarationInfo> fieldDeclarations = new ArrayList<FieldDeclarationInfo>();

		// Get the field info
		for (FieldDeclaration fieldDeclaration : javaType.getFields()) {
			FieldDeclarationInfo fieldDeclarationInfo = getFieldDeclarationInfo(fieldDeclaration);
			if (fieldDeclarationInfo != null) {
				fieldDeclarations.add(fieldDeclarationInfo);
			}
		}

		JavaInfo javaInfo = new JavaInfo();
		javaInfo.setPackageName(packageName);
		javaInfo.setImportList(importInfo);
		javaInfo.setTypeName(typeName);
		javaInfo.setTypeNameFullyQualified(typeNameFullyQualified);
		javaInfo.setFieldDeclarations(fieldDeclarations);

		return javaInfo;
	}

	/**
	 * Create the package declaration based on the package name
	 * @param ast
	 * @param packageName
	 * @return package declaration
	 */
	private static PackageDeclaration createPackageDeclaration(AST ast, String packageName) {
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(ast.newName(packageName));
		return packageDeclaration;
	}

	/**
	 * Get the Field Declaration info
	 * @param fieldDeclaration
	 * @return the field declaration object
	 */
	private static FieldDeclarationInfo getFieldDeclarationInfo(FieldDeclaration fieldDeclaration) {
		FieldDeclarationInfo fieldDeclartionInfo = new FieldDeclarationInfo();

		// Get the name of the fields
		VariableDeclarationFragment fragment;
		List<String> fields = new ArrayList<String>();

		// Don't add FINAL type field
		for (Object object : fieldDeclaration.modifiers()) {
			if (object instanceof Modifier && ((Modifier) object).isFinal()) {
				return null;
			}
		}

		for (Object object : fieldDeclaration.fragments()) {
			fragment = (VariableDeclarationFragment) object;

			// Get field name
			SimpleName fieldName = fragment.getName();
			fields.add(fieldName.getIdentifier());
		}

		// Get the type and package of the fields
		Type fieldType = fieldDeclaration.getType();
		ITypeBinding typeBinding = fieldType.resolveBinding();
		if (typeBinding != null) {
			String typeName1 = typeBinding.getName();
			fieldDeclartionInfo.setTypeName(typeName1);

			if (fieldType.isSimpleType()) {
				fieldDeclartionInfo.setSimpleType(true);

				IPackageBinding packageBinding = typeBinding.getPackage();
				if (packageBinding != null) {
					fieldDeclartionInfo.setTypePackage(packageBinding.getName());
				}
			} else {
				fieldDeclartionInfo.setSimpleType(false);
			}
		}
		fieldDeclartionInfo.setFieldNames(fields);
		return fieldDeclartionInfo;
	}

	/**
	 * Get the type name with the package details
	 * @param astRoot
	 * @return
	 */
	private static String getTypeNameFullyQualified(CompilationUnit astRoot) {
		return getPackageName(astRoot) + "." + getTypeName(getTypeDeclaration(astRoot));
	}

	/**
	 * Returns the type declaration from the CompilationUnit
	 * @param unit
	 * @return
	 */
	public static TypeDeclaration getTypeDeclaration(CompilationUnit unit) {
		Object type = unit.types().get(0);
		if (type instanceof TypeDeclaration) {
			return ((TypeDeclaration) type);
		}
		return null;
	}

	/**
	 * Returns the name of the java type
	 * 
	 * @param javaType
	 * @return type name
	 */
	private static String getTypeName(TypeDeclaration javaType) {
		SimpleName className = javaType.getName();
		return className.getFullyQualifiedName();
	}

	/**
	 * Returns the import list
	 * 
	 * @param astRoot
	 * @return list of import declaration
	 */
	private static List<String> getImportInfo(CompilationUnit astRoot) {
		List<String> imports = new ArrayList<String>();
		for (Object importObj : astRoot.imports()) {
			if (importObj instanceof ImportDeclaration) {
				Name importName = ((ImportDeclaration) importObj).getName();
				imports.add(importName.getFullyQualifiedName());
			}
		}
		return imports;
	}

	/**
	 * Get the package name
	 * 
	 * @param astRoot
	 * @return name of the package
	 */
	private static String getPackageName(CompilationUnit astRoot) {
		Name packageName = astRoot.getPackage().getName();
		return packageName.getFullyQualifiedName();
	}
}
