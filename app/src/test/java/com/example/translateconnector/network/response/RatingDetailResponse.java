package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.RatingDetail;

public class RatingDetailResponse {
    @SerializedName("data")
    @Expose
    private RatingDetail ratingDetail;

    public RatingDetail getRatingDetail() {
        return ratingDetail;
    }
}
