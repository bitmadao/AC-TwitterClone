package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;

public class TwitterUsersActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLoginActivity();
        }
        TextView textView = findViewById(R.id.txtTwitterCloneActivity);

        textView.setText(ParseUser.getCurrentUser().getUsername());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_twitter_users_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItemTwitterUsersLogout:
                menuItemLogoutTapped();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }

    private void menuItemLogoutTapped(){
        final String userName = ParseUser.getCurrentUser().getUsername();
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(
                            TwitterUsersActivity.this,
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
                            TwitterUsersActivity.this,
                            getString(R.string.generic_toast_error),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }
    private void transitionToLoginActivity() {
        startActivity(new Intent(TwitterUsersActivity.this,LoginActivity.class));
        finish();
    }
}
