package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RatingDetail {
    @SerializedName("type")
    @Expose
    private Integer type;
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
    @SerializedName("reviews")
    @Expose
    private List<ReviewContent> reviews;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public List<ReviewContent> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewContent> reviews) {
        this.reviews = reviews;
    }

    public boolean isRattingForUser() {
        return type == 0;
    }
}
