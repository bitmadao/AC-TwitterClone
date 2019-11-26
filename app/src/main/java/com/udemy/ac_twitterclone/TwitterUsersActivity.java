package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;
import static com.udemy.ac_twitterclone.ACTwitterCloneTools.logoutParseUser;

public class TwitterUsersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ParseUser currentUser;

    private ListView usersListView;
    private ArrayList<TwitterUsersActivityListUser> usersArrayList;
    private ArrayAdapter<TwitterUsersActivityListUser> arrayAdapter;
    private ArrayList<String> currentUserFollowingArrayList;

    private float usersListViewAlphaValue; // use for animations during populateUsersScrollView()
    private TextView txtLoadingUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLoginActivity();
        } {
            currentUser = ParseUser.getCurrentUser();
        }

        usersArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(TwitterUsersActivity.this,android.R.layout.simple_list_item_checked,usersArrayList);

        usersListView = findViewById(R.id.activityTwitterUsersListView);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        usersListView.setOnItemClickListener(TwitterUsersActivity.this);
        usersListViewAlphaValue = usersListView.getAlpha();
        usersListView.setAlpha(0); // animate return to correct alpha value during populateUsersScrollView()
        txtLoadingUsers = findViewById(R.id.txtActivityTwitterUsersLoading);


        currentUserFollowingArrayList = new ArrayList<>();
        updateFollowingArrayAndPopulateListView(); // query ParseServer for which users currentUser is following, call populateUsersScrollView() inside method

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_twitter_users_menu, menu);

        if(currentUser != null) {
            menu.getItem(0).setTitle(
                    String.format(
                            getString(R.string.menu_item_activity_twitter_users_current_user),
                            currentUser.getUsername()
                )
            );
        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;

        if(checkedTextView.isChecked()){
            followUser(checkedTextView);
        } else {
            unFollowUser(checkedTextView);
        }


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
        parseQuery.whereNotEqualTo("username", currentUser.getUsername());

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if (objects.size() > 0){
                        for(ParseUser user: objects){
                            boolean followingUser = false;
                            if(currentUserFollowingArrayList.contains(user.getObjectId())) {
                                followingUser = true;
                            }

                            usersArrayList.add(new TwitterUsersActivityListUser(user.getUsername(),followingUser));
                        }
                        usersListView.setAdapter(arrayAdapter);

                        for(int i = 0; i < usersArrayList.size(); i ++){
                            usersListView.setItemChecked(i,usersArrayList.get(i).isCurrentUserFollowing());
                        }

                        txtLoadingUsers.animate().alpha(0).setDuration(2000).start();
                        usersListView.animate().alpha(usersListViewAlphaValue).setDuration(3000).start();

                    } else {
                        Toast.makeText(
                                TwitterUsersActivity.this,
                                "No users", // TODO strings.xml
                                Toast.LENGTH_LONG
                        ).show();
                    }

                } else {
                    Log.i(APPTAG,e.getMessage());
                }
            }
        });
    }

    private void updateFollowingArrayAndPopulateListView(){
        ParseQuery<ParseObject> followingArrayQuery = new ParseQuery<>("Follower");
        followingArrayQuery.whereEqualTo("followerId",currentUser.getObjectId());
        followingArrayQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if(objects.size() > 0) {
                        for(ParseObject object: objects){
                            currentUserFollowingArrayList.add((String) object.get("userId"));
                        }
                    }
                    populateUsersScrollView();
                }
            }

        });

    }

    private void followUser(final CheckedTextView checkedTextView){
        final String username = checkedTextView.getText().toString();
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo("username",username);
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        final ParseUser userToFollow = objects.get(0);
                        Log.i(APPTAG, "" + userToFollow.getObjectId());
                        ParseObject parseObject = new ParseObject("Follower");
                        parseObject.put("userId",userToFollow.getObjectId());
                        parseObject.put("followerId",currentUser.getObjectId());

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){

                                    Toast.makeText(
                                            TwitterUsersActivity.this,
                                            String.format(
                                                    "%s followed successfully", // TODO strings.xml
                                                    username),
                                            Toast.LENGTH_LONG
                                        ).show();

                                } else {
                                    Log.i(APPTAG, e.getMessage());
                                    Toast.makeText(
                                            TwitterUsersActivity.this,
                                            getString(R.string.generic_toast_error),
                                            Toast.LENGTH_LONG
                                        ).show();
                                    checkedTextView.setChecked(false);
                                }
                            }
                        });
                    }
                } else {
                    e.getMessage();
                    Toast.makeText(
                            TwitterUsersActivity.this,
                            getString(R.string.generic_toast_error),
                            Toast.LENGTH_LONG
                    ).show();
                    checkedTextView.setChecked(false);
                }
            }
        });
    }

    public void unFollowUser(final CheckedTextView checkedTextView){
        final String username = checkedTextView.getText().toString();
        ParseQuery<ParseUser> userToUnFollowParseQuery = ParseUser.getQuery();
        userToUnFollowParseQuery.whereEqualTo("username",username);
        userToUnFollowParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        ParseUser userToUnFollow = objects.get(0);
                        ParseQuery<ParseObject> followerRowToDeleteQuery = ParseQuery.getQuery("Follower");
                        followerRowToDeleteQuery.whereEqualTo("userId",userToUnFollow.getObjectId());
                        followerRowToDeleteQuery.whereEqualTo("followerId",currentUser.getObjectId());
                        followerRowToDeleteQuery.setLimit(1);
                        followerRowToDeleteQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e == null) {
                                    if(objects.size() > 0) {
                                        objects.get(0).deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null) {
                                                    Toast.makeText(
                                                            TwitterUsersActivity.this,
                                                            String.format("%s unfollowed successfully", username), //TODO strings.xml
                                                            Toast.LENGTH_LONG
                                                        ).show();
                                                } else {
                                                    Log.i(APPTAG, e.getMessage());
                                                    Toast.makeText(
                                                            TwitterUsersActivity.this,
                                                            getString(R.string.generic_toast_error),
                                                            Toast.LENGTH_LONG
                                                        ).show();
                                                    checkedTextView.setChecked(true);
                                                }
                                            }
                                        });
                                    } else {
                                        Log.i(APPTAG,"objects size from followerRowToDeleteQuery is " + objects.size());
                                        Toast.makeText(
                                                TwitterUsersActivity.this,
                                                getString(R.string.generic_toast_error),
                                                Toast.LENGTH_LONG
                                        ).show();
                                        checkedTextView.setChecked(true);
                                    }
                                } else {
                                    Log.i(APPTAG, e.getMessage());
                                    Toast.makeText(
                                            TwitterUsersActivity.this,
                                            getString(R.string.generic_toast_error),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    checkedTextView.setChecked(true);
                                }
                            }
                        });

                    }
                } else {
                    Log.i(APPTAG, e.getMessage());
                    Toast.makeText(
                            TwitterUsersActivity.this,
                            getString(R.string.generic_toast_error),
                            Toast.LENGTH_LONG
                    ).show();
                    checkedTextView.setChecked(true);
                }
            }
        });
    }
}
