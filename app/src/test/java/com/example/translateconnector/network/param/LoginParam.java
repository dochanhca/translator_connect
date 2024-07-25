package com.example.translateconnector.network.param;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tvoer on 3/30/18.
 */

public class LoginParam {
    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("grant_type")
    @Expose
    private String grantType;

    @SerializedName("client_id")
    @Expose
    private String clientId;

    @SerializedName("client_secret")
    @Expose
    private String clientSecret;

    @SerializedName("device_token")
    @Expose
    private String deviceToken;

    @SerializedName("device_type")
    @Expose
    private String deviceType;


    public LoginParam(String username, String password) {
        this.username = username;
        this.password = password;
        this.grantType = "password";
        this.clientId = "2";
        this.clientSecret = "OL6yNHnhLWrUknxlkCwhlvxUhKiZvPcTaR0VFil3";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
