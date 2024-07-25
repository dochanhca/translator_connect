package com.example.translateconnector.model;

import com.imoktranslator.model.firebase.Friend;
import com.imoktranslator.model.firebase.User;

public class UserFriend {

    private User user;
    private Friend friend;

    public UserFriend(User user, Friend friend) {
        this.user = user;
        this.friend = friend;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
