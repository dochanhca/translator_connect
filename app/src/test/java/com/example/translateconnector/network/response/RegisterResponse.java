package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.RegisterItem;

/**
 * Created by ducpv on 3/29/18.
 */

public class RegisterResponse {

    @SerializedName("data")
    @Expose
    private RegisterItem data;

    public RegisterItem getData() {
        return data;
    }
}
