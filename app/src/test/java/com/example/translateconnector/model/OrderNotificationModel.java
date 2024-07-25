package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderNotificationModel {
    public static final int NEW_ORDER = 1;
    public static final int NEW_BIDS = 2;
    public static final int NEW_MESSAGE = 3;
    public static final int ALLOW_PRICE = 4;
    public static final int REFUSE_PRICE = 5;
    public static final int CANCELLED = 6;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("receiver_id")
    @Expose
    private Integer receiverId;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
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
    @SerializedName("order")
    @Expose
    private OrderModel order;
    @SerializedName("sender")
    @Expose
    private PersonalInfo sender;
    @SerializedName("receiver")
    @Expose
    private PersonalInfo receiver;

    public Integer getId() {
        return id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getType() {
        return type;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public OrderModel getOrder() {
        return order;
    }

    public PersonalInfo getSender() {
        return sender;
    }

    public PersonalInfo getReceiver() {
        return receiver;
    }

    public boolean isNotificationForWorker() {
        return type == NEW_ORDER || type == ALLOW_PRICE || type == REFUSE_PRICE;
    }

    public boolean isNotificationForOwner() {
        return  (type == NEW_BIDS || type == NEW_MESSAGE);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public void setSender(PersonalInfo sender) {
        this.sender = sender;
    }

    public void setReceiver(PersonalInfo receiver) {
        this.receiver = receiver;
    }

    public boolean isBlocked(String senderId) {
        if (order.getSenderBlocked() == null) {
            return false;
        } else {
            return order.getSenderBlocked().contains(senderId);
        }
    }
}
