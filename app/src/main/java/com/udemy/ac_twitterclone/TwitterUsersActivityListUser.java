package com.udemy.ac_twitterclone;

import java.io.Serializable;

public class TwitterUsersActivityListUser implements Serializable {
    private String name;
    private String id;
    private boolean following;

    public TwitterUsersActivityListUser(String name, String id, boolean following) {
        this.name = name;
        this.id = id;
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
