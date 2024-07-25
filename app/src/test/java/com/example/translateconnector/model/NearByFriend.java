package com.example.translateconnector.model;

import android.support.annotation.NonNull;


public class NearByFriend extends SearchFriend implements Comparable<NearByFriend> {

    private float distance;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }


    public NearByFriend(int id, String avatar, String status, String name, double latitude,
                        double longitude, String countryCode, String city, float star, int gender,
                        String dob, String phone, int mutualFriend, float distance) {
        super(id, avatar, status, name, latitude, longitude, countryCode, city, star, gender, dob, phone, mutualFriend);
        this.distance = distance;
    }

    @Override
    public int compareTo(@NonNull NearByFriend o) {
        return Double.compare(this.distance, o.getDistance());
    }
}
