package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.NotificationSetting;

public class NotificationSettingResponse {
    @SerializedName("data")
    @Expose
    private NotificationSetting notificationSetting;

    public NotificationSetting getNotificationSetting() {
        return notificationSetting;
    }
}
