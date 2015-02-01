package com.tcs.mobility.sf.lecton.testcaser.mockingbird.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
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

import com.tcs.mobility.sf.lecton.testcaser.mockingbird.datatypes.PrimaryDataType;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.models.FieldDeclarationInfo;
import com.tcs.mobility.sf.lecton.testcaser.mockingbird.models.JavaInfo;

public class ASTProcessor extends ParentProcessor {

	public static final int AST_LEVEL = AST.JLS3;

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

	public static JavaInfo getClassInformation(ICompilationUnit cmpUnit) {

		System.out.println(cmpUnit.getElementName());

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
			if(fieldDeclarationInfo != null){
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

	private static PackageDeclaration createPackageDeclaration(AST ast, String packageName) {
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		packageDeclaration.setName(ast.newName(packageName));
		return packageDeclaration;
	}

	/**
	 * @param fieldDeclaration
	 * @return
	 */
	private static FieldDeclarationInfo getFieldDeclarationInfo(FieldDeclaration fieldDeclaration) {
		FieldDeclarationInfo fieldDeclartionInfo = new FieldDeclarationInfo();

		// Get the name of the fields
		VariableDeclarationFragment fragment;
		List<String> fields = new ArrayList<String>();

		for (Object object : fieldDeclaration.modifiers()) {
			if (object instanceof Modifier && ((Modifier) object).isFinal()) {
				return null;
			}
		}

		for (Object object : fieldDeclaration.fragments()) {
			fragment = (VariableDeclarationFragment) object;

			// Get field name
			SimpleName fieldName = fragment.getName();
			System.out.println("Field Name : " + fieldName.getIdentifier());
			fields.add(fieldName.getIdentifier());
		}

		// Get the type and package of the fields
		Type fieldType = fieldDeclaration.getType();
		ITypeBinding typeBinding = fieldType.resolveBinding();
		if (typeBinding != null) {
			String typeName1 = typeBinding.getName();
			System.out.println("Type Name : " + typeName1);
			fieldDeclartionInfo.setTypeName(typeName1);

			if (fieldType.isSimpleType()) {
				fieldDeclartionInfo.setSimpleType(true);

				IPackageBinding packageBinding = typeBinding.getPackage();
				if (packageBinding != null) {
					System.out.println("Type Package : " + packageBinding.getName());
					fieldDeclartionInfo.setTypePackage(packageBinding.getName());
				}
			} else {
				fieldDeclartionInfo.setSimpleType(false);
			}
		}
		fieldDeclartionInfo.setFieldNames(fields);
		return fieldDeclartionInfo;
	}

	private static String getTypeNameFullyQualified(CompilationUnit astRoot) {
		return getPackageName(astRoot) + "." + getTypeName(getTypeDeclaration(astRoot));
	}

	private static TypeDeclaration getTypeDeclaration(CompilationUnit astRoot) {
		Object type = astRoot.types().get(0);
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
		System.out.println("Class Name : " + className.getFullyQualifiedName());
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
				System.out.println("Import Name : " + importName.getFullyQualifiedName());
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
