package com.example.translateconnector.model;

public class NearBySettingItem {

    private int gender;
    private int fromAge;
    private int toAge;

    public NearBySettingItem(int gender, int fromAge, int toAge) {
        this.gender = gender;
        this.fromAge = fromAge;
        this.toAge = toAge;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getFromAge() {
        return fromAge;
    }

    public void setFromAge(int fromAge) {
        this.fromAge = fromAge;
    }

    public int getToAge() {
        return toAge;
    }

    public void setToAge(int toAge) {
        this.toAge = toAge;
    }
}
