package com.example.translateconnector.exception;

import android.content.Context;

import com.imoktranslator.R;
import com.imoktranslator.network.APIConstant;

/**
 * Created by ducpv on 3/24/18.
 */

public class NetworkException {

    private int errorCode;
    private String message;
    private Context mContext;

    public NetworkException(Context context, int errorCode, String message) {
        this.mContext = context;
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getMessage() {
        switch (errorCode) {
            case APIConstant.INTERNAL_SERVER_ERROR:
            case APIConstant.BAD_GATEWAY:
                return mContext.getString(R.string.TB_1016);
            case APIConstant.NO_NETWORK:
                return mContext.getString(R.string.TB_1015);
            case APIConstant.PHONE_NUMBER_OR_PASSWORD_ERROR:
                return mContext.getString(R.string.TB_1004);
            case APIConstant.EMAIL_ERROR:
                return mContext.getString(R.string.TB_1005);
            case APIConstant.PHONE_DONT_EXIST:
                return mContext.getString(R.string.TB_1006);
            case APIConstant.EMAIL_DONT_EXIST:
                return mContext.getString(R.string.TB_1007);
            case APIConstant.OTP_ERROR:
                return mContext.getString(R.string.TB_1008);
            case APIConstant.OTP_EXPIRED:
                return mContext.getString(R.string.TB_1009);
            case APIConstant.PHONE_NUMBER_DUPLICATED:
                return mContext.getString(R.string.TB_1012);
            case APIConstant.EMAIL_DUPLICATED:
                return mContext.getString(R.string.TB_1017);
            case APIConstant.EMAIL_OR_PHONE_NOT_CORRECT:
                return mContext.getString(R.string.TB_1018);
            case APIConstant.CHAT_ROOM_NOT_FOUND:
                return mContext.getString(R.string.TB_9001);
            case APIConstant.TRANSLATOR_BUSY:
                return mContext.getString(R.string.TB_2011);
            case APIConstant.OLD_PASSWORD_INCORRECT:
                return mContext.getString(R.string.TB_2012);
            case APIConstant.EMAIL_AND_PHONE_REGISTERED:
                return mContext.getString(R.string.TB_1033);
            case APIConstant.ACCOUNT_LOCKED:
                return mContext.getString(R.string.TB_2014);
            default:
                return message;

        }
    }
}
