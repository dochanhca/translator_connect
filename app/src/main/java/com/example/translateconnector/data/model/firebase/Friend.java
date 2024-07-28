package com.example.translateconnector.data.model.firebase;

public class Friend {
    private String userKey;
    private String roomKey;
    private boolean bestFriend;

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public boolean isBestFriend() {
        return bestFriend;
    }

    public void setBestFriend(boolean bestFriend) {
        this.bestFriend = bestFriend;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Friend(){

    }

    public Friend(String userKey, String roomKey) {
        this.userKey = userKey;
        this.roomKey = roomKey;
    }
}
