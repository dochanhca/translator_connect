package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationSetting {

    @SerializedName("notify_post")
    @Expose
    private int notifyPost;

    @SerializedName("notify_message")
    @Expose
    private int notifyMessage;

    @SerializedName("notify_update_price")
    @Expose
    private int notifyUpdatePrice;

    @SerializedName("notify_find_translator")
    @Expose
    private int notifyFindTranslator;

    @SerializedName("notify_register_translator")
    @Expose
    private int notifyRegisterTranslator;

    @SerializedName("notify_add_friend")
    @Expose
    private int notifyAddFriend;

    @SerializedName("notify_best_friend")
    @Expose
    private int notifyBestFriend;

    public int getNotifyPost() {
        return notifyPost;
    }

    public void setNotifyPost(int notifyPost) {
        this.notifyPost = notifyPost;
    }

    public int getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(int notifyMessage) {
        this.notifyMessage = notifyMessage;
    }

    public int getNotifyUpdatePrice() {
        return notifyUpdatePrice;
    }

    public void setNotifyUpdatePrice(int notifyUpdatePrice) {
        this.notifyUpdatePrice = notifyUpdatePrice;
    }

    public int getNotifyFindTranslator() {
        return notifyFindTranslator;
    }

    public void setNotifyFindTranslator(int notifyFindTranslator) {
        this.notifyFindTranslator = notifyFindTranslator;
    }

    public int getNotifyRegisterTranslator() {
        return notifyRegisterTranslator;
    }

    public void setNotifyRegisterTranslator(int notifyRegisterTranslator) {
        this.notifyRegisterTranslator = notifyRegisterTranslator;
    }

    public int getNotifyAddFriend() {
        return notifyAddFriend;
    }

    public void setNotifyAddFriend(int notifyAddFriend) {
        this.notifyAddFriend = notifyAddFriend;
    }

    public int getNotifyBestFriend() {
        return notifyBestFriend;
    }

    public void setNotifyBestFriend(int notifyBestFriend) {
        this.notifyBestFriend = notifyBestFriend;
    }
}
