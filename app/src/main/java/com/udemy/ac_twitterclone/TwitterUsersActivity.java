package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;
import static com.udemy.ac_twitterclone.ACTwitterCloneTools.logoutParseUser;

public class TwitterUsersActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView usersListView;
    private ArrayList<String> usersArrayList;
    private ArrayAdapter<String> arrayAdapter;

    private float usersListViewAlphaValue; // use for animations during populateUsersScrollView()
    private TextView txtLoadingUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLoginActivity();
        }

        usersArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(TwitterUsersActivity.this,android.R.layout.simple_list_item_1,usersArrayList);

        usersListView = findViewById(R.id.activityTwitterUsersListView);
        usersListViewAlphaValue = usersListView.getAlpha();
        usersListView.setAlpha(0); // animate return to correct alpha value during populateUsersScrollView()
        txtLoadingUsers = findViewById(R.id.txtActivityTwitterUsersLoading);

        populateUsersScrollView();


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

    private void populateUsersScrollView() {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if (objects.size() > 0){
                        for(ParseUser user: objects){
                            usersArrayList.add(user.getUsername());
                        }
                        usersListView.setAdapter(arrayAdapter);
                        txtLoadingUsers.animate().alpha(0).setDuration(2000).start();
                        usersListView.animate().alpha(usersListViewAlphaValue).setDuration(3000).start();

                    } else {
                        Toast.makeText(TwitterUsersActivity.this, "No users", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.i(APPTAG,e.getMessage());
                }
            }
        });
    }
}
