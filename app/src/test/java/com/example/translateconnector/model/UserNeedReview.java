package com.example.translateconnector.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserNeedReview implements Parcelable {
    @SerializedName("order_id")
    @Expose
    private int orderId;

    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("accepted_translator_id")
    @Expose
    private int acceptedTranslatorId;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAcceptedTranslatorId() {
        return acceptedTranslatorId;
    }

    public void setAcceptedTranslatorId(int acceptedTranslatorId) {
        this.acceptedTranslatorId = acceptedTranslatorId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.orderId);
        dest.writeInt(this.userId);
        dest.writeInt(this.acceptedTranslatorId);
    }

    public UserNeedReview() {
    }

    protected UserNeedReview(Parcel in) {
        this.orderId = in.readInt();
        this.userId = in.readInt();
        this.acceptedTranslatorId = in.readInt();
    }

    public static final Creator<UserNeedReview> CREATOR = new Creator<UserNeedReview>() {
        @Override
        public UserNeedReview createFromParcel(Parcel source) {
            return new UserNeedReview(source);
        }

        @Override
        public UserNeedReview[] newArray(int size) {
            return new UserNeedReview[size];
        }
    };
}
