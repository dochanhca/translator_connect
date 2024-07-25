package com.example.translateconnector.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ton on 4/2/18.
 */

public class LocationDataModel {
    @SerializedName("locations")
    @Expose
    private List<LocationModel> locations;

    public List<LocationModel> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationModel> locations) {
        this.locations = locations;
    }
}
