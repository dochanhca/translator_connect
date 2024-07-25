package com.example.translateconnector.model.firebase;

import com.imoktranslator.utils.FireBaseDataUtils;

public class Message {

    //    private String id;
    private String key;
    private String userKey;
    private String message;
    private String timeStamp;
    private FileModel file;
    private VoiceRecord record;
    private int userId;
    private int type;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }

    public VoiceRecord getRecord() {
        return record;
    }

    public void setRecord(VoiceRecord record) {
        this.record = record;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Message(String userKey, String message, String timeStamp, FileModel file, VoiceRecord record, int userId) {
        this.userKey = userKey;
        this.message = message;
        this.timeStamp = timeStamp;
        this.file = file;
        this.record = record;
        this.userId = userId;
        this.type = FireBaseDataUtils.CHAT_TYPE_NORMAL;
    }

    public Message() {
    }

}
