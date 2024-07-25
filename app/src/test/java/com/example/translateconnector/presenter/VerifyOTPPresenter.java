package com.example.translateconnector.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imoktranslator.activity.VerifyOTPActivity;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.LoginParam;
import com.imoktranslator.network.param.ResendOTPParam;
import com.imoktranslator.network.param.VerifyOTPParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.network.response.ResendOTPResponse;
import com.imoktranslator.network.response.VerifyOTPResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.LocaleHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tontn on 3/25/18.
 */

public class VerifyOTPPresenter extends BasePresenter {
    private VerifyOTPView view;

    public VerifyOTPPresenter(Context context, VerifyOTPView view) {
        super(context);
        this.view = view;
    }

    public void verifyClicked() {
        String OTP = view.getOTP();
        int userId = view.getUserID();
        if (view.isDataValid()) {
            if (TextUtils.isEmpty(OTP)) {
                view.showRequireOTP();
            } else {
                verifyOTP(OTP, userId);
            }
        }
    }

    private void verifyOTP(String OTP, int userId) {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().verifyOTP(new VerifyOTPParam(OTP, userId)), new BaseRequest<VerifyOTPResponse>() {
            @Override
            public void onSuccess(VerifyOTPResponse response) {
                if (view.getAction() == VerifyOTPActivity.ACTION_FORGOT_PASSWORD) {
                    view.openChangePasswordScreen(userId);
                    DialogUtils.hideProgress();
                } else if (view.getAction() == VerifyOTPActivity.ACTION_VERIFY_NEW_ACCOUNT) {
                    login(view.getPhoneNumber(), view.getPassword());
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void login(String phoneNumber, String password) {
        String token = FirebaseInstanceId.getInstance().getToken();
        LoginParam loginParam = new LoginParam(phoneNumber, password);
        loginParam.setDeviceToken(token);
        loginParam.setDeviceType("android");
        requestAPI(getAPI().login(loginParam), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                DialogUtils.hideProgress();
                LocalSharedPreferences.getInstance(getContext()).saveAccessToken(response.getPersonalInfo().getAccessToken());
                LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(response.getPersonalInfo());
                createFirebaseAccount(response.getPersonalInfo());
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void createFirebaseAccount(PersonalInfo personalInfo) {
        FireBaseDataUtils.getInstance().createUserWithEmail(personalInfo.getEmail(),
                Constants.FIREBASE_DEFAULT_PASS, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        DialogUtils.hideProgress();
                        updateSettingLanguage(personalInfo);
                        view.openHomeScreen(personalInfo);
                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            loginToFirebase(personalInfo);
                        } else {
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            DialogUtils.hideProgress();
                        }
                    }
                });
    }

    private void loginToFirebase(PersonalInfo personalInfo) {
        FireBaseDataUtils.getInstance().loginToFireBase(personalInfo.getEmail(),
                Constants.FIREBASE_DEFAULT_PASS, (OnCompleteListener<AuthResult>) task -> {
                    DialogUtils.hideProgress();
                    if (task.isSuccessful()) {
                        updateSettingLanguage(personalInfo);
                        view.openHomeScreen(personalInfo);
                    } else {
                        Log.e("FireBase", "signInWithEmail:failed", task.getException());
                    }
                });
    }

    public void resendOTPClicked() {
        DialogUtils.showProgress(getContext());
        requestAPI(getAPI().resendOTP(new ResendOTPParam(view.getUserEmail())), new BaseRequest<ResendOTPResponse>() {
            @Override
            public void onSuccess(ResendOTPResponse response) {
                DialogUtils.hideProgress();
                view.showSentOTP();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void updateSettingLanguage(PersonalInfo personalInfo) {
        if (TextUtils.isEmpty(personalInfo.getSettingLanguage())) {
            Map<String, String> updateStatus = new HashMap<>();
            updateStatus.put("setting_language", LocaleHelper.getLanguage(getContext()));
            requestAPI(getAPI().updateProfileInfo(updateStatus), new BaseRequest<PersonalInfoResponse>() {
                @Override
                public void onSuccess(PersonalInfoResponse response) {
                    view.hideProgress();
                }

                @Override
                public void onFailure(int errCode, String errMessage) {

                }
            });
        }
    }

    public interface VerifyOTPView extends BaseView {

        String getOTP();

        void showRequireOTP();

        void showSentOTP();

        boolean isDataValid();

        int getUserID();

        String getPhoneNumber();

        String getPassword();

        void openHomeScreen(PersonalInfo personalInfo);

        String getUserEmail();

        int getAction();

        void openChangePasswordScreen(int userId);
    }
}
