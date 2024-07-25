package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.PriceModel;

import java.util.List;

public class ListPriceResponse {

    @SerializedName("data")
    @Expose
    private List<PriceModel> priceModels;

    public List<PriceModel> getPriceModels() {
        return priceModels;
    }
}
