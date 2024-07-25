package com.example.translateconnector.activity;

import com.imoktranslator.R;

/**
 * Created by tontn on 3/26/18.
 */

public class VerifySecurityCodeActivity extends VerifyOTPActivity {
    @Override
    protected String initVerifyButtonText() {
        return getString(R.string.MH03_003);
    }

    @Override
    protected String initTitle() {
        return getString(R.string.MH07_002);
    }
}
