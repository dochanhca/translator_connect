package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewContent {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("order_id")
    @Expose
    private Integer orderId;

    @SerializedName("reviewer_id")
    @Expose
    private Integer reviewerId;

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("total_ratting")
    @Expose
    private Float totalRatting;

    @SerializedName("skill_ratting")
    @Expose
    private Float skillRatting;

    @SerializedName("major_ratting")
    @Expose
    private Float majorRatting;

    @SerializedName("price_ratting")
    @Expose
    private Float priceRatting;

    @SerializedName("attitude_ratting")
    @Expose
    private Float attitudeRatting;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Integer reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getTotalRatting() {
        return totalRatting;
    }

    public void setTotalRatting(Float totalRatting) {
        this.totalRatting = totalRatting;
    }

    public Float getSkillRatting() {
        return skillRatting;
    }

    public void setSkillRatting(Float skillRatting) {
        this.skillRatting = skillRatting;
    }

    public Float getMajorRatting() {
        return majorRatting;
    }

    public void setMajorRatting(Float majorRatting) {
        this.majorRatting = majorRatting;
    }

    public Float getPriceRatting() {
        return priceRatting;
    }

    public void setPriceRatting(Float priceRatting) {
        this.priceRatting = priceRatting;
    }

    public Float getAttitudeRatting() {
        return attitudeRatting;
    }

    public void setAttitudeRatting(Float attitudeRatting) {
        this.attitudeRatting = attitudeRatting;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
