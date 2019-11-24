package com.udemy.ac_twitterclone;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ACTwitterCloneTools {

    public static String APPTAG ="ACTC_TAG";

    private ACTwitterCloneTools(){
        // not instantiable
    }

    public static ProgressBar createGoneProgressBar(Context context, ConstraintLayout constraintLayout) {
        ProgressBar progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleLarge);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(150,150);
        constraintLayout.addView(progressBar,params);
        progressBar.setVisibility(View.GONE);

        return progressBar;
    }

    public static void hideSoftKeyboardOnTap(View view, Context context) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(Exception e) {
            Log.i(APPTAG,e.getMessage());
            Toast.makeText(
                    context,
                    context.getString(R.string.generic_toast_error),
                    Toast.LENGTH_LONG)
                .show();
        }

    }
}
