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
    private boolean usesFanOf;

    private ListView usersListView;
    private ArrayList<TwitterUsersActivityListUser> usersArrayList;
    private ArrayAdapter<TwitterUsersActivityListUser> arrayAdapterTwitterUsersActivityListUser;
    private ArrayAdapter<String> arrayAdapterString;
    private ArrayList<String> currentUserFollowingArrayList;
    private ArrayList<String> currentUserFanOfArrayList;

    private float usersListViewAlphaValue; // use for animations during populateUsersScrollView()
    private TextView txtLoadingUsers;

    // Course references
    private ArrayList<String> tUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLoginActivity();
        } {
            currentUser = ParseUser.getCurrentUser();
            usesFanOf = currentUser.getBoolean("fanOf");
        }

        usersArrayList = new ArrayList<>();
        tUsers = new ArrayList<>();

        if(!usesFanOf) {
            arrayAdapterTwitterUsersActivityListUser = new ArrayAdapter<>(
                    TwitterUsersActivity.this,
                    android.R.layout.simple_list_item_checked,
                    usersArrayList);
        } else {
            arrayAdapterString = new ArrayAdapter<>(
                    TwitterUsersActivity.this,
                    android.R.layout.simple_list_item_checked,
                    tUsers);

        }
        usersListView = findViewById(R.id.activityTwitterUsersListView);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        usersListView.setOnItemClickListener(TwitterUsersActivity.this);
        usersListViewAlphaValue = usersListView.getAlpha();
        usersListView.setAlpha(0); // animate return to correct alpha value during populateUsersScrollView()
        txtLoadingUsers = findViewById(R.id.txtActivityTwitterUsersLoading);

        currentUserFollowingArrayList = new ArrayList<>();
        currentUserFanOfArrayList = new ArrayList<>();
        updateCurrentUserFollowingArraysAndPopulateListView(); // query ParseServer for which users currentUser is following, call populateUsersScrollView() inside method


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

        if(!usesFanOf) {
            if (checkedTextView.isChecked()) {
                followUser(checkedTextView);
            } else {
                unFollowUser(checkedTextView);
            }
        } else {
            if (checkedTextView.isChecked()) {
                followUserFanOf(checkedTextView, position);
            } else {
                unFollowUserFanOf(checkedTextView, position);
            }
        }

    }
    private void menuItemLogoutTapped(){
        logoutParseUser(this,TwitterUsersActivity.this,LoginActivity.class);

    }

    private void transitionToLoginActivity() {
        startActivity(new Intent(TwitterUsersActivity.this,LoginActivity.class));
        finish();
    }

    private void updateCurrentUserFollowingArraysAndPopulateListView(){

        if(!usesFanOf) {
            ParseQuery<ParseObject> followingArrayQuery = new ParseQuery<>("Follower");
            followingArrayQuery.whereEqualTo("followerId", currentUser.getObjectId());
            followingArrayQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseObject object : objects) {
                                currentUserFollowingArrayList.add((String) object.get("userId"));
                            }
                        }
                        populateUsersScrollView();
                    }
                }

            });
        } else {
            if(currentUser.getList("fanOf") != null) {
                List fanOfList =  currentUser.getList("fanOf");
                currentUserFanOfArrayList.addAll(fanOfList);

                populateUsersScrollView();
            }
        }

    }

    private void populateUsersScrollView() {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username", currentUser.getUsername());

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            boolean followingUser = false;
                            if (currentUserFollowingArrayList.contains(user.getObjectId())) {
                                followingUser = true;
                            }

                            tUsers.add(user.getUsername());

                            usersArrayList.add(new TwitterUsersActivityListUser(user.getUsername(), followingUser));
                        }


                        if (!usesFanOf){

                            usersListView.setAdapter(arrayAdapterTwitterUsersActivityListUser);

                            for (int i = 0; i < usersArrayList.size(); i++) {
                                usersListView.setItemChecked(i, usersArrayList.get(i).isCurrentUserFollowing());
                            }

                        } else {
                            usersListView.setAdapter(arrayAdapterString);

                            for (String twitterUser : tUsers) {
                                usersListView.setItemChecked(
                                        tUsers.indexOf(twitterUser),
                                        currentUserFanOfArrayList.contains(twitterUser)
                                    );
                            }
                        }

                        txtLoadingUsers.animate().alpha(0).setDuration(2000).start();
                        usersListView.animate().alpha(usersListViewAlphaValue).setDuration(3000).start();

                    } else {
                        Toast.makeText(
                                TwitterUsersActivity.this,
                                getString(R.string.toast_activity_twitter_users_no_users),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                } else {
                    Log.i(APPTAG,e.getMessage());
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
                                                    getString(R.string.toast_activity_twitter_users_follow_success),
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

    private void followUserFanOf(final CheckedTextView checkedTextView, final int position){
        currentUser.add("fanOf",usersArrayList.get(position).getName());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(
                            TwitterUsersActivity.this,
                            String.format(
                                    getString(R.string.toast_activity_twitter_users_follow_success),
                                    checkedTextView.getText().toString()
                                ),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void unFollowUser(final CheckedTextView checkedTextView){
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
                                                            String.format(
                                                                    getString(R.string.toast_activity_twitter_users_un_follow_success),
                                                                    username
                                                                ),
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

    private void unFollowUserFanOf(final CheckedTextView checkedTextView, final int position) {

        currentUser.getList("fanOf").remove(usersArrayList.get(position).getName());

        List currentUserFanOfList = currentUser.getList("fanOf");
        currentUser.remove("fanOf");
        currentUser.put("fanOf", currentUserFanOfList);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(
                            TwitterUsersActivity.this,
                            String.format(
                                    getString(R.string.toast_activity_twitter_users_un_follow_success),
                                    checkedTextView.getText().toString()
                                ),
                            Toast.LENGTH_LONG
                        ).show();
                }
            }
        });

    }
}
