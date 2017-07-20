package com.theLoneWarrior.floating.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.SelectedApplication;

/**
 * Created by HitRo on 21-06-2017.
 */

public class PreferenceDialog extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_preference);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.settingFrgment, new Setting()).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent newIntent = new Intent(PreferenceDialog.this, SelectedApplication.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //   newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(new Intent(newIntent));
    }

    public static class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.app_preferences);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key), key);

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



}
