package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.OrderModel;

import java.util.List;

public class OrderListResponse {

    @SerializedName("data")
    @Expose
    private List<OrderModel> orderModels;

    public List<OrderModel> getOrderModels() {
        return orderModels;
    }
}
