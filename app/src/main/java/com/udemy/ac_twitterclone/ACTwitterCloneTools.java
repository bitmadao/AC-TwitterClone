package com.udemy.ac_twitterclone;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

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
}
