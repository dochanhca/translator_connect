package com.example.translateconnector.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.presenter.VerifyOTPPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by tontn on 3/25/18.
 */

public abstract class VerifyOTPActivity extends BaseActivity implements HeaderView.BackButtonClickListener,
        VerifyOTPPresenter.VerifyOTPView {
    public static final int ACTION_FORGOT_PASSWORD = 0;
    public static final int ACTION_VERIFY_NEW_ACCOUNT = 1;
    public static final String KEY_ACTION = "key_action";

    public static final String KEY_USER_ID = "KEY USER ID";
    public static final String KEY_PHONE_NUMBER = "key_phone_number";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_EMAIL = "key_email";
    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.tvVerify)
    TextView btVerify;
    @BindView(R.id.edtOTP)
    TextFieldView edtOTP;
    @BindView(R.id.tvResend)
    OpenSansBoldTextView tvResendOTP;

    private VerifyOTPPresenter presenter;
    private int mUserID;
    private String phoneNumber;
    private String password;
    private String email;
    private int action;
    private Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verify_otp;
    }

    @Override
    protected void initViews() {
        presenter = new VerifyOTPPresenter(this, this);
        extractDataFromIntent();

        header.setCallback(this);
        header.setTittle(initTitle());
        btVerify.setText(initVerifyButtonText());
        edtOTP.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtOTP.setOnTextFieldErrorListener(edtValue -> edtOTP.setErrorVisible(
                !(edtValue.length() == 0 || edtValue.length() == 6)));
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        mUserID = intent.getIntExtra(KEY_USER_ID, 0);
        phoneNumber = intent.getStringExtra(KEY_PHONE_NUMBER);
        password = intent.getStringExtra(KEY_PASSWORD);
        email = intent.getStringExtra(KEY_EMAIL);
        action = intent.getIntExtra(KEY_ACTION, -1);
    }

    @OnClick({R.id.tvVerify, R.id.tvResend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvVerify:
                presenter.verifyClicked();
                break;
            case R.id.tvResend:
                presenter.resendOTPClicked();
                break;
        }
    }

    protected abstract String initVerifyButtonText();

    protected abstract String initTitle();

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public String getOTP() {
        return edtOTP.getText().trim();
    }

    @Override
    public void showRequireOTP() {
        showNotifyDialog(String.format(getString(R.string.TB_1001), getString(R.string.MH03_002)), getString(R.string.MH12_008));
    }

    @Override
    public void showSentOTP() {
        Toast.makeText(this, getString(R.string.TB_1028), Toast.LENGTH_SHORT).show();
        tvResendOTP.setEnabled(false);
        tvResendOTP.setTextColor(ContextCompat.getColor(this, R.color.text_brown));
        handler.postDelayed(() -> {
            tvResendOTP.setEnabled(true);
            tvResendOTP.setTextColor(ContextCompat.getColor(VerifyOTPActivity.this, R.color.dark_sky_blue));
        }, 120000);
    }

    @Override
    public boolean isDataValid() {
        edtOTP.setErrorVisible(edtOTP.getText().length() < 6 ? true : false);
        return !edtOTP.isError();
    }

    @Override
    public int getUserID() {
        return mUserID;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUserEmail() {
        return email;
    }

    @Override
    public void openHomeScreen(PersonalInfo personalInfo) {
        LocalSharedPreferences.getInstance(this).saveBooleanData(Constants.IS_USER_LOGGED_IN, true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void openChangePasswordScreen(int userId) {
        ChangePasswordActivity.startActivity(this, userId, true);
    }

    @Override
    public int getAction() {
        return action;
    }
}
