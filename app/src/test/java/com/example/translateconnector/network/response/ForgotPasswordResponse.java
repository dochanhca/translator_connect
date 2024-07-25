package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.ForgotPasswordModel;

/**
 * Created by ton on 3/30/18.
 */

public class ForgotPasswordResponse {
    @SerializedName("data")
    @Expose
    private ForgotPasswordModel forgotPasswordModel;

    public ForgotPasswordModel getForgotPasswordModel() {
        return forgotPasswordModel;
    }

    public void setForgotPasswordModel(ForgotPasswordModel forgotPasswordModel) {
        this.forgotPasswordModel = forgotPasswordModel;
    }


    public int getUserId() {
        return forgotPasswordModel.getUserId();
    }

    public String getUserEmail() {
        return forgotPasswordModel.getEmail();
    }
}
