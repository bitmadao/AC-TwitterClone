package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;

public class TweetActivity extends AppCompatActivity implements View.OnClickListener{

    private ParseUser currentUser;
    private boolean usesFanOf;
    private boolean hashMapMode;
    private EditText edtTweet;
    private Button btnTweet;
    private Button btnModeChange;
    private Button btnRefresh;

    private TextView txtCurrentUserTweets;
    private TextView txtFollowedUserTweets;

    private ListView currentUserTweetListView;
    private ListView followedUserTweetListView;


    private ArrayList<DisplayTweet> currentUserTweetsArrayList;
    private ArrayList<DisplayTweet> followedUserTweetsArrayList;

    private ArrayList<HashMap<String, String>> currentUserTweetsHashmap;
    private ArrayList<HashMap<String, String>> followedUserTweetsHashmap;

    private DisplayTweetAdapter currentUserTweetsAdapter;
    private DisplayTweetAdapter followedUserTweetsAdapter;

    private SimpleAdapter currentUserTweetsSimpleAdapter;
    private SimpleAdapter followedUserTweetsSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLogin();
        } else {
            currentUser = ParseUser.getCurrentUser();
        }

        usesFanOf = currentUser.getBoolean("usesFanOf");


        hashMapMode = false;

        setTitle(String.format("Sending tweet as %s",currentUser.getUsername()));

        edtTweet = findViewById(R.id.edtTweetActivityTweet);

        btnTweet = findViewById(R.id.btnTweetActivityTweet);
        btnTweet.setOnClickListener(TweetActivity.this);


        btnModeChange = findViewById(R.id.btnTweetActivityModeChange);
        btnModeChange.setOnClickListener(TweetActivity.this);

        btnRefresh = findViewById(R.id.btnTweetActivityRefresh);
        btnRefresh.setOnClickListener(TweetActivity.this);

        txtCurrentUserTweets = findViewById(R.id.txtTweetActivityTweetsCurrentUser);
        txtFollowedUserTweets= findViewById(R.id.txtTweetActivityTweetsFollowed);

        currentUserTweetListView = findViewById(R.id.activityTweetCurrentUserTweetListView);
        followedUserTweetListView = findViewById(R.id.activityTweetFollowedUserTweetListView);

        if(!usesFanOf){
            btnModeChange.setEnabled(false);
            btnModeChange.setVisibility(View.GONE);
        }

        getCurrentUserDisplayTweets();
        getFollowedUserDisplayTweets();



    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnTweetActivityTweet:
                btnTweetTapped();
                break;
            case R.id.btnTweetActivityModeChange:
                btnModeChangeTapped();
                break;
            case R.id.btnTweetActivityRefresh:
                btnRefreshTapped();
                break;

        }

    }

    private void btnTweetTapped() {
        if(edtTweet.getText().toString().isEmpty() || edtTweet.getText().toString().trim().length() < 1){
            Toast.makeText(this, getString(R.string.toast_activity_tweet_tweet_too_short), Toast.LENGTH_LONG).show();
            return;
        }

        ParseObject sendTweetObject = ParseObject.create("Tweet");
        sendTweetObject.put("senderId",currentUser.getObjectId());
        sendTweetObject.put("senderUsername",currentUser.getUsername());
        sendTweetObject.put("message",edtTweet.getText().toString());
        sendTweetObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(TweetActivity.this, getString(R.string.toast_activity_tweet_tweet_success), Toast.LENGTH_LONG).show();
                    getCurrentUserDisplayTweets();
                } else {
                    Log.i(APPTAG, e.getMessage());
                    Toast.makeText(TweetActivity.this, getString(R.string.generic_toast_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void btnModeChangeTapped(){
        hashMapMode = !hashMapMode;

        if(hashMapMode){
            btnModeChange.setText(R.string.btn_activity_tweet_mode_use_display_tweet);
            getCurrentUserHashMapTweets();
            getFollowedUserHashMapTweets();
        } else {
            btnModeChange.setText(R.string.btn_activity_tweet_mode_use_hashmaps);
            getCurrentUserDisplayTweets();
            getFollowedUserDisplayTweets();
        }
    }

    private void btnRefreshTapped(){
        Toast.makeText(TweetActivity.this, "Refreshing posts...", Toast.LENGTH_LONG).show();
        getCurrentUserDisplayTweets();
        getFollowedUserDisplayTweets();
    }

    private void transitionToLogin(){
        startActivity(new Intent(TweetActivity.this,LoginActivity.class));
    }

    private void getCurrentUserDisplayTweets(){
        currentUserTweetsArrayList = new ArrayList<>();
        ParseQuery<ParseObject> currentUserTweetsQuery = ParseQuery.getQuery("Tweet");
        currentUserTweetsQuery.whereEqualTo("senderId",currentUser.getObjectId());
        currentUserTweetsQuery.orderByDescending("createdAt");
        currentUserTweetsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        txtCurrentUserTweets.setText("Your tweets:");
                        for(ParseObject tweetObject : objects){
                            currentUserTweetsArrayList.add(new DisplayTweet(tweetObject));
                        }
                        currentUserTweetsAdapter = new DisplayTweetAdapter(TweetActivity.this, currentUserTweetsArrayList);
                        populateListView(currentUserTweetListView, currentUserTweetsAdapter);
                    } else {
                        txtCurrentUserTweets.setText("No tweets from you yet");
                    }
                }
            }
        });
    }

    private void getCurrentUserHashMapTweets(){
        currentUserTweetsHashmap = new ArrayList<>();

        final ParseQuery<ParseObject> currentUserTweetsQuery = ParseQuery.getQuery("Tweet");
        currentUserTweetsQuery.whereEqualTo("senderUsername",currentUser.getUsername());
        currentUserTweetsQuery.orderByDescending("createdAt");

        currentUserTweetsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject tweetObject: objects){
                            HashMap<String, String> tweetHashMap = new HashMap<>();
                            tweetHashMap.put("tweetUser",(String) tweetObject.get("senderUsername"));
                            tweetHashMap.put("tweetMessage",(String) tweetObject.get("message"));
                            currentUserTweetsHashmap.add(tweetHashMap);
                        }
                        currentUserTweetsSimpleAdapter = new SimpleAdapter(TweetActivity.this,
                                currentUserTweetsHashmap,
                                android.R.layout.simple_list_item_2,
                                new String[]{"tweetUser", "tweetMessage"},
                                new int[]{android.R.id.text1,android.R.id.text2});
                        populateListView(currentUserTweetListView,currentUserTweetsSimpleAdapter);
                    }
                }
            }
        });

    }

    private void getFollowedUserDisplayTweets(){
        followedUserTweetsArrayList = new ArrayList<>();
        final ParseQuery<ParseObject> followedUserTweetsQuery = ParseQuery.getQuery("Tweet");
        final FindCallback<ParseObject> followedUserTweetsQueryFindCallback = new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if(objects.size() > 0){
                        txtFollowedUserTweets.setText("Tweets from users you follow:");
                        for(ParseObject followedUserTweet: objects){
                            followedUserTweetsArrayList.add(new DisplayTweet(followedUserTweet));
                        }

                        followedUserTweetsAdapter = new DisplayTweetAdapter(TweetActivity.this, followedUserTweetsArrayList);
                        populateListView(followedUserTweetListView,followedUserTweetsAdapter);

                    } else {
                        txtFollowedUserTweets.setText("No posts from your followed users yet");
                    }
                }
            }
        };

        if(!usesFanOf){

            ParseQuery<ParseObject> followingQuery = ParseQuery.getQuery("Follower");
            followingQuery.whereEqualTo("followerId",currentUser.getObjectId());
            followingQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        if(objects.size() > 0) {
                            ArrayList<String> followingUserIdArrayList = new ArrayList<>();

                            for(ParseObject followedUserObject :objects){
                                followingUserIdArrayList.add((String)followedUserObject.get("userId"));
                            }

                            followedUserTweetsQuery.whereContainedIn("senderId",followingUserIdArrayList);
                            followedUserTweetsQuery.orderByDescending("createdAt");

                            followedUserTweetsQuery.findInBackground(followedUserTweetsQueryFindCallback);


                        } else {
                            txtFollowedUserTweets.setText("Tweets for users you follow go here");

                        }
                    }
                }
            });

        } else {
            if(currentUser.getList("fanOf") != null && currentUser.getList("fanOf").size() > 0){

                List fanOfList = currentUser.getList("fanOf");
                followedUserTweetsQuery.whereContainedIn("senderUsername", fanOfList);
                followedUserTweetsQuery.orderByDescending("createdAt");

                followedUserTweetsQuery.findInBackground(followedUserTweetsQueryFindCallback);

            } else {
                txtFollowedUserTweets.setText("Tweets for users you follow go here");
            }
        }


    }

    private void getFollowedUserHashMapTweets(){
        followedUserTweetsHashmap = new ArrayList<>();

        ParseQuery<ParseObject> followedUserParseQuery = ParseQuery.getQuery("Tweet");
        List fanOfList = currentUser.getList("fanOf");
        followedUserParseQuery.whereContainedIn("senderUsername",fanOfList);
        followedUserParseQuery.orderByDescending("createdAt");

        followedUserParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject tweetObject: objects){
                            HashMap<String,String> tweetHashMap = new HashMap<>();
                            tweetHashMap.put("tweetUser",(String) tweetObject.get("senderUsername"));
                            tweetHashMap.put("tweetMessage",(String) tweetObject.get("message"));
                            followedUserTweetsHashmap.add(tweetHashMap);
                        }

                        followedUserTweetsSimpleAdapter = new SimpleAdapter(
                                TweetActivity.this,
                                followedUserTweetsHashmap,
                                android.R.layout.simple_list_item_2,
                                new String[]{"tweetUser","tweetMessage"},
                                new int[]{android.R.id.text1, android.R.id.text2}
                        );
                        populateListView(followedUserTweetListView,followedUserTweetsSimpleAdapter);
                    }
                }
            }
        });


    }

    private void populateListView(ListView listView, DisplayTweetAdapter displayTweetAdapter){
        listView.setAdapter(displayTweetAdapter);

    }

    private void populateListView(ListView listView, SimpleAdapter simpleAdapter){
        listView.setAdapter(simpleAdapter);
    }
}
