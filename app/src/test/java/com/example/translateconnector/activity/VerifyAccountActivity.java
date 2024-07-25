package com.example.translateconnector.activity;

import com.imoktranslator.R;

/**
 * Created by tontn on 3/25/18.
 */

public class VerifyAccountActivity extends VerifyOTPActivity {
    @Override
    protected String initVerifyButtonText() {
        return getString(R.string.MH02_006);
    }

    @Override
    protected String initTitle() {
        return getString(R.string.MH03_001);
    }
}
