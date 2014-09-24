package com.tcs.mobility.sf.lecton.xml2xsd2java.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.tcs.mobility.sf.lecton.xml2xsd2java.Activator;

public class JavaGeneratorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public JavaGeneratorPreferencePage() {
		super(GRID);
		setTitle("Java Generator Preferences");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.PREF_BOOLEAN_XMLELEMENTWRAPPER, "Enable XMLWrapperElement", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.PREF_BOOLEAN_ENABLEFIELDNAMESPACE, "Add Namespace to all Fields", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.PREF_BOOLEAN_IMPLEMENTSERIALIZABLE, "Implement Serializable", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}