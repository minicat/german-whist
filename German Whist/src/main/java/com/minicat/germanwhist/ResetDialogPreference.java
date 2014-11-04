package com.minicat.germanwhist;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class ResetDialogPreference extends DialogPreference {
    public ResetDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // deal with persisting your values here
            persistBoolean(positiveResult);
        }
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