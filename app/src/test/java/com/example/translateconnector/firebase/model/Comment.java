package com.example.translateconnector.firebase.model;

import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.User;

public class Comment {
    private String id;
    private User author;
    private String message;
    private long timestamp;
    private long timestampReverse;
    private FileModel file;

    public Comment() {
    }

    public Comment(String id, User author, String message, long timestamp) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.timestamp = timestamp;
        this.timestampReverse = (-1) * this.timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampReverse() {
        return timestampReverse;
    }

    public void setTimestampReverse(long timestampReverse) {
        this.timestampReverse = timestampReverse;
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }
}
