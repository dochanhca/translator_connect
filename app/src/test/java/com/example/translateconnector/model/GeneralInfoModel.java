package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeneralInfoModel {
    @SerializedName("total_order_notification_unread")
    @Expose
    private int totalOrderNotificationUnder;

    @SerializedName("total_notification_unread")
    @Expose
    private int totalNotificationUnread;

    @SerializedName("users_needed_review")
    @Expose
    private List<UserNeedReview> listUserNeedReview;

    @SerializedName("order_needed_extend")
    @Expose
    private List<OrderModel>  listOrderNeededExtend;

    @SerializedName("order_extended")
    @Expose
    private List<OrderModel>  listOrderExtended;

    public List<OrderModel> getListOrderExtended() {
        return listOrderExtended;
    }

    public void setListOrderExtended(List<OrderModel> listOrderExtended) {
        this.listOrderExtended = listOrderExtended;
    }

    public List<OrderModel> getListOrderNeededExtend() {
        return listOrderNeededExtend;
    }

    public void setListOrderNeededExtend(List<OrderModel> listOrderNeededExtend) {
        this.listOrderNeededExtend = listOrderNeededExtend;
    }

    public List<UserNeedReview> getListUserNeedReview() {
        return listUserNeedReview;
    }

    public void setListUserNeedReview(List<UserNeedReview> listUserNeedReview) {
        this.listUserNeedReview = listUserNeedReview;
    }

    public int getTotalOrderNotificationUnder() {
        return totalOrderNotificationUnder;
    }

    public void setTotalOrderNotificationUnder(int totalUnreadNotification) {
        this.totalOrderNotificationUnder = totalUnreadNotification;
    }

    public int getTotalNotificationUnread() {
        return totalNotificationUnread;
    }

    public void setTotalNotificationUnread(int totalNotificationUnread) {
        this.totalNotificationUnread = totalNotificationUnread;
    }
}
