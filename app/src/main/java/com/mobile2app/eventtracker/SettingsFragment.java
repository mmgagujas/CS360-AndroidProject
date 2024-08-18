package com.mobile2app.eventtracker;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Settings screen for changing preferences and account details.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}