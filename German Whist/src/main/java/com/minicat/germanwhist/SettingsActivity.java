package com.minicat.germanwhist;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
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

    /* http://stackoverflow.com/questions/5365310/creating-a-dialogpreference-from-xml/8818446#8818446
     Note: this does mean that reset isnt instant, but since onPause should always be called
     before exit, should be fine. */
    @Override
    protected void onPause() {
        super.onPause();
        Preference prefReset = findPreference(getResources().getString(R.string.pref_reset));
        if (prefReset.getSharedPreferences().getBoolean(prefReset.getKey(), false)) {
            // apply reset, and then set the pref-value back to false
            StatsHelper.resetStats(getSharedPreferences(StatsHelper.PREFS_NAME, 0));
            prefReset.getEditor().putBoolean(prefReset.getKey(), false);
        }
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
