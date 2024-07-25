package com.example.translateconnector.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.model.PhoneCodeItem;
import com.imoktranslator.model.RegisterItem;
import com.imoktranslator.presenter.RegisterPresenter;
import com.imoktranslator.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ducpv on 3/25/18.
 */

public class RegisterActivity extends BaseActivity implements
        HeaderView.BackButtonClickListener, RegisterPresenter.RegisterView {

    private static final int REQUEST_SELECT_COUNTRY = 111;

    @BindView(R.id.header)
    HeaderView headerView;
    @BindView(R.id.txt_phone_code_number)
    OpenSansTextView txtPhoneCodeNumber;
    @BindView(R.id.edt_phone_number)
    TextFieldView fieldPhoneNumber;
    @BindView(R.id.field_name)
    TextFieldView fieldName;
    @BindView(R.id.field_email)
    TextFieldView fieldEmail;
    @BindView(R.id.field_password)
    TextFieldView fieldPassword;
    @BindView(R.id.field_re_password)
    TextFieldView fieldRePassword;
    @BindView(R.id.txt_register)
    OpenSansBoldTextView txtRegister;

    @BindView(R.id.txt_phone_number_error)
    OpenSansTextView txtPhoneNumberError;

    private RegisterPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViews() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        presenter = new RegisterPresenter(this, this);
        headerView.setCallback(this);
        headerView.setTittle(getString(R.string.MH02_001));

        fieldPhoneNumber.getEdtValue().setInputType(InputType.TYPE_CLASS_PHONE);
        fieldPassword.getEdtValue().setInputType(InputType.TYPE_CLASS_TEXT);
        fieldPassword.getEdtValue().setTransformationMethod(PasswordTransformationMethod.getInstance());
        fieldRePassword.getEdtValue().setInputType(InputType.TYPE_CLASS_TEXT);
        fieldRePassword.getEdtValue().setTransformationMethod(PasswordTransformationMethod.getInstance());
        fieldRePassword.getEdtValue().setImeOptions(EditorInfo.IME_ACTION_DONE);

        initErrorListener();
    }

    private void initErrorListener() {
        fieldPhoneNumber.setOnTextFieldErrorListener(s -> {
            if (s.toString().isEmpty()) {
                txtPhoneNumberError.setVisibility(View.VISIBLE);
                txtPhoneNumberError.setText(String.format(getString(R.string.TB_1001), getString(R.string.MH02_002)));
            } else if (!s.toString().matches(Constants.PHONE_PATTERN)) {
                txtPhoneNumberError.setVisibility(View.VISIBLE);
                txtPhoneNumberError.setText(getString(R.string.TB_1003));
            } else {
                txtPhoneNumberError.setVisibility(View.GONE);
            }

        });

        fieldName.setOnTextFieldErrorListener(s -> fieldName.setError(s.isEmpty() ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH02_003)) : ""));


        fieldEmail.setOnTextFieldErrorListener(s -> {
            if (s.toString().isEmpty()) {
                fieldEmail.setError(String.format(getString(R.string.TB_1001),
                        getString(R.string.MH02_008)));
            } else if (!presenter.isValidEmail(s.toString())) {
                fieldEmail.setError(getString(R.string.TB_1005));
            } else {
                fieldEmail.setError("");
            }
        });

        fieldPassword.setOnTextFieldErrorListener(s -> {
            if (s.toString().isEmpty()) {
                fieldPassword.setError(String.format(getString(R.string.TB_1001),
                        getString(R.string.MH05_003)));
            } else if (s.toString().length() < Constants.PASSWORD_MIN_LENGTH) {
                fieldPassword.setError(getString(R.string.TB_1010));
            } else {
                fieldPassword.setError("");
            }
        });

        fieldRePassword.setOnTextFieldErrorListener(s -> {
            if (s.isEmpty()) {
                fieldRePassword.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH05_003)));
            } else if (s.length() < Constants.PASSWORD_MIN_LENGTH) {
                fieldRePassword.setError(getString(R.string.TB_1010));
            } else if (!fieldPassword.getText().equals(fieldRePassword.getText())) {
                fieldRePassword.setError(getString(R.string.TB_1014));
            } else {
                fieldRePassword.setError("");
            }
        });
    }

    @OnClick({R.id.txt_phone_code_number, R.id.txt_register, R.id.txt_account_existed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_phone_code_number:
                ChooseCountryActivity.startAcitivity(this, REQUEST_SELECT_COUNTRY);
                break;
            case R.id.txt_register:
                if (isDataValid()) {
                    presenter.doRegister(getUserItem());
                } else {
                    requestFocus();
                }
                break;
            case R.id.txt_account_existed:
                onBackPressed();
                break;
        }
    }

    private void requestFocus() {
        if (txtPhoneNumberError.getVisibility() == View.VISIBLE) {
            showKeyboard(fieldPhoneNumber.getEdtValue());
        } else if (fieldName.isError()) {
            showKeyboard(fieldName.getEdtValue());
        } else if (fieldEmail.isError()) {
            showKeyboard(fieldEmail.getEdtValue());
        } else if (fieldPassword.isError()) {
            showKeyboard(fieldPassword.getEdtValue());
        } else if (fieldRePassword.isError()) {
            showKeyboard(fieldRePassword.getEdtValue());
        }
    }

    /**
     * Using for case user don't focus on any Field & Click Register.
     *
     * @return
     */
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

        fieldName.setError(fieldName.getText().isEmpty() ?
                String.format(getString(R.string.TB_1001), getString(R.string.MH02_003)) : "");


        if (fieldEmail.getText().isEmpty()) {
            fieldEmail.setError(String.format(getString(R.string.TB_1001),
                    getString(R.string.MH02_008)));
        } else if (!presenter.isValidEmail(fieldEmail.getText())) {
            fieldEmail.setError(getString(R.string.TB_1005));
        } else {
            fieldEmail.setError("");
        }

        if (fieldPassword.getText().isEmpty()) {
            fieldPassword.setError(String.format(getString(R.string.TB_1001),
                    getString(R.string.MH05_003)));
        } else if (fieldPassword.getText().toString().length() < Constants.PASSWORD_MIN_LENGTH) {
            fieldPassword.setError(getString(R.string.TB_1010));
        } else {
            fieldPassword.setError("");
        }

        if (fieldRePassword.getText().isEmpty()) {
            fieldRePassword.setError(String.format(getString(R.string.TB_1001),
                    getString(R.string.MH05_003)));
        } else if (fieldRePassword.getText().toString().length() < Constants.PASSWORD_MIN_LENGTH) {
            fieldRePassword.setError(getString(R.string.TB_1010));
        } else if (!fieldPassword.getText().equals(fieldRePassword.getText())) {
            fieldRePassword.setError(getString(R.string.TB_1014));
        } else {
            fieldRePassword.setError("");
        }

        return isAllFieldValid();
    }

    private boolean isAllFieldValid() {
        return txtPhoneNumberError.getVisibility() == View.GONE &&
                !fieldName.isError() && !fieldEmail.isError() && !fieldPassword.isError()
                && !fieldRePassword.isError() && fieldPassword.getText().equals(fieldRePassword.getText());
    }

    private RegisterItem getUserItem() {
        RegisterItem registerItem = new RegisterItem();
        registerItem.setPhone(txtPhoneCodeNumber.getText() + " " + fieldPhoneNumber.getText());
        registerItem.setName(fieldName.getText());
        registerItem.setEmail(fieldEmail.getText());
        registerItem.setPass(fieldPassword.getEdtValue().getText().toString());
        registerItem.setRePassword(fieldRePassword.getEdtValue().getText().toString());

        return registerItem;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_COUNTRY && resultCode == RESULT_OK) {
            PhoneCodeItem phoneCodeItem = data.getExtras().getParcelable(ChooseCountryActivity.SELECTED_COUNTRY);
            txtPhoneCodeNumber.setText(phoneCodeItem.getCode());
        }
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }


    @Override
    public void openVerifyAccountScreen(int userId, String phoneNumber, String password, String email) {
        Toast.makeText(this, getString(R.string.TB_1028), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, VerifyAccountActivity.class);
        intent.putExtra(VerifyOTPActivity.KEY_USER_ID, userId);
        intent.putExtra(VerifyOTPActivity.KEY_PHONE_NUMBER, phoneNumber);
        intent.putExtra(VerifyOTPActivity.KEY_PASSWORD, password);
        intent.putExtra(VerifyOTPActivity.KEY_EMAIL, email);
        intent.putExtra(VerifyOTPActivity.KEY_ACTION, VerifyOTPActivity.ACTION_VERIFY_NEW_ACCOUNT);
        startActivity(intent);
        finish();

    }
}
