package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseInstallation;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnLogin, btnNeedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        btnLogin = findViewById(R.id.btnLoginActivityLogin);
        btnNeedAccount = findViewById(R.id.btnLoginActivityLoginNeedAccount);

        btnLogin.setOnClickListener(LoginActivity.this);
        btnNeedAccount.setOnClickListener(LoginActivity.this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginActivityLogin:
                btnLoginTapped();
                break;

            case R.id.btnLoginActivityLoginNeedAccount:
                btnNeedAccountTapped();
                break;
        }
    }

    private void btnLoginTapped() {

    }

    private void btnNeedAccountTapped() {
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        finish();

    }
}
