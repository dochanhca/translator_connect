package com.example.translateconnector.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class PostStats implements Parcelable {
    private String postId;
    private Map<String, Boolean> viewer = new HashMap<>(); // viewer khong thay doi, boi vi no duoc tao ra o thoi diem post bai

    public PostStats() {
    }

    public PostStats(String postId, Map<String, Boolean> viewer) {
        this.postId = postId;
        this.viewer = viewer;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Map<String, Boolean> getViewer() {
        return viewer;
    }

    public void setViewer(Map<String, Boolean> viewer) {
        this.viewer = viewer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postId);
        dest.writeInt(this.viewer.size());
        for (Map.Entry<String, Boolean> entry : this.viewer.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    protected PostStats(Parcel in) {
        this.postId = in.readString();
        int viewerSize = in.readInt();
        this.viewer = new HashMap<>(viewerSize);
        for (int i = 0; i < viewerSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.viewer.put(key, value);
        }
    }

    public static final Creator<PostStats> CREATOR = new Creator<PostStats>() {
        @Override
        public PostStats createFromParcel(Parcel source) {
            return new PostStats(source);
        }

        @Override
        public PostStats[] newArray(int size) {
            return new PostStats[size];
        }
    };
}
