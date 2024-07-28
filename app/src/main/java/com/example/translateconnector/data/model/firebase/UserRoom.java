package com.example.translateconnector.data.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class UserRoom implements Parcelable {

    private String key;
    private String userKey;
    //temp field for search User Chat Room
    private String userName;

    private String roomKey;
    private int type;
    private long createdTimeStamp;
    private long lastTimeActive;
    private boolean muteNotification;
    private boolean hasUnreadMessage;
    private boolean deleted;

    protected UserRoom(Parcel in) {
        key = in.readString();
        userKey = in.readString();
        userName = in.readString();
        roomKey = in.readString();
        type = in.readInt();
        createdTimeStamp = in.readLong();
        lastTimeActive = in.readLong();
        muteNotification = in.readByte() != 0;
        hasUnreadMessage = in.readByte() != 0;
        deleted = in.readByte() != 0;
    }

    public static final Creator<UserRoom> CREATOR = new Creator<UserRoom>() {
        @Override
        public UserRoom createFromParcel(Parcel in) {
            return new UserRoom(in);
        }

        @Override
        public UserRoom[] newArray(int size) {
            return new UserRoom[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLastTimeActive() {
        return lastTimeActive;
    }

    public void setLastTimeActive(long lastTimeActive) {
        this.lastTimeActive = lastTimeActive;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public boolean isMuteNotification() {
        return muteNotification;
    }

    public void setMuteNotification(boolean isMuteNotification) {
        this.muteNotification = isMuteNotification;
    }

    public boolean isHasUnreadMessage() {
        return hasUnreadMessage;
    }

    public void setHasUnreadMessage(boolean hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(long createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public UserRoom(){

    }

    public UserRoom(String key, String userKey, String roomKey, long lastTimeActive,
                    long createdTimeStamp) {
        this.key = key;
        this.userKey = userKey;
        this.roomKey = roomKey;
        this.lastTimeActive = lastTimeActive;
        this.createdTimeStamp = createdTimeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(userKey);
        dest.writeString(userName);
        dest.writeString(roomKey);
        dest.writeInt(type);
        dest.writeLong(createdTimeStamp);
        dest.writeLong(lastTimeActive);
        dest.writeByte((byte) (muteNotification ? 1 : 0));
        dest.writeByte((byte) (hasUnreadMessage ? 1 : 0));
        dest.writeByte((byte) (deleted ? 1 : 0));
    }
}
