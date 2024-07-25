package com.example.translateconnector.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.imoktranslator.model.IntroPageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tontn on 3/25/18.
 */

public class IntroduceResponse implements Parcelable {

    @SerializedName("data")
    @Expose
    private List<IntroPageItem> data;

    public List<IntroPageItem> getData() {
        return data;
    }

    public void setData(List<IntroPageItem> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.data);
    }

    public IntroduceResponse() {
    }

    protected IntroduceResponse(Parcel in) {
        this.data = new ArrayList<IntroPageItem>();
        in.readList(this.data, IntroPageItem.class.getClassLoader());
    }

    public static final Creator<IntroduceResponse> CREATOR = new Creator<IntroduceResponse>() {
        @Override
        public IntroduceResponse createFromParcel(Parcel source) {
            return new IntroduceResponse(source);
        }

        @Override
        public IntroduceResponse[] newArray(int size) {
            return new IntroduceResponse[size];
        }
    };
}
