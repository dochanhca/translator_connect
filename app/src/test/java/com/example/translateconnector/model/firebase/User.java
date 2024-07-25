package com.example.translateconnector.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.imoktranslator.firebase.model.CommentStats;
import com.imoktranslator.firebase.model.PostStats;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    private String key;
    private int id;
    private String avatar;
    private String status;
    private String name;
    private double latitude;
    private double longitude;
    private String countryCode;
    private String city;
    private float star;
    private int gender;
    private String dob;
    private String phone;
    private String registerCountry;
    private String registerCity;
    private boolean translator;
    private Map<String, Boolean> friends = new HashMap<>(); // friend co the thay doi theo thoi gian
    private Map<String, PostStats> postStats = new HashMap<>();
    private Map<String, CommentStats> commentStats = new HashMap<>();
    private String prioritySetting;

    public User() {
    }

    public User(int id, String avatar, String status, String name, double latitude,
                double longitude, String countryCode, String city, float star, int gender, String dob, String phone) {
        this.id = id;
        this.avatar = avatar;
        this.status = status;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
        this.city = city;
        this.star = star;
        this.gender = gender;
        this.dob = dob;
        this.phone = phone;

    }

    protected User(Parcel in) {
        key = in.readString();
        id = in.readInt();
        avatar = in.readString();
        status = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        countryCode = in.readString();
        city = in.readString();
        star = in.readFloat();
        gender = in.readInt();
        dob = in.readString();
        phone = in.readString();
        registerCountry = in.readString();
        registerCity = in.readString();
        translator = in.readByte() != 0;
        int friendsSize = in.readInt();
        prioritySetting = in.readString();
        this.friends = new HashMap<>(friendsSize);
        for (int i = 0; i < friendsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.friends.put(key, value);
        }
        int postStatsSize = in.readInt();
        this.postStats = new HashMap<>(postStatsSize);
        for (int i = 0; i < postStatsSize; i++) {
            String key = in.readString();
            PostStats value = in.readParcelable(PostStats.class.getClassLoader());
            this.postStats.put(key, value);
        }
        int commentStatsSize = in.readInt();
        this.commentStats = new HashMap<>(commentStatsSize);
        for (int i = 0; i < commentStatsSize; i++) {
            String key = in.readString();
            CommentStats value = in.readParcelable(CommentStats.class.getClassLoader());
            this.commentStats.put(key, value);
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRegisterCountry() {
        return registerCountry;
    }

    public void setRegisterCountry(String registerCountry) {
        this.registerCountry = registerCountry;
    }

    public String getRegisterCity() {
        return registerCity;
    }

    public void setRegisterCity(String registerCity) {
        this.registerCity = registerCity;
    }

    public boolean isTranslator() {
        return translator;
    }

    public void setTranslator(boolean isTranslator) {
        this.translator = isTranslator;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }

    public Map<String, PostStats> getPostStats() {
        return postStats;
    }

    public void setPostStats(Map<String, PostStats> postStats) {
        this.postStats = postStats;
    }

    public Map<String, CommentStats> getCommentStats() {
        return commentStats;
    }

    public void setCommentStats(Map<String, CommentStats> commentStats) {
        this.commentStats = commentStats;
    }

    public String getPrioritySetting() {
        return prioritySetting;
    }

    public void setPrioritySetting(String prioritySetting) {
        this.prioritySetting = prioritySetting;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeInt(id);
        dest.writeString(avatar);
        dest.writeString(status);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(countryCode);
        dest.writeString(city);
        dest.writeFloat(star);
        dest.writeInt(gender);
        dest.writeString(dob);
        dest.writeString(phone);
        dest.writeString(registerCountry);
        dest.writeString(registerCity);
        dest.writeByte((byte) (translator ? 1 : 0));
        dest.writeInt(this.friends.size());
        dest.writeString(prioritySetting);
        for (Map.Entry<String, Boolean> entry : this.friends.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.postStats.size());
        for (Map.Entry<String, PostStats> entry : this.postStats.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeInt(this.commentStats.size());
        for (Map.Entry<String, CommentStats> entry : this.commentStats.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
    }
}
