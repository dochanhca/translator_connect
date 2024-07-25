package com.example.translateconnector.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.darsh.multipleimageselect.models.Image;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ton on 3/31/18.
 */

public class PersonalInfo implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("identity_card_number")
    @Expose
    private String identityCardNumber;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("expires_at")
    @Expose
    private String expiresAt;
    @SerializedName("address_type")
    @Expose
    private Integer addressType;
    @SerializedName("lat")
    @Expose
    private String latitude;
    @SerializedName("long")
    @Expose
    private String longitude;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("foreign_languages")
    @Expose
    private Integer foreignLanguages;
    @SerializedName("translate_languages")
    @Expose
    private List<String> translateLanguages;
    @SerializedName("university")
    @Expose
    private String university;
    @SerializedName("degree_classification")
    @Expose
    private Integer degreeClassification;
    @SerializedName("year_of_graduation")
    @Expose
    private String yearOfGraduation;
    @SerializedName("year_experience")
    @Expose
    private Integer yearExperience;
    @SerializedName("certificate_name")
    @Expose
    private List<String> certificateName;
    @SerializedName("certificates")
    @Expose
    private List<CertificateModel> certificates;
    @SerializedName("other_info")
    @Expose
    private String otherInfo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("requested_at")
    @Expose
    private String requestedAt;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("score")
    @Expose
    private double score;
    @SerializedName("count_order_created")
    @Expose
    private int orderCreated;
    @SerializedName("count_order_cancel")
    @Expose
    private int orderCanceled;
    @SerializedName("count_order_price")
    @Expose
    private int orderUpdatedPrice;
    @SerializedName("count_order_price_success")
    @Expose
    private int orderSuccess;
    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("setting_language")
    @Expose
    private String settingLanguage;

    private boolean isNeedUpdateAvatar;
    private List<Image> attachPaths;

    protected PersonalInfo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            gender = null;
        } else {
            gender = in.readInt();
        }
        dob = in.readString();
        email = in.readString();
        phone = in.readString();
        if (in.readByte() == 0) {
            type = null;
        } else {
            type = in.readInt();
        }
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
        identityCardNumber = in.readString();
        avatar = in.readString();
        otp = in.readString();
        expiresAt = in.readString();
        if (in.readByte() == 0) {
            addressType = null;
        } else {
            addressType = in.readInt();
        }
        latitude = in.readString();
        longitude = in.readString();
        address = in.readString();
        city = in.readString();
        country = in.readString();
        if (in.readByte() == 0) {
            foreignLanguages = null;
        } else {
            foreignLanguages = in.readInt();
        }
        translateLanguages = in.createStringArrayList();
        university = in.readString();
        if (in.readByte() == 0) {
            degreeClassification = null;
        } else {
            degreeClassification = in.readInt();
        }
        yearOfGraduation = in.readString();
        if (in.readByte() == 0) {
            yearExperience = null;
        } else {
            yearExperience = in.readInt();
        }
        certificateName = in.createStringArrayList();
        certificates = in.createTypedArrayList(CertificateModel.CREATOR);
        otherInfo = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        role = in.readString();
        requestedAt = in.readString();
        accessToken = in.readString();
        score = in.readDouble();
        orderCreated = in.readInt();
        orderCanceled = in.readInt();
        orderUpdatedPrice = in.readInt();
        orderSuccess = in.readInt();
        isNeedUpdateAvatar = in.readByte() != 0;
        statusMessage = in.readString();
        attachPaths = in.createTypedArrayList(Image.CREATOR);
        settingLanguage = in.readString();
    }

    public static final Creator<PersonalInfo> CREATOR = new Creator<PersonalInfo>() {
        @Override
        public PersonalInfo createFromParcel(Parcel in) {
            return new PersonalInfo(in);
        }

        @Override
        public PersonalInfo[] newArray(int size) {
            return new PersonalInfo[size];
        }
    };

    public String getSettingLanguage() {
        return settingLanguage;
    }

    public void setSettingLanguage(String settingLanguage) {
        this.settingLanguage = settingLanguage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public List<Image> getAttachPaths() {
        return attachPaths;
    }

    public void setAttachPaths(List<Image> attachPaths) {
        this.attachPaths = attachPaths;
    }

    public boolean isNeedUpdateAvatar() {
        return isNeedUpdateAvatar;
    }

    public void setNeedUpdateAvatar(boolean needUpdateAvatar) {
        isNeedUpdateAvatar = needUpdateAvatar;
    }

    public int getOrderCreated() {
        return orderCreated;
    }

    public void setOrderCreated(int orderCreated) {
        this.orderCreated = orderCreated;
    }

    public int getOrderCanceled() {
        return orderCanceled;
    }

    public void setOrderCanceled(int orderCanceled) {
        this.orderCanceled = orderCanceled;
    }

    public int getOrderUpdatedPrice() {
        return orderUpdatedPrice;
    }

    public void setOrderUpdatedPrice(int orderUpdatedPrice) {
        this.orderUpdatedPrice = orderUpdatedPrice;
    }

    public int getOrderSuccess() {
        return orderSuccess;
    }

    public void setOrderSuccess(int orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getForeignLanguages() {
        return foreignLanguages;
    }

    public void setForeignLanguages(Integer foreignLanguages) {
        this.foreignLanguages = foreignLanguages;
    }

    public List<String> getTranslateLanguages() {
        return translateLanguages;
    }

    public void setTranslateLanguages(List<String> translateLanguages) {
        this.translateLanguages = translateLanguages;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Integer getDegreeClassification() {
        return degreeClassification;
    }

    public void setDegreeClassification(Integer degreeClassification) {
        this.degreeClassification = degreeClassification;
    }

    public String getYearOfGraduation() {
        return yearOfGraduation;
    }

    public void setYearOfGraduation(String yearOfGraduation) {
        this.yearOfGraduation = yearOfGraduation;
    }

    public Integer getYearExperience() {
        return yearExperience;
    }

    public void setYearExperience(Integer yearExperience) {
        this.yearExperience = yearExperience;
    }

    public List<String> getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(List<String> certificateName) {
        this.certificateName = certificateName;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public List<CertificateModel> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificateModel> certificates) {
        this.certificates = certificates;
    }

    public boolean isTranslator() {
        return type == 1;
    }

    public boolean canCreateOrder() {
        return !TextUtils.isEmpty(name) && !TextUtils.isEmpty(identityCardNumber) &&
                gender > 0 && !TextUtils.isEmpty(dob) && !TextUtils.isEmpty(avatar) && addressValid();
    }

    private boolean addressValid() {
        return (latitude != null && longitude != null) ||
                !TextUtils.isEmpty(country);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (gender == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gender);
        }
        dest.writeString(dob);
        dest.writeString(email);
        dest.writeString(phone);
        if (type == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(type);
        }
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
        dest.writeString(identityCardNumber);
        dest.writeString(avatar);
        dest.writeString(otp);
        dest.writeString(expiresAt);
        if (addressType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(addressType);
        }
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(country);
        if (foreignLanguages == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(foreignLanguages);
        }
        dest.writeStringList(translateLanguages);
        dest.writeString(university);
        if (degreeClassification == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(degreeClassification);
        }
        dest.writeString(yearOfGraduation);
        if (yearExperience == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(yearExperience);
        }
        dest.writeStringList(certificateName);
        dest.writeTypedList(certificates);
        dest.writeString(otherInfo);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(role);
        dest.writeString(requestedAt);
        dest.writeString(accessToken);
        dest.writeDouble(score);
        dest.writeInt(orderCreated);
        dest.writeInt(orderCanceled);
        dest.writeInt(orderUpdatedPrice);
        dest.writeInt(orderSuccess);
        dest.writeByte((byte) (isNeedUpdateAvatar ? 1 : 0));
        dest.writeTypedList(attachPaths);
        dest.writeString(statusMessage);
        dest.writeString(settingLanguage);
    }
}
