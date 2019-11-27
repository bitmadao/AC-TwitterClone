package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;

public class TroughActivity extends AppCompatActivity {

    private ParseUser currentUser;

    private ConstraintLayout constraintLayout;
    private ListView troughListView;
    private ArrayList twitterTroughArrayList;
    private ArrayAdapter arrayAdapter;

    private ArrayList<String> currentUserFollowsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trough);

        if(ParseUser.getCurrentUser() != null){
            currentUser = ParseUser.getCurrentUser();
        } else {
            startActivity(new Intent(TroughActivity.this,LoginActivity.class));
            finish();
        }

        setTitle(
                String.format(
                        "Trough for %s",
                        currentUser.getUsername()
                    )
            );




        constraintLayout = findViewById(R.id.activityTroughConstraintLayout);
        troughListView = findViewById(R.id.activityTroughListView);

        twitterTroughArrayList = new ArrayList();
        currentUserFollowsArrayList = new ArrayList<>();

        fillTrough();



    }

    private void fillTrough(){

        ParseQuery<ParseObject> currentUserIsFollowingQuery = ParseQuery.getQuery("Follower");

        currentUserIsFollowingQuery.whereEqualTo("followerId",currentUser.getObjectId());

        currentUserIsFollowingQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if(objects.size() > 0) {

                        ParseQuery<ParseObject> getTweetsFromFollowedUsersQuery = ParseQuery.getQuery("Tweet");

                        for (ParseObject object: objects){
                            currentUserFollowsArrayList.add(((String)object.get("userId")));
                        }

                        getTweetsFromFollowedUsersQuery.whereContainedIn("senderId",currentUserFollowsArrayList);

                        getTweetsFromFollowedUsersQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e == null) {
                                    if(objects.size() > 0) {
                                        for(ParseObject object : objects){
                                            Log.i(APPTAG,object.get("message").toString());
                                        }
                                    } else {
                                        Toast.makeText(TroughActivity.this, "No tweets found", Toast.LENGTH_LONG).show();
                                        Log.i(APPTAG, "objects.size() " + objects.size());
                                    }
                                }
                            }
                        });

                    } else {
                        Toast.makeText(
                                TroughActivity.this,
                                "No users followed!",
                                Toast.LENGTH_LONG)
                            .show();
                    }
                }
            }
        });

    }
}
