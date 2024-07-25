package com.example.translateconnector.network.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PersonalInfoParam {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("gender")
    @Expose
    private int gender;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("identity_card_number")
    @Expose
    private String passport;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("address_type")
    @Expose
    private int addressType;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("long")
    @Expose
    private String lon;

    @SerializedName("foreign_languages")
    @Expose
    private int nativeLanguage;

    @SerializedName("translate_languages")
    @Expose
    private List<String> transLanguage;

    @SerializedName("university")
    @Expose
    private String university;

    @SerializedName("degree_classification")
    @Expose
    private Integer degreeClassification;//optional

    @SerializedName("year_of_graduation")
    @Expose
    private String graduationYear;//optional

    @SerializedName("year_experience")
    @Expose
    private Integer experience;//optional

    @SerializedName("other_info")
    @Expose
    private String otherInfo;//optional

    @SerializedName("certificate_name")
    @Expose
    private List<String> certificates;

    private boolean isNeedUpdateAvatar;

    private boolean isTranslator;

    public boolean isNeedUpdateAvatar() {
        return isNeedUpdateAvatar;
    }

    public void setNeedUpdateAvatar(boolean needUpdateAvatar) {
        isNeedUpdateAvatar = needUpdateAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob == null ? "" : dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassport() {
        return passport == null ? "" : dob;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getAddress() {
        return address == null ? "" : dob;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country == null ? "" : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getLat() {
        return lat == null ? "" : lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon == null ? "" : lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public int getNativeLanguage() {
        return nativeLanguage;
    }

    public void setNativeLanguage(int nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    public List<String> getTransLanguage() {
        return transLanguage == null ? new ArrayList<>() : transLanguage;
    }

    public void setTransLanguage(List<String> transLanguage) {
        this.transLanguage = transLanguage;
    }

    public String getUniversity() {
        return university == null ? "" : university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Integer getDegreeClassification() {
        return degreeClassification ==  null ? 0 : degreeClassification;
    }

    public void setDegreeClassification(Integer degreeClassification) {
        this.degreeClassification = degreeClassification;
    }

    public String getGraduationYear() {
        return graduationYear == null ? "" : graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public Integer getExperience() {
        return experience == null ? 0 : experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getOtherInfo() {
        return otherInfo == null ? "" : otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public List<String> getCertificates() {
        return certificates == null ? new ArrayList<>() : certificates;
    }

    public void setCertificates(List<String> certificates) {
        this.certificates = certificates;
    }

    public boolean isTranslator() {
        return isTranslator;
    }

    public void setTranslator(boolean translator) {
        isTranslator = translator;
    }
}
