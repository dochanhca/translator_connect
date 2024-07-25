package com.example.translateconnector.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.imoktranslator.model.firebase.FileModel;
import com.imoktranslator.model.firebase.User;
import com.imoktranslator.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post implements Parcelable {
    private String id;
    private User author;
    private String message;
    private List<FileModel> fileModels = new ArrayList<>();
    private Map<String, Boolean> likes = new HashMap<>();
    private Map<String, Boolean> comments = new HashMap<>();
    private Map<String, Integer> commentators = new HashMap<>();
    private Map<String, Boolean> viewer = new HashMap<>();
    // viewer khong thay doi, boi vi no duoc tao ra o thoi diem post bai
    private long timestamp;
    private long timestampReverse;
    private String mode;

    public Post() {
    }

    public Post(String id, User author, String message, long timestamp, String mode) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.timestamp = timestamp;
        this.timestampReverse = (-1) * this.timestamp;
        for (String friendKey : this.author.getFriends().keySet()) {
            this.viewer.put(friendKey, true);
        }
        this.mode = mode;
    }

    protected Post(Parcel in) {
        this.id = in.readString();
        this.author = in.readParcelable(User.class.getClassLoader());
        this.message = in.readString();
        this.fileModels = in.createTypedArrayList(FileModel.CREATOR);
        int likesSize = in.readInt();
        this.likes = new HashMap<String, Boolean>(likesSize);
        for (int i = 0; i < likesSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.likes.put(key, value);
        }
        int commentsSize = in.readInt();
        this.comments = new HashMap<String, Boolean>(commentsSize);
        for (int i = 0; i < commentsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.comments.put(key, value);
        }
        int commentatorsSize = in.readInt();
        this.commentators = new HashMap<String, Integer>(commentatorsSize);
        for (int i = 0; i < commentatorsSize; i++) {
            String key = in.readString();
            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
            this.commentators.put(key, value);
        }
        int viewerSize = in.readInt();
        this.viewer = new HashMap<String, Boolean>(viewerSize);
        for (int i = 0; i < viewerSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.viewer.put(key, value);
        }
        this.timestamp = in.readLong();
        this.timestampReverse = in.readLong();
        this.mode = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

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

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
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

    public Map<String, Integer> getCommentators() {
        return commentators;
    }

    public void setCommentators(Map<String, Integer> commentators) {
        this.commentators = commentators;
    }

    public Map<String, Boolean> getViewer() {
        return viewer;
    }

    public void setViewer(Map<String, Boolean> viewer) {
        this.viewer = viewer;
    }

    public Map<String, Boolean> getComments() {
        return comments;
    }

    public void setComments(Map<String, Boolean> comments) {
        this.comments = comments;
    }

    public List<FileModel> getFileModels() {
        return fileModels;
    }

    public void setFileModels(List<FileModel> fileModels) {
        this.fileModels = fileModels;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.author, flags);
        dest.writeString(this.message);
        dest.writeTypedList(this.fileModels);
        dest.writeInt(this.likes.size());
        for (Map.Entry<String, Boolean> entry : this.likes.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.comments.size());
        for (Map.Entry<String, Boolean> entry : this.comments.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.commentators.size());
        for (Map.Entry<String, Integer> entry : this.commentators.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.viewer.size());
        for (Map.Entry<String, Boolean> entry : this.viewer.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeLong(this.timestamp);
        dest.writeLong(this.timestampReverse);
        dest.writeString(this.mode);
    }

    public boolean isPrivate() {
        return Constants.PRIVATE_MODE.equals(mode);
    }

    public boolean isCreateByCurrentUser(String userKey) {
        return author.getKey().equals(userKey);
    }

    public boolean isVisibilityWithFriends() {
        return Constants.FRIENDS_MODE.equals(mode);
    }
}
