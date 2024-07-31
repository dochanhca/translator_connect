package com.example.translateconnector.data.model.firebase;

public class VoiceRecord {
    private String latitude;
    private String longitude;

    public VoiceRecord() {
    }

    public VoiceRecord(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
