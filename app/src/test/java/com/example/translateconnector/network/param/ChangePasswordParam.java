package com.example.translateconnector.network.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TuanNM on 4/2/2018.
 */

public class ChangePasswordParam {
    @SerializedName("old_password")
    @Expose
    private String oldPassword;
    @SerializedName("password")
    @Expose
    private String password;

    public ChangePasswordParam(String oldPassword, String password) {
        this.oldPassword = oldPassword;
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
