package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.OrderNotificationModel;

import java.util.List;

public class ListOrderNotificationResponse {
    @SerializedName("data")
    @Expose
    private List<OrderNotificationModel> notificationList;

    public List<OrderNotificationModel> getNotificationList() {
        return notificationList;
    }
}
