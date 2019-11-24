package com.udemy.ac_twitterclone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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

    public static void hideSoftKeyboard(Context context, View activityLayout) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activityLayout.getWindowToken(), 0);
        } catch(Exception e) {
            Log.i(APPTAG,e.getMessage());
            Toast.makeText(
                    context,
                    context.getString(R.string.generic_toast_error),
                    Toast.LENGTH_LONG)
                .show();
        }

    }

    public static void logoutParseUser(final Activity activity, final Context context, final Class<?> transitionClass){
        final String userName = ParseUser.getCurrentUser().getUsername();

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(
                            context,
                            String.format(
                                    "%s logged out successfully", //TODO strings.xml
                                    userName
                            ),
                            Toast.LENGTH_SHORT)
                            .show();
                    context.startActivity(new Intent(context, transitionClass));
                    activity.finish();



                } else {
                    Log.i(APPTAG,e.getMessage());
                    Toast.makeText(
                            context,
                            context.getString(R.string.generic_toast_error),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }
}
