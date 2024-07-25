package com.example.translateconnector.activity;

import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.presenter.ChangePasswordPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ton on 3/31/18.
 */

public class ChangePasswordActivity extends BaseActivity implements HeaderView.BackButtonClickListener,
        ChangePasswordPresenter.ChangePasswordView {
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_FORGOT_PWD = "key_forgot_pwd";

    @BindView(R.id.header_change_password)
    HeaderView headerView;
    @BindView(R.id.tf_old_password)
    TextFieldView tfOldPassword;
    @BindView(R.id.tf_new_password)
    TextFieldView tfNewPassword;
    @BindView(R.id.tf_confirm_new_password)
    TextFieldView tfConfirmPassword;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    private boolean isFogotPassword = true;

    private ChangePasswordPresenter presenter;
    private int mUserID;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void initViews() {
        presenter = new ChangePasswordPresenter(this, this);
        mUserID = getIntent().getIntExtra(KEY_USER_ID, -1);
        isFogotPassword = getIntent().getBooleanExtra(KEY_FORGOT_PWD, true);

        headerView.setTittle(getResources().getString(R.string.MH05_011));
        headerView.setCallback(this);

        if (isFogotPassword) {
            tfOldPassword.setVisibility(View.INVISIBLE);
        } else {
            tfOldPassword.setVisibility(View.VISIBLE);
            tfOldPassword.setOnTextFieldErrorListener(s -> oldPasswordValidation(s.trim()));
        }
        tfOldPassword.getEdtValue().setTransformationMethod(PasswordTransformationMethod.getInstance());

        tfNewPassword.getEdtValue().setTransformationMethod(PasswordTransformationMethod.getInstance());
        tfNewPassword.setOnTextFieldErrorListener(s -> passwordValidation(s.trim(), tfOldPassword.getText()));

        tfConfirmPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tfConfirmPassword.getEdtValue().setTransformationMethod(PasswordTransformationMethod.getInstance());
        tfConfirmPassword.setOnTextFieldErrorListener(s -> confirmPasswordValidation(tfNewPassword.getText(), s.trim()));
    }

    @OnClick(R.id.tv_confirm)
    public void onViewClicked(View v) {
        hideKeyboard();
        if (isDataValid()) {
            presenter.confirmChangePassword(isFogotPassword);
        } else {
            requestFocus();
        }
    }

    private void requestFocus() {
        if (tfOldPassword.isError()) {
            showKeyboard(tfOldPassword.getEdtValue());
        } else if (tfNewPassword.isError()) {
            showKeyboard(tfNewPassword.getEdtValue());
        } else if (tfConfirmPassword.isError()) {
            showKeyboard(tfConfirmPassword.getEdtValue());
        }
    }

    private boolean isDataValid() {
        String oldPassword = tfOldPassword.getText();
        String password = tfNewPassword.getText();
        String confirmPassword = tfConfirmPassword.getText();
        oldPasswordValidation(oldPassword);
        passwordValidation(password, oldPassword);
        confirmPasswordValidation(password, confirmPassword);
        return isAllErrorMessageGone();
    }

    private void confirmPasswordValidation(String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            tfConfirmPassword.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH05_010)));
        } else if (confirmPassword.length() < 6) {
            tfConfirmPassword.setError(getString(R.string.TB_1010));
        } else if (!confirmPassword.equals(password)) {
            tfConfirmPassword.setError(getString(R.string.TB_1014));
        } else {
            tfConfirmPassword.setError("");
        }
    }

    private void passwordValidation(String password, String oldPassword) {
        if (password.isEmpty()) {
            tfNewPassword.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH05_009)));
        } else if (password.length() < 6) {
            tfNewPassword.setError(getString(R.string.TB_1010));
        } else {
            if (!isFogotPassword && password.equals(oldPassword)) {
                tfNewPassword.setError(getString(R.string.MH05_013));
            } else {
                tfNewPassword.setError("");
            }
        }
    }

    private void oldPasswordValidation(String oldpassword) {
        if (!isFogotPassword) {
            if (oldpassword.isEmpty()) {
                tfOldPassword.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH02_004)));
            } else if (oldpassword.length() < 6) {
                tfOldPassword.setError(getString(R.string.TB_1010));
            } else {
                tfOldPassword.setError("");
            }
        }
    }

    private boolean isAllErrorMessageGone() {
        if (!tfNewPassword.isError() && !tfConfirmPassword.isError() && !tfOldPassword.isError()) {
            return true;
        }
        return false;
    }

    @Override
    public void backButtonClicked() {
        onBackPressed();
    }

    @Override
    public int getUserID() {
        return mUserID;
    }

    @Override
    public String getNewPassword() {
        return tfNewPassword.getEdtValue().getText().toString().trim();
    }

    @Override
    public String getOldPassword() {
        return tfOldPassword.getEdtValue().getText().toString().trim();
    }

    @Override
    public void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPress() {
        onBackPressed();
    }

    public static void startActivity(BaseActivity activity, int userId, boolean isForgotPassword) {
        Intent intent = new Intent(activity, ChangePasswordActivity.class);
        intent.putExtra(KEY_FORGOT_PWD, isForgotPassword);
        intent.putExtra(KEY_USER_ID, userId);
        activity.startActivity(intent);
    }
}
