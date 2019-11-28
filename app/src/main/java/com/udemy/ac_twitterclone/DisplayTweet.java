package com.udemy.ac_twitterclone;

import com.parse.ParseObject;

public class DisplayTweet {

    private String header;

    private String message;

    public DisplayTweet(ParseObject tweetObject) {
        this.header = String.format(
                "%s %s",
                tweetObject.get("senderUsername"),
                tweetObject.getCreatedAt().toString()
        );

        this.message = (String) tweetObject.get("message");
    }
    public String getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }
}
