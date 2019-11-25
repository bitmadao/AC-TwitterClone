package com.udemy.ac_twitterclone;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TwitterUsersActivityListUser implements Serializable {
    private String name;
    private boolean following;

    public TwitterUsersActivityListUser(String name, boolean following) {
        this.name = name;
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name ;
    }
}
