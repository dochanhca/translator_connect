package com.example.translateconnector.data.network;

import com.imoktranslator.BuildConfig;

/**
 * Created by ducpv on 3/24/18.
 */

public class APIConstant {

    public static final String DEV_DOMAIN = "http://api.translatorapp.tk/api/";
    public static final String PRODUCTION_DOMAIN = "http://api.Imoktranslator.com/api/";

    public static final String BASE_URL = BuildConfig.DEBUG ?
            DEV_DOMAIN : PRODUCTION_DOMAIN;

    public static final int SUCCESS_CODE = 200;
    public static final int NO_NETWORK = 1015;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int PHONE_NUMBER_OR_PASSWORD_ERROR = 1004;
    public static final int EMAIL_ERROR = 1005;
    public static final int PHONE_DONT_EXIST = 1006;
    public static final int EMAIL_DONT_EXIST = 1007;
    public static final int OTP_ERROR = 1008;
    public static final int OTP_EXPIRED = 1009;
    public static final int PHONE_NUMBER_DUPLICATED = 1012;
    public static final int EMAIL_DUPLICATED = 1017;
    public static final int EMAIL_OR_PHONE_NOT_CORRECT = 1018;

    public static final int CHAT_ROOM_NOT_FOUND = 9001;
    public static final int TRANSLATOR_BUSY = 2011;
    public static final int OLD_PASSWORD_INCORRECT = 2012;
    public static final int EMAIL_AND_PHONE_REGISTERED = 1033;
    public static final int WAIT_PREVIOUS_REQUEST_APPROVE = 2099;
    public static final int ACCOUNT_LOCKED = 2014;
}
