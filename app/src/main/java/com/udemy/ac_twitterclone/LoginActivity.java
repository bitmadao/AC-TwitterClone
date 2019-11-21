package com.udemy.ac_twitterclone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseInstallation;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
