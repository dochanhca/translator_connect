package com.example.translateconnector.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class CommentStats implements Parcelable {
    private String postId;
    private Map<String, Boolean> commentIds = new HashMap<>();

    public CommentStats() {
    }

    public CommentStats(String postId, Map<String, Boolean> commentIds) {
        this.postId = postId;
        this.commentIds = commentIds;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Map<String, Boolean> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(Map<String, Boolean> commentIds) {
        this.commentIds = commentIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postId);
        dest.writeInt(this.commentIds.size());
        for (Map.Entry<String, Boolean> entry : this.commentIds.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    protected CommentStats(Parcel in) {
        this.postId = in.readString();
        int commentIdsSize = in.readInt();
        this.commentIds = new HashMap<>(commentIdsSize);
        for (int i = 0; i < commentIdsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.commentIds.put(key, value);
        }
    }

    public static final Creator<CommentStats> CREATOR = new Creator<CommentStats>() {
        @Override
        public CommentStats createFromParcel(Parcel source) {
            return new CommentStats(source);
        }

        @Override
        public CommentStats[] newArray(int size) {
            return new CommentStats[size];
        }
    };
}
