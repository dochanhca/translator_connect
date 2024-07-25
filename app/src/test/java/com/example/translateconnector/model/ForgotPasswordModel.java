package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ton on 3/30/18.
 */

public class ForgotPasswordModel {
    @SerializedName("id")
    @Expose
    private int userId;


    @SerializedName("email")
    @Expose
    private String email;



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
