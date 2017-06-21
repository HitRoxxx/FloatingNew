package com.theLoneWarrior.floating.preference;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;

import com.theLoneWarrior.floating.R;

public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.app_preferences);
    }




    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key),key);

    }

   /* private void change(String key) {
        Preference connectionPref = findPreference(key);
        // Set summary to be the user-description for the selected value
        String str = String.valueOf(connectionPref.getSummary());
        Toast.makeText(getActivity(), ""+str, Toast.LENGTH_SHORT).show();
        if(str.equals("Default View")) {
            connectionPref.setSummary("Default View");
            // connectionPref.setDefaultValue(false);
            connectionPref.setIcon(R.mipmap.ic_launcher);
        }
        else
        {
            connectionPref.setSummary("Icon View");
            // connectionPref.setDefaultValue(false);
            connectionPref.setIcon(R.drawable.search);

        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
       getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey());
                }
            } else {
                updatePreference(preference, preference.getKey());
            }
        }
    }

    private void updatePreference(Preference preference, String key) {
        if (preference == null) return;
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;

            if (key.equals("OutputView")) {
                if (getPreferenceManager().getSharedPreferences().getBoolean("OutputView", false)) {
                    switchPreference.setIcon(R.mipmap.ic_launcher);
                    switchPreference.setSummary("Default View");
                } else {
                    switchPreference.setIcon(R.drawable.center);
                    switchPreference.setSummary("Icon View");
                }
                return;
            }
        }
     //   SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
      // preference.setSummary(sharedPrefs.getString(key, "Default"));
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
