package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class TwitterCloneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_clone);

        if(ParseUser.getCurrentUser() == null) {
            startActivity(new Intent(TwitterCloneActivity.this,LoginActivity.class));
            finish();
        }
        ((TextView) findViewById(R.id.txtTwitterCloneActivity)).setText(ParseUser.getCurrentUser().getUsername());

    }
}
