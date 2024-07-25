package com.example.translateconnector.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private String key;
    private String chatRoomName;
    private String lastMessage;
    private String owner;
    private String visitor;
    private int type; //0: personal 1: group
    private long lastTimeActive;
    private String lastSender;
    private boolean readOnly;
    private String avatar;


    protected Room(Parcel in) {
        key = in.readString();
        chatRoomName = in.readString();
        lastMessage = in.readString();
        owner = in.readString();
        visitor = in.readString();
        type = in.readInt();
        lastTimeActive = in.readLong();
        lastSender = in.readString();
        readOnly = in.readByte() != 0;
        avatar = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVisitor() {
        return visitor;
    }

    public void setVisitor(String visitor) {
        this.visitor = visitor;
    }

    public long getLastTimeActive() {
        return lastTimeActive;
    }

    public void setLastTimeActive(long lastTimeActive) {
        this.lastTimeActive = lastTimeActive;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Room(){

    }

    public Room(String chatRoomName, int type, String lastSender, String lastMessage, String owner, String visitor, long lastTimeActive) {
        this.chatRoomName = chatRoomName;
        this.type = type;
        this.lastSender = lastSender;
        this.lastMessage = lastMessage;
        this.owner = owner;
        this.visitor = visitor;
        this.lastTimeActive = lastTimeActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(chatRoomName);
        dest.writeString(lastMessage);
        dest.writeString(owner);
        dest.writeString(visitor);
        dest.writeInt(type);
        dest.writeLong(lastTimeActive);
        dest.writeString(lastSender);
        dest.writeByte((byte) (readOnly ? 1 : 0));
        dest.writeString(avatar);
    }
}
