package com.minicat.germanwhist;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {
    SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        // Need to get shared preferences now, so that the resetdialog can access it.
        mPrefs = getSharedPreferences(StatsHelper.PREFS_NAME, 0);

        // want to have the back button there
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // go back
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class ResetDialogPreference extends DialogPreference {
        public ResetDialogPreference(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onDialogClosed(boolean positiveResult) {
            super.onDialogClosed(positiveResult);
            persistBoolean(positiveResult);
        }

        @Override
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);
        }

        // Handle OK.
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //StatsHelper.resetStats(mPrefs);
            }
        }
    }
}
