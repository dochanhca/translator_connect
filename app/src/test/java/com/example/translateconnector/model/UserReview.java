package com.example.translateconnector.model;

public class UserReview {

    private int userId;
    private String avatar;
    private String review;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public UserReview(int userId, String avatar, String review) {
        this.userId = userId;
        this.avatar = avatar;
        this.review = review;
    }
}
