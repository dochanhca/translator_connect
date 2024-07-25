package com.example.translateconnector.activity;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.model.PhoneCodeItem;
import com.imoktranslator.presenter.ForgotPasswordPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.Validator;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity implements HeaderView.BackButtonClickListener, ForgotPasswordPresenter.ForgotPasswordView {
    private static final int REQUEST_SELECT_COUNTRY = 111;
    @BindView(R.id.header_forgot_password)
    HeaderView headerView;
    @BindView(R.id.txt_phone_code_number)
    OpenSansTextView txtPhoneCode;
    @BindView(R.id.edt_phone_number)
    TextFieldView fieldPhoneNumber;
    @BindView(R.id.txt_phone_number_error)
    OpenSansTextView txtPhoneNumberError;
    @BindView(R.id.edtEmail)
    TextFieldView fieldEmail;
    @BindView(R.id.bt_send)
    OpenSansBoldTextView btSend;
    @BindView(R.id.bt_cancel)
    OpenSansBoldTextView btCancel;

    private ForgotPasswordPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initViews() {
        headerView.setCallback(this);
        fieldPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        fieldPhoneNumber.setOnTextFieldErrorListener(phone -> txtPhoneNumberError.setVisibility(Validator.validPhone(phone.toString()) ? View.GONE : View.VISIBLE));
        fieldEmail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        fieldEmail.setOnTextFieldErrorListener(email -> fieldEmail.setErrorVisible(!Validator.validEmail(email.toString())));

        presenter = new ForgotPasswordPresenter(this, this);

        initListeners();
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @OnClick({R.id.txt_phone_code_number, R.id.bt_send, R.id.bt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_phone_code_number:
                ChooseCountryActivity.startAcitivity(this, REQUEST_SELECT_COUNTRY);
                break;
            case R.id.bt_send:
                doforgotPass();
                break;
            case R.id.bt_cancel:
                onBackPressed();
                break;
        }
    }

    private void doforgotPass() {
        if (isDataValid()) {
            String email = fieldEmail.getText();
            String phone = txtPhoneCode.getText().toString().trim() + " " + fieldPhoneNumber.getText();
            presenter.requestSettingNewPassword(email, phone);
        } else {
            requestFocus();
        }
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
    public void openSecurityCodeScreen(int userId, String userEmail) {
        Toast.makeText(this, getString(R.string.TB_1028), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, VerifySecurityCodeActivity.class);
        intent.putExtra(VerifyOTPActivity.KEY_USER_ID, userId);
        intent.putExtra(VerifyOTPActivity.KEY_EMAIL, userEmail);
        intent.putExtra(VerifyOTPActivity.KEY_ACTION, VerifyOTPActivity.ACTION_FORGOT_PASSWORD);
        startActivity(intent);
    }

    private void initListeners() {
        fieldPhoneNumber.setOnTextFieldErrorListener(s -> {
            if (s.toString().isEmpty()) {
                txtPhoneNumberError.setVisibility(View.VISIBLE);
                txtPhoneNumberError.setText(String.format(getString(R.string.TB_1001), getString(R.string.MH02_002)));
            } else if (!s.trim().toString().matches(Constants.PHONE_PATTERN)) {
                txtPhoneNumberError.setVisibility(View.VISIBLE);
                txtPhoneNumberError.setText(getString(R.string.TB_1003));
            } else {
                txtPhoneNumberError.setVisibility(View.GONE);
            }

        });

        fieldEmail.setOnTextFieldErrorListener(s -> {
            if (s.toString().isEmpty()) {
                fieldEmail.setErrorVisible(true);
                fieldEmail.setError(String.format(getString(R.string.TB_1001),
                        getString(R.string.MH02_008)));
            } else if (!presenter.isValidEmail(s.toString())) {
                fieldEmail.setErrorVisible(true);
                fieldEmail.setError(getString(R.string.TB_1005));
            } else {
                fieldEmail.setErrorVisible(false);
            }
        });
    }

    private void requestFocus() {
        if (txtPhoneNumberError.getVisibility() == View.VISIBLE) {
            showKeyboard(fieldPhoneNumber.getEdtValue());
        } else if (fieldEmail.isError()) {
            showKeyboard(fieldEmail.getEdtValue());
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

        if (fieldEmail.getText().isEmpty()) {
            fieldEmail.setErrorVisible(true);
            fieldEmail.setError(String.format(getString(R.string.TB_1001),
                    getString(R.string.MH02_008)));
        } else if (!presenter.isValidEmail(fieldEmail.getText())) {
            fieldEmail.setErrorVisible(true);
            fieldEmail.setError(getString(R.string.TB_1005));
        } else {
            fieldEmail.setErrorVisible(false);
        }

        return isAllFieldValid();
    }

    private boolean isAllFieldValid() {
        return txtPhoneNumberError.getVisibility() == View.GONE &&
                !fieldEmail.isError();
    }

}
