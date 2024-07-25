package com.example.translateconnector.network.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ton on 3/31/18.
 */

public class UpdatePasswordParam {
    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("password")
    @Expose
    private String password;

    public UpdatePasswordParam(int userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
