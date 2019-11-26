package com.udemy.ac_twitterclone;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TwitterUsersActivityListUser implements Serializable {
    private String id;
    private String name;
    private boolean following;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TwitterUsersActivityListUser(String id, String name, boolean following) {
        this.id = id ;
        this.name = name;
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isCurrentUserFollowing() {
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
