package com.example.translateconnector.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel implements Parcelable {

    protected OrderModel(Parcel in) {
        orderId = in.readInt();
        userId = in.readInt();
        name = in.readString();
        translationLang = in.readInt();
        translationType = in.readInt();
        latitude = in.readString();
        longitude = in.readString();
        addressType = in.readInt();
        country = in.readString();
        address = in.readString();
        city = in.readString();
        fromDate = in.readString();
        toDate = in.readString();
        experience = in.readInt();
        gender = in.readInt();
        expirationDate = in.readString();
        description = in.readString();
        quality = in.readDouble();
        orderStatus = in.readInt();
        price = in.readDouble();
        currency = in.readInt();
        dateCreated = in.readString();
        statusPriceForTrans = in.readInt();
        acceptedTransId = in.readInt();
        senderBlocked = in.createStringArrayList();
        reasonToCancel = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderId);
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeInt(translationLang);
        dest.writeInt(translationType);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeInt(addressType);
        dest.writeString(country);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(fromDate);
        dest.writeString(toDate);
        dest.writeInt(experience);
        dest.writeInt(gender);
        dest.writeString(expirationDate);
        dest.writeString(description);
        dest.writeDouble(quality);
        dest.writeInt(orderStatus);
        dest.writeDouble(price);
        dest.writeInt(currency);
        dest.writeString(dateCreated);
        dest.writeInt(statusPriceForTrans);
        dest.writeInt(acceptedTransId);
        dest.writeStringList(this.senderBlocked);
        dest.writeString(reasonToCancel);
    }

    public static class ORDER_TYPE {
        public static final int NEW_ORDER = 1;
        public static final int SEARCHING_ORDER = 2;
        public static final int UPDATED_PRICE_ORDER = 3;
        public static final int REJECTED_ORDER = 4;
        public static final int TRANS_TRADING_ORDER = 5;
        public static final int OWNER_TRADING_ORDER = 6;
        public static final int CANCELED_ORDER = 7;
        public static final int FINISHED_ORDER = 8;
        public static final int EXPIRED_ORDER = 9;
    }

    public static class ORDER_STATUS {
        public static final int SEARCHING_ORDER = 1;
        public static final int TRADING_ORDER = 2;
        public static final int CANCELED_ORDER = 3;
        public static final int FINISHED_ORDER = 4;
    }

    public static class ORDER_PRICE_STATUS {
        public static final int NOTHING = 0;
        public static final int UPDATED_PRICE = 1;
        public static final int CANCELED_PRICE = 2;
    }

    public OrderModel() {
    }

    @SerializedName("id")
    @Expose(serialize = false)
    private int orderId;

    @SerializedName("user_id")
    @Expose(serialize = false)
    private int userId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("translate_language")
    @Expose
    private int translationLang;

    @SerializedName("translator_type")
    @Expose
    private int translationType;

    @SerializedName("lat")
    @Expose
    private String latitude;

    @SerializedName("long")
    @Expose
    private String longitude;

    @SerializedName("address_type")
    @Expose
    private int addressType;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("started_at")
    @Expose
    private String fromDate;

    @SerializedName("ended_at")
    @Expose
    private String toDate;

    @SerializedName("translator_year_experience")
    @Expose
    private int experience;

    @SerializedName("translator_gender")
    @Expose
    private int gender;

    @SerializedName("expires_at")
    @Expose
    private String expirationDate;

    @SerializedName("other_info")
    @Expose
    private String description;

    @SerializedName("translator_score")
    @Expose
    private double quality;

    @SerializedName("status")
    @Expose
    private int orderStatus;

    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("currency")
    @Expose
    private int currency;

    @SerializedName("created_at")
    @Expose
    private String dateCreated;

    @SerializedName("status_price_for_trans")
    @Expose(serialize = false)
    private int statusPriceForTrans;

    @SerializedName("accepted_translator_id")
    @Expose(serialize = false)
    private int acceptedTransId;
    
    @SerializedName("sender_blocked")
    @Expose
    List<String> senderBlocked;

    @SerializedName("reason_to_cancel")
    @Expose
    private String reasonToCancel;

    public String getReasonToCancel() {
        return reasonToCancel;
    }

    public void setReasonToCancel(String reasonToCancel) {
        this.reasonToCancel = reasonToCancel;
    }

    public List<String> getSenderBlocked() {
        return senderBlocked;
    }

    public void setSenderBlocked(List<String> senderBlocked) {
        this.senderBlocked = senderBlocked;
    }

    public int getAcceptedTransId() {
        return acceptedTransId;
    }

    public void setAcceptedTransId(int acceptedTransId) {
        this.acceptedTransId = acceptedTransId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTranslationLang() {
        return translationLang;
    }

    public void setTranslationLang(int translationLang) {
        this.translationLang = translationLang;
    }

    public int getTranslationType() {
        return translationType;
    }

    public void setTranslationType(int translationType) {
        this.translationType = translationType;
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

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public int getStatusPriceForTrans() {
        return statusPriceForTrans;
    }

    public void setStatusPriceForTrans(int statusPriceForTrans) {
        this.statusPriceForTrans = statusPriceForTrans;
    }
}
