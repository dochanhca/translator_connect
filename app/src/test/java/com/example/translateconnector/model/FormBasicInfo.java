package com.example.translateconnector.model;

/**
 * Created by tvoer on 4/6/18.
 */

public class FormBasicInfo {
    private String avatar;
    private String name;
    private String phone;
    private String passport;
    private int gender;
    private String dob;
    private String email;
    private boolean isUploadNewAvatar;

    public FormBasicInfo(String avatar, String name, String phone, String passport, int gender, String dob, String email, boolean isUploadNewAvatar) {
        this.avatar = avatar;
        this.name = name;
        this.phone = phone;
        this.passport = passport;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.isUploadNewAvatar = isUploadNewAvatar;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getPassport() {
        return passport;
    }

    public int getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean isUploadNewAvatar() {
        return isUploadNewAvatar;
    }
}
