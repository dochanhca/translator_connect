package com.example.translateconnector.network.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VotePartnerParam {
    @SerializedName("order_id")
    @Expose
    private int orderId;

    @SerializedName("reviewer_id")
    @Expose
    private int reviewerId;

    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("total_ratting")
    @Expose
    private float totalRatting;

    @SerializedName("skill_ratting")
    @Expose
    private float skillRatting;

    @SerializedName("major_ratting")
    @Expose
    private float majorRatting;

    @SerializedName("price_ratting")
    @Expose
    private float priceRatting;

    @SerializedName("attitude_ratting")
    @Expose
    private float attitudeRatting;

    @SerializedName("content")
    @Expose
    private String content;

    public VotePartnerParam(int orderId, int reviewerId, int userId, float totalRatting, float skillRatting, float majorRatting, float priceRatting, float attitudeRatting, String content) {
        this.orderId = orderId;
        this.reviewerId = reviewerId;
        this.userId = userId;
        this.totalRatting = totalRatting;
        this.skillRatting = skillRatting;
        this.majorRatting = majorRatting;
        this.priceRatting = priceRatting;
        this.attitudeRatting = attitudeRatting;
        this.content = content;
    }
}
