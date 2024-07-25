package com.example.translateconnector.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.PersonalInfo;

/**
 * Created by ton on 3/31/18.
 */

public class PersonalInfoResponse {
    @SerializedName("data")
    @Expose
    private PersonalInfo personalInfo;

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }
}
