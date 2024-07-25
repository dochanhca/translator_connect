package com.example.translateconnector.utils;

import android.content.Context;

import com.imoktranslator.dialog.ProgressDialog;

/**
 * Created by ducpv on 3/25/18.
 */

public class DialogUtils {
    private static ProgressDialog mProgressDialog;
    private static int mProgressCounter;

    public static void showProgress(Context context) {
        if (mProgressDialog == null) {
            mProgressCounter = 0;
            mProgressDialog = new ProgressDialog(context);
        } else {
            mProgressCounter++;
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.showProgress();
        }
    }

    public static void hideProgress() {
        if (mProgressDialog != null) {
            mProgressCounter--;
            if (mProgressCounter <= 0) {
                mProgressCounter = 0;
                mProgressDialog.hideProgress();
                mProgressDialog = null;
            }
        }
    }
}
