package com.example.translateconnector.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ducpv on 3/25/18.
 */

public class PhoneCodeItem implements Parcelable {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("code")
    @Expose
    private String code;

    protected PhoneCodeItem(Parcel in) {
        country = in.readString();
        code = in.readString();
    }

    public static final Creator<PhoneCodeItem> CREATOR = new Creator<PhoneCodeItem>() {
        @Override
        public PhoneCodeItem createFromParcel(Parcel in) {
            return new PhoneCodeItem(in);
        }

        @Override
        public PhoneCodeItem[] newArray(int size) {
            return new PhoneCodeItem[size];
        }
    };

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(code);
    }
}
