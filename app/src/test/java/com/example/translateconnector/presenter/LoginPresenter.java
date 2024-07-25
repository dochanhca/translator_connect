package com.example.translateconnector.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imoktranslator.TranlookApplication;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.LoginParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.DialogUtils;
import com.imoktranslator.utils.FireBaseDataUtils;
import com.imoktranslator.utils.LocalSharedPreferences;
import com.imoktranslator.utils.LocaleHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ducpv on 3/24/18.
 */

public class LoginPresenter extends BasePresenter {

    private LoginView view;

    public LoginPresenter(Context context, LoginView view) {
        super(context);
        this.view = view;
        LocalSharedPreferences.getInstance(context.getApplicationContext()).clearAll();
    }

    public void login(String phoneNumber, String password) {
        DialogUtils.showProgress(getContext());

        String token = FirebaseInstanceId.getInstance().getToken();
        LoginParam loginParam = new LoginParam(phoneNumber, password);
        loginParam.setDeviceToken(token);
        loginParam.setDeviceType("android");

        requestAPI(getAPI().login(loginParam), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                LocalSharedPreferences.getInstance(getContext()).saveAccessToken(response.getPersonalInfo().getAccessToken());
                LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(response.getPersonalInfo());
                if (response.getPersonalInfo().getStatus() == Constants.STATUS_ACTIVE) {
                    createFirebaseAccount(response.getPersonalInfo());
                } else {
                    view.openVerifyAccountScreen(response.getPersonalInfo().getId(), phoneNumber, password,
                            response.getPersonalInfo().getEmail());
                    DialogUtils.hideProgress();
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                DialogUtils.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void loginToFirebase(PersonalInfo personalInfo) {
        FireBaseDataUtils.getInstance().loginToFireBase(personalInfo.getEmail(),
                Constants.FIREBASE_DEFAULT_PASS, (OnCompleteListener<AuthResult>) task -> {
            DialogUtils.hideProgress();
            if (task.isSuccessful()) {
                updateSettingLanguage(personalInfo);
                view.onLogin(personalInfo);
            } else {
                Log.e("FireBase", "signInWithEmail:failed", task.getException());
                view.onLoginFirebaseError();
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
                view.onLogin(personalInfo);
            } else {
                // If sign in fails, display a message to the user.
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    loginToFirebase(personalInfo);
                } else {
                    Log.e(TAG, "createUserWithEmail:failure", task.getException());
                    DialogUtils.hideProgress();
                    view.onLoginFirebaseError();
                }
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
        } else {
            TranlookApplication.updateAppLanguage(getContext(),
                    personalInfo.getSettingLanguage());
        }
    }

    public interface LoginView extends BaseView {
        void onLogin(PersonalInfo personalInfo);

        void openVerifyAccountScreen(int userId, String phoneNumber, String password, String email);

        void onLoginFirebaseError();
    }
}
