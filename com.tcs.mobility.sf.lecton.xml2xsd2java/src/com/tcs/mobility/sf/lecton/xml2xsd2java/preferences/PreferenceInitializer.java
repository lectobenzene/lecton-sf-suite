package com.tcs.mobility.sf.lecton.xml2xsd2java.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.tcs.mobility.sf.lecton.xml2xsd2java.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.PREF_BOOLEAN_IMPLEMENTSERIALIZABLE, true);
	}

}
