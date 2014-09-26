package com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;
import com.tcs.mobility.sf.lecton.xml2xsd2java.utils.Utility;

public class JavaPropOrderer {

	public void runPropOrder(List<ICompilationUnit> units) {
		for (ICompilationUnit unit : units) {
			Document document;
			try {
				document = new Document(unit.getSource());
				String typeName = Utility.getFileNameWithoutExtension(unit.getElementName());

				ASTParser parser = ASTParser.newParser(JavaGenerator.AST_LEVEL);
				parser.setSource(unit);
				CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);

				AST ast = astRoot.getAST();
				ASTRewrite rewriter = ASTRewrite.create(ast);

				Object object = astRoot.types().get(0);
				List<IExtendedModifier> modifiers = null;
				if (object instanceof TypeDeclaration) {
					modifiers = ((TypeDeclaration) object).modifiers();
				} else if (object instanceof EnumDeclaration) {
					WSConsole.d("ENUM Declaration : propOrder not applicable");
				} else {
					WSConsole.d("Type class : " + object.getClass());
				}

				if (modifiers != null) {
					List<MemberValuePair> annotationValues = getAnnotationValues(modifiers);
					if (annotationValues != null) {
						ArrayInitializer originalValue = getPropOrder(annotationValues);
						ArrayInitializer modifiedValue = ast.newArrayInitializer();
						List<Expression> expressions = modifiedValue.expressions();
						expressions.addAll(getNewPropOrderList(unit, typeName, ast));
						rewriter.replace(originalValue, modifiedValue, null);
					}
				}

				// computation of the text edits
				TextEdit edits = rewriter.rewriteAST(document, unit.getJavaProject().getOptions(true));

				// computation of the new source code
				edits.apply(document);
				String newSource = document.get();

				// update of the compilation unit
				unit.getBuffer().setContents(newSource);
				unit.getBuffer().save(null, true);

			} catch (JavaModelException e) {
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

	private List<Expression> getNewPropOrderList(ICompilationUnit cu, String typeName, AST ast) throws JavaModelException {
		IType type = cu.getType(typeName);
		IField[] fields = type.getFields();
		StringLiteral propOrderValue;
		List<Expression> propOrders = new ArrayList<Expression>();
		for (IField field : fields) {
			if (!field.getElementName().equalsIgnoreCase("serialVersionUID")) {
				propOrderValue = ast.newStringLiteral();
				propOrderValue.setLiteralValue(field.getElementName());
				propOrders.add(propOrderValue);
			}
		}
		return propOrders;
	}

	private ArrayInitializer getPropOrder(List<MemberValuePair> annotationValues) {
		for (MemberValuePair memberValue : annotationValues) {
			if ("propOrder".equals(memberValue.getName().getIdentifier())) {
				Expression value = memberValue.getValue();
				if (value instanceof ArrayInitializer) {
					return (ArrayInitializer) value;
				}
			}
		}
		return null;
	}

	private List<MemberValuePair> getAnnotationValues(List<IExtendedModifier> modifiers) {
		for (IExtendedModifier modifier : modifiers) {
			if (modifier instanceof NormalAnnotation) {
				Name typeName = ((NormalAnnotation) modifier).getTypeName();
				if ("XmlType".equals(typeName.getFullyQualifiedName())) {
					List<MemberValuePair> values = ((NormalAnnotation) modifier).values();
					return values;
				}
			}
		}
		return null;
	}
}
