package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;

public class TwitterCloneActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_clone);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLoginActivity();
        }
        TextView textView = findViewById(R.id.txtTwitterCloneActivity);

        textView.setText(ParseUser.getCurrentUser().getUsername());
        textView.setOnClickListener(TwitterCloneActivity.this);

    }

    @Override
    public void onClick(View v) {
        final String userName = ParseUser.getCurrentUser().getUsername();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(
                            TwitterCloneActivity.this,
                            String.format(
                                    "%s logged out successfully",
                                    userName
                                ),
                            Toast.LENGTH_SHORT)
                        .show();

                    transitionToLoginActivity();
                } else {
                    Log.i(APPTAG,e.getMessage());
                    Toast.makeText(
                            TwitterCloneActivity.this,
                            getString(R.string.generic_toast_error),
                            Toast.LENGTH_SHORT)
                        .show();
                }
            }
        });
    }

    private void transitionToLoginActivity() {
        startActivity(new Intent(TwitterCloneActivity.this,LoginActivity.class));
        finish();
    }
}
