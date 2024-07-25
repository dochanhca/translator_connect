package com.example.translateconnector.model;

public class FriendInvitationModel {

    private int senderId;
    private String senderName;
    private String senderKey;
    private String senderAvatar;
    private int commonFriends;

    public FriendInvitationModel(int senderId, String senderName, String senderKey, String senderAvatar, int commonFriends) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderKey = senderKey;
        this.senderAvatar = senderAvatar;
        this.commonFriends = commonFriends;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public int getCommonFriends() {
        return commonFriends;
    }

    public int getSenderId() {
        return senderId;
    }
}
