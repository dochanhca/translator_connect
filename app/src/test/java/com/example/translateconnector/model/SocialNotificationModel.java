package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.utils.NotificationHelper;

public class SocialNotificationModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("receiver_id")
    @Expose
    private Integer receiverId;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("model_id")
    @Expose
    private String modelId;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("sender")
    @Expose
    private PersonalInfo sender;
    @SerializedName("receiver")
    @Expose
    private PersonalInfo receiver;
    @SerializedName("owner")
    @Expose
    private PersonalInfo owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PersonalInfo getSender() {
        return sender;
    }

    public void setSender(PersonalInfo sender) {
        this.sender = sender;
    }

    public PersonalInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(PersonalInfo receiver) {
        this.receiver = receiver;
    }

    public boolean typeLikePost() {
        return this.type == Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_LIKE);
    }

    public boolean typeAuthorComment() {
        return this.type == Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_AUTHOR_COMMENT);
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public boolean typeOtherUserComment() {
        return this.type == Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_OTHER_USER_COMMENT);
    }

    public boolean typeBestFriend() {
        return this.type == Integer.parseInt(NotificationHelper.NOTIFICATION_TYPE_BEST_FRIEND);
    }

    public PersonalInfo getOwner() {
        return owner;
    }

    public void setOwner(PersonalInfo owner) {
        this.owner = owner;
    }
}
