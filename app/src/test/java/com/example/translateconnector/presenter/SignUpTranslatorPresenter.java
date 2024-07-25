package com.example.translateconnector.presenter;

import android.content.Context;

import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.asynctask.LoadAvatarTask;
import com.imoktranslator.asynctask.LoadBitmapTask;
import com.imoktranslator.asynctask.LoadCertificateTask;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.param.PersonalInfoParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;

public class SignUpTranslatorPresenter extends BasePresenter {
    private SignUpTranslatorView view;

    public SignUpTranslatorPresenter(Context context, SignUpTranslatorView view) {
        super(context);
        this.view = view;
    }

    private void uploadCertificates(List<MultipartBody.Part> parts) {
        requestAPI(getAPI().uploadCertificates(parts), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                view.hideProgress();
                view.backToUserProfileScreen(response.getPersonalInfo());
                LocalSharedPreferences.getInstance(getContext()).removeData(Constants.KEY_USER_INFO_CACHED);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                view.notify(errMessage);
            }
        });
    }

    public void signUpTranslator(PersonalInfoParam param) {
        view.showProgress();
        if (param.isNeedUpdateAvatar()) {
            loadProfileImage(param);
        } else {
            uploadTranslatorProfile(param);
        }

    }

    private void uploadTranslatorProfile(PersonalInfoParam param) {
        requestAPI(getAPI().signUpTranslator(param), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                loadCertificates();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
            }
        });
    }

    public void uploadAvatar(MultipartBody.Part part, PersonalInfoParam param) {
        requestAPI(getAPI().signUpTranslator(part), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                uploadTranslatorProfile(param);
                PersonalInfo personalInfo = LocalSharedPreferences.getInstance(getContext()).getPersonalInfo();
                personalInfo.setAvatar(response.getPersonalInfo().getAvatar());
                LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(personalInfo);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void loadProfileImage(PersonalInfoParam param) {
        LoadAvatarTask loadAvatarTask = new LoadAvatarTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(MultipartBody.Part part) {
                super.onPostExecute(part);
                uploadAvatar(part, param);

            }
        };
        loadAvatarTask.execute(view.getProfileImage());
    }

    /**
     * Load certificate images with async task to prevent images auto rotate & Out of memory exception
     */
    private void loadCertificates() {
        LoadCertificateTask loadCertificateTask = new LoadCertificateTask() {
            @Override
            protected void onPostExecute(List<MultipartBody.Part> parts) {
                super.onPostExecute(parts);
                uploadCertificates(parts);
            }
        };
        loadCertificateTask.execute(view.getImages());
    }

    public interface SignUpTranslatorView extends BaseView {
        List<Image> getImages();

        File getProfileImage();

        void backToUserProfileScreen(PersonalInfo personalInfo);
    }
}
