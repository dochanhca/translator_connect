package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ton on 4/2/18.
 */

public class ProvinceModel {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cities")
    @Expose
    private List<CityModel> cities = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityModel> getCities() {
        return cities;
    }

    public void setCities(List<CityModel> cities) {
        this.cities = cities;
    }
}
