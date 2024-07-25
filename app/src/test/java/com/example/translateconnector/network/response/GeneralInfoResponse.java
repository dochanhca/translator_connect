package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.GeneralInfoModel;

public class GeneralInfoResponse {
    @SerializedName("data")
    @Expose
    private GeneralInfoModel generalInfo;

    public GeneralInfoModel getGeneralInfo() {
        return generalInfo;
    }
}
