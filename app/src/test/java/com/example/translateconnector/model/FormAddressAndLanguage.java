package com.example.translateconnector.model;

import java.util.List;

public class FormAddressAndLanguage {
    private String mapAddress;
    private String lat;
    private String lon;
    private String country;
    private String city;
    private String address;
    private int nativeLanguage;
    private List<String> transLanguages;

    public FormAddressAndLanguage(String mapAddress, String lat, String lon, String country, String city, String address, int nativeLanguage, List<String> transLanguages) {
        this.mapAddress = mapAddress;
        this.lat = lat;
        this.lon = lon;
        this.country = country;
        this.city = city;
        this.address = address;
        this.nativeLanguage = nativeLanguage;
        this.transLanguages = transLanguages;
    }

    public String getMapAddress() {
        return mapAddress;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public int getNativeLanguage() {
        return nativeLanguage;
    }

    public List<String> getTransLanguages() {
        return transLanguages;
    }


}
