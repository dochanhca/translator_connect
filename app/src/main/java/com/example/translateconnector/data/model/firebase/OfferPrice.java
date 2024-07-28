package com.example.translateconnector.data.model.firebase;

public class OfferPrice {
    private String key;
    private int orderId;
    private String roomKey;
    private int transID;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public int getTransID() {
        return transID;
    }

    public void setTransID(int transID) {
        this.transID = transID;
    }

    public OfferPrice() {
    }

    public OfferPrice(String key, int orderId, String roomKey, int transID) {

        this.key = key;
        this.orderId = orderId;
        this.roomKey = roomKey;
        this.transID = transID;
    }

}
