package com.minicat.germanwhist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;

public class SettingsActivity extends PreferenceActivity {
    SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        // Need to get shared preferences now, so that the resetdialog can access it.
        mPrefs = getSharedPreferences(StatsHelper.PREFS_NAME, 0);

    }

    public static class ResetDialogPreference extends DialogPreference {
        public ResetDialogPreference(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        // Handle OK.
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //StatsHelper.resetStats(mPrefs);
            }
        }
    }
}
