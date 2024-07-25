package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.OrderModel;

public class OrderInfoResponse {

    @SerializedName("data")
    @Expose
    private OrderModel orderModel;

    public OrderModel getOrderModel() {
        return orderModel;
    }
}
