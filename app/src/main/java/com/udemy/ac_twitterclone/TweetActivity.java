package com.udemy.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import static com.udemy.ac_twitterclone.ACTwitterCloneTools.APPTAG;

public class TweetActivity extends AppCompatActivity implements View.OnClickListener{

    private ParseUser currentUser;
    private EditText edtTweet;
    private Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        if(ParseUser.getCurrentUser() == null) {
            transitionToLogin();
        } else {
            currentUser = ParseUser.getCurrentUser();
        }
        setTitle(String.format("Sending tweet as %s",currentUser.getUsername()));

        edtTweet = findViewById(R.id.edtTweetActivityTweet);

        btnTweet = findViewById(R.id.btnTweetActivityTweet);
        btnTweet.setOnClickListener(TweetActivity.this);



    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnTweetActivityTweet){
            btnTweetTapped();
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
                    finish();
                } else {
                    Log.i(APPTAG, e.getMessage());
                    Toast.makeText(TweetActivity.this, getString(R.string.generic_toast_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void transitionToLogin(){
        startActivity(new Intent(TweetActivity.this,LoginActivity.class));
    }
}
