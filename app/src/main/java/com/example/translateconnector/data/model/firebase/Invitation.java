package com.example.translateconnector.data.model.firebase;

public class Invitation {
    private String senderKey;
    private int senderId;
    private int mutualFriends;

    public Invitation() {
    }

    public Invitation(String senderKey, int senderId, int mutualFriends) {
        this.senderKey = senderKey;
        this.senderId = senderId;
        this.mutualFriends = mutualFriends;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(int mutualFriends) {
        this.mutualFriends = mutualFriends;
    }
}
