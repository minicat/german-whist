package com.minicat.germanwhist;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * helper stuff for saving stats
 * in the future, for more in-depth stats stuff
 */
public class StatsHelper {
    /* preferences */
    public static final String PREFS_NAME = "GermanWhistPrefs";
    public static final String PREFS_GAMES = "games";
    public static final String PREFS_WINS = "wins";
    public static final String PREFS_FORFEIT = "forfeit";

    public static final String TAG = "StatsHelper";

    public static void resetStats(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREFS_GAMES, 0).putInt(PREFS_WINS, 0).putInt(PREFS_FORFEIT, 0).commit();
        Log.d(TAG, "Reset game statistics.");
    }

    public static void incrementCount(SharedPreferences prefs, String key) {
        SharedPreferences.Editor editor = prefs.edit();
        int val = prefs.getInt(key, 0);
        editor.putInt(key, val + 1).apply();
        Log.d(TAG, "Incremented " + key);
    }

}
