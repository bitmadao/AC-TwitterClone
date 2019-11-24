package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.*;

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
        logoutParseUser(this,TwitterUsersActivity.this,LoginActivity.class);

    }
    private void transitionToLoginActivity() {
        startActivity(new Intent(TwitterUsersActivity.this,LoginActivity.class));
        finish();
    }
}
