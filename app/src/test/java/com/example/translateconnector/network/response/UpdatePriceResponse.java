package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.network.param.UpdatePriceParam;

public class UpdatePriceResponse {

    @SerializedName("data")
    @Expose
    private UpdatePriceParam updatePriceParam;

    public UpdatePriceParam getUpdatePriceParam() {
        return updatePriceParam;
    }

}
