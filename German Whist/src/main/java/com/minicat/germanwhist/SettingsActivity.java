package com.minicat.germanwhist;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

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
}
