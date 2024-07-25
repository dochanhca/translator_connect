package com.example.translateconnector.network.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tvoer on 3/30/18.
 */

public class VerifyOTPParam {
    @SerializedName("otp")
    @Expose
    private String OTP;

    @SerializedName("user_id")
    @Expose
    private int userId;

    public VerifyOTPParam(String OTP, int userId) {
        this.OTP = OTP;
        this.userId = userId;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
