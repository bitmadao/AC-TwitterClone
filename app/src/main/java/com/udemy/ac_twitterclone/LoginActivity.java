package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.*;
import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;
import static com.udemy.ac_twitterclone.ACTwitterCloneTools.createGoneProgressBar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ConstraintLayout constraintLayout;
    private TextInputEditText edtUsername, edtPassword;

    private Button btnLogin, btnNeedAccount;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        constraintLayout = findViewById(R.id.activityLoginConstraintLayout);

        edtUsername = findViewById(R.id.textInputEditTextLoginActivityUsername);
        edtPassword = findViewById(R.id.textInputEditTextLoginActivityPassword);

        btnLogin = findViewById(R.id.btnLoginActivityLogin);
        btnNeedAccount = findViewById(R.id.btnLoginActivityLoginNeedAccount);

        constraintLayout.setOnClickListener(LoginActivity.this);
        btnLogin.setOnClickListener(LoginActivity.this);
        btnNeedAccount.setOnClickListener(LoginActivity.this);

        if(ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this,TwitterCloneActivity.class));
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activityLoginConstraintLayout:
                hideSoftKeyboardOnTap(constraintLayout,LoginActivity.this);
                break;

            case R.id.btnLoginActivityLogin:
                btnLoginTapped();
                break;

            case R.id.btnLoginActivityLoginNeedAccount:
                btnNeedAccountTapped();
                break;
        }
    }

    private void btnLoginTapped() {
        if(edtUsername.getText().toString() == null || edtPassword.getText().toString() == null){
            Toast.makeText(LoginActivity.this,
                    "Please provide a username AND password...",
                    Toast.LENGTH_LONG)
                .show();
            return;
        }


        progressBar = createGoneProgressBar(LoginActivity.this,constraintLayout);
        progressBar.setVisibility(View.VISIBLE);
        ParseUser.logInInBackground(
                edtUsername.getText().toString(),
                edtPassword.getText().toString(),
                new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e == null) {
                        Toast.makeText(
                                LoginActivity.this,
                                String.format(
                                        getString(R.string.toast_activity_login_login_success),
                                        user.getUsername()
                                ),
                                Toast.LENGTH_LONG)
                            .show();

                        startActivity(new Intent(LoginActivity.this,TwitterCloneActivity.class));
                        finish();
                    } else if (e.getMessage().equals("Invalid username/password.")) {
                        Toast.makeText(
                                LoginActivity.this,
                                getString(R.string.toast_activity_login_login_invalid),
                                Toast.LENGTH_SHORT)
                            .show();

                    } else {
                        Log.i(APPTAG,e.getMessage());
                        Toast.makeText(
                                LoginActivity.this,
                                getString(R.string.generic_toast_error),
                                Toast.LENGTH_LONG)
                            .show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
        });

    }

    private void btnNeedAccountTapped() {
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        finish();

    }
}
