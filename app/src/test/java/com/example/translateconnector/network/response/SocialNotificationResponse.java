package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.SocialNotificationModel;

import java.util.List;

public class SocialNotificationResponse {
    @SerializedName("data")
    @Expose
    private List<SocialNotificationModel> notificationList;

    public List<SocialNotificationModel> getNotificationList() {
        return notificationList;
    }
}
