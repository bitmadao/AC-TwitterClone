package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;
import static com.udemy.ac_twitterclone.ACTwitterCloneTools.createGoneProgressBar;
import static com.udemy.ac_twitterclone.ACTwitterCloneTools.hideSoftKeyboard;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    private ConstraintLayout constraintLayout;
    private ProgressBar progressBar;

    private TextInputEditText edtUsername, edtEmail, edtPassword, edtPasswordConfirm;

    private Button  btnSignUp, btnHaveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        constraintLayout = findViewById(R.id.activitySignUpConstraintLayout);
        progressBar = createGoneProgressBar(SignUpActivity.this,constraintLayout);

        edtUsername = findViewById(R.id.textInputEditTextSignUpActivityUsername);
        edtEmail = findViewById(R.id.textInputEditTextSignUpActivityEmail);
        edtPassword = findViewById(R.id.textInputEditTextSignUpActivityPassword);
        edtPasswordConfirm = findViewById(R.id.textInputEditTextSignUpActivityPasswordConfirm);

        btnSignUp = findViewById(R.id.btnSignUpActivitySignUp);
        btnHaveAccount = findViewById(R.id.btnSignUpActivityHaveAccount);

        constraintLayout.setOnClickListener(SignUpActivity.this);
        btnSignUp.setOnClickListener(SignUpActivity.this);
        btnHaveAccount.setOnClickListener(SignUpActivity.this);

        edtPasswordConfirm.setOnKeyListener(SignUpActivity.this);

        if(ParseUser.getCurrentUser() != null){
            transitionToTwitterUsersActivity();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.activitySignUpConstraintLayout:
                hideSoftKeyboard(SignUpActivity.this,constraintLayout);
                break;
            case R.id.btnSignUpActivitySignUp:
                btnSignUpTapped();
                break;

            case R.id.btnSignUpActivityHaveAccount:
                btnHaveAccountTapped();
                break;
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            hideSoftKeyboard(SignUpActivity.this,constraintLayout);
            onClick(btnSignUp);
        }

        return false;
    }

    private void btnSignUpTapped() {
        boolean objection = false;
        StringBuilder objectionStringBuilder = new StringBuilder();
        objectionStringBuilder.append(getString(R.string.toast_activity_sign_up_objection_string_start));

        if(edtUsername.getText().toString().isEmpty()){
            objection = true;
            objectionStringBuilder.append(getString(R.string.toast_activity_sign_up_objection_string_username));
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
            objection = true;
            objectionStringBuilder.append(getString(R.string.toast_activity_sign_up_objection_string_email));
        }

        if(edtPassword.getText().toString().isEmpty()) {
            objection = true;
            objectionStringBuilder.append(getString(R.string.toast_activity_sign_up_objection_string_password));
        } else if (!edtPassword.getText().toString().equals(edtPasswordConfirm.getText().toString())){
            objection = true;
            objectionStringBuilder.append(getString(R.string.toast_activity_sign_up_objection_string_password_confirm));
        }

        if (objection){
            Toast.makeText(SignUpActivity.this,
                    objectionStringBuilder.toString(),
                    Toast.LENGTH_SHORT)
                .show();
        } else {
            final ParseUser parseUser = new ParseUser();
            parseUser.setUsername(edtUsername.getText().toString());
            parseUser.setEmail(edtEmail.getText().toString());
            parseUser.setPassword(edtPassword.getText().toString());

            progressBar.setVisibility(View.VISIBLE);

            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Toast.makeText(
                                SignUpActivity.this,
                                String.format(
                                        getString(R.string.toast_activity_sign_up_sign_up_success),
                                        parseUser.getUsername()
                                ),
                                Toast.LENGTH_SHORT)
                            .show();
                        transitionToTwitterUsersActivity();

                    } else if (e.getMessage().equals("Account already exists for this username.")) {
                        Toast.makeText(
                                SignUpActivity.this,
                                String.format(
                                        getString(R.string.toast_activity_sign_up_sign_up_error_username),
                                        edtUsername.getText().toString()
                                    ),
                                Toast.LENGTH_LONG)
                            .show();
                    } else {
                        Log.i(APPTAG, e.getMessage());
                        Toast.makeText(
                                SignUpActivity.this,
                                getString(R.string.generic_toast_error),
                                Toast.LENGTH_LONG)
                            .show();

                    }

                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void btnHaveAccountTapped() {
        progressBar.setVisibility(View.VISIBLE);
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        progressBar.setVisibility(View.GONE);
        finish();
    }

    private void transitionToTwitterUsersActivity() {
        startActivity(new Intent(SignUpActivity.this,TwitterUsersActivity.class));
        finish();
    }
}
