package com.example.translateconnector.activity;

import android.content.Intent;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.model.PhoneCodeItem;
import com.imoktranslator.presenter.LoginPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ducpv on 3/24/18.
 */

public class LoginActivity extends BaseActivity implements LoginPresenter.LoginView {

    private static final int REQUEST_SELECT_COUNTRY = 111;

    @BindView(R.id.txt_phone_code_number)
    OpenSansTextView txtPhoneCode;
    @BindView(R.id.edt_phone_number)
    TextFieldView fieldPhoneNumber;
    @BindView(R.id.edt_password)
    TextFieldView fieldPassword;
    @BindView(R.id.txt_login)
    OpenSansBoldTextView txtLogin;
    @BindView(R.id.txt_forgot_pass)
    OpenSansBoldTextView txtForgotPass;
    @BindView(R.id.txt_register_account)
    OpenSansBoldTextView txtRegisterAccount;
    @BindView(R.id.ll_action)
    LinearLayout llAction;
    @BindView(R.id.txt_phone_number_error)
    OpenSansTextView txtPhoneNumberError;

    private LoginPresenter loginPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        loginPresenter = new LoginPresenter(this, this);

        fieldPhoneNumber.getEdtValue().setInputType(InputType.TYPE_CLASS_PHONE);
        fieldPassword.getEdtValue().setInputType(InputType.TYPE_CLASS_TEXT);
        fieldPassword.getEdtValue().setTransformationMethod(PasswordTransformationMethod.getInstance());
        fieldPassword.getEdtValue().setImeOptions(EditorInfo.IME_ACTION_DONE);

        initListeners();
    }

    private void initListeners() {
        fieldPhoneNumber.setOnTextFieldErrorListener(s -> {
            if (s.isEmpty()) {
                txtPhoneNumberError.setVisibility(View.VISIBLE);
                txtPhoneNumberError.setText(String.format(getString(R.string.TB_1001), getString(R.string.MH02_002)));
            } else if (!s.matches(Constants.PHONE_PATTERN)) {
                txtPhoneNumberError.setVisibility(View.VISIBLE);
                txtPhoneNumberError.setText(getString(R.string.TB_1003));
            } else {
                txtPhoneNumberError.setVisibility(View.GONE);
            }

        });

        fieldPassword.setOnTextFieldErrorListener(s -> {
            if (s.isEmpty()) {
                fieldPassword.setErrorVisible(true);
                fieldPassword.setError(String.format(getString(R.string.TB_1001),
                        getString(R.string.MH05_003)));
            } else if (s.length() < Constants.PASSWORD_MIN_LENGTH) {
                fieldPassword.setErrorVisible(true);
                fieldPassword.setError(getString(R.string.TB_1010));
            } else {
                fieldPassword.setErrorVisible(false);
            }
        });
    }

    @OnClick({R.id.txt_phone_code_number, R.id.txt_login, R.id.txt_forgot_pass, R.id.txt_register_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_phone_code_number:
                ChooseCountryActivity.startAcitivity(this, REQUEST_SELECT_COUNTRY);
                break;
            case R.id.txt_login:
                if (isDataValid()) {
                    loginPresenter.login(txtPhoneCode.getText().toString()
                            + " " + fieldPhoneNumber.getText().toString(), fieldPassword.getText());
                } else {
                    requestFocus();
                }
                break;
            case R.id.txt_forgot_pass:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.txt_register_account:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void requestFocus() {
        if (txtPhoneNumberError.getVisibility() == View.VISIBLE) {
            showKeyboard(fieldPhoneNumber.getEdtValue());
        } else if (fieldPassword.isError()) {
            showKeyboard(fieldPassword.getEdtValue());
        }
    }

    private boolean isDataValid() {
        if (fieldPhoneNumber.getText().isEmpty()) {
            txtPhoneNumberError.setVisibility(View.VISIBLE);
            txtPhoneNumberError.setText(String.format(getString(R.string.TB_1001), getString(R.string.MH02_002)));
        } else if (!fieldPhoneNumber.getText().matches(Constants.PHONE_PATTERN)) {
            txtPhoneNumberError.setVisibility(View.VISIBLE);
            txtPhoneNumberError.setText(getString(R.string.TB_1003));
        } else {
            txtPhoneNumberError.setVisibility(View.GONE);
        }

        if (fieldPassword.getText().isEmpty()) {
            fieldPassword.setErrorVisible(true);
            fieldPassword.setError(String.format(getString(R.string.TB_1001),
                    getString(R.string.MH05_003)));
        } else if (fieldPassword.getText().length() < Constants.PASSWORD_MIN_LENGTH) {
            fieldPassword.setErrorVisible(true);
            fieldPassword.setError(getString(R.string.TB_1010));
        } else {
            fieldPassword.setErrorVisible(false);
        }

        return isAllFieldValid();
    }

    private boolean isAllFieldValid() {
        return txtPhoneNumberError.getVisibility() == View.GONE
                && !fieldPassword.isError();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_COUNTRY && resultCode == RESULT_OK) {
            PhoneCodeItem phoneCodeItem = data.getExtras().getParcelable(ChooseCountryActivity.SELECTED_COUNTRY);
            txtPhoneCode.setText(phoneCodeItem.getCode());
        }
    }

    @Override
    public void onLogin(PersonalInfo personalInfo) {
        LocalSharedPreferences.getInstance(this).saveBooleanData(Constants.IS_USER_LOGGED_IN, true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void openVerifyAccountScreen(int mUserID, String phoneNumber, String password, String email) {
        Intent intent = new Intent(this, VerifyAccountActivity.class);
        intent.putExtra(VerifyOTPActivity.KEY_USER_ID, mUserID);
        intent.putExtra(VerifyOTPActivity.KEY_PHONE_NUMBER, phoneNumber);
        intent.putExtra(VerifyOTPActivity.KEY_PASSWORD, password);
        intent.putExtra(VerifyOTPActivity.KEY_EMAIL, email);
        intent.putExtra(VerifyOTPActivity.KEY_ACTION, VerifyOTPActivity.ACTION_VERIFY_NEW_ACCOUNT);
        startActivity(intent);
    }

    @Override
    public void onLoginFirebaseError() {
        showNotifyDialog("Đăng nhập thất bại");
    }
}
