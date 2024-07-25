package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ton on 4/2/18.
 */

public class LocationModel {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("provinces")
    @Expose
    private List<ProvinceModel> provinces = null;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<ProvinceModel> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<ProvinceModel> provinces) {
        this.provinces = provinces;
    }

}
