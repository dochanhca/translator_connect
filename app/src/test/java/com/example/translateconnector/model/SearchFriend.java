package com.example.translateconnector.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.imoktranslator.model.firebase.User;

public class SearchFriend extends User implements Parcelable {

    private int mutualFriend;

    public SearchFriend(int mutualFriend) {
        this.mutualFriend = mutualFriend;
    }

    public SearchFriend(int id, String avatar, String status, String name, double latitude,
                        double longitude, String countryCode, String city, float star, int gender,
                        String dob, String phone, int mutualFriend) {
        super(id, avatar, status, name, latitude, longitude, countryCode, city, star, gender, dob, phone);
        this.mutualFriend = mutualFriend;
    }

    protected SearchFriend(Parcel in) {
        super(in);
        mutualFriend = in.readInt();
    }

    public static final Creator<SearchFriend> CREATOR = new Creator<SearchFriend>() {
        @Override
        public SearchFriend createFromParcel(Parcel in) {
            return new SearchFriend(in);
        }

        @Override
        public SearchFriend[] newArray(int size) {
            return new SearchFriend[size];
        }
    };

    public int getMutualFriend() {
        return mutualFriend;
    }

    public void setMutualFriend(int mutualFriend) {
        this.mutualFriend = mutualFriend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mutualFriend);
    }
}
