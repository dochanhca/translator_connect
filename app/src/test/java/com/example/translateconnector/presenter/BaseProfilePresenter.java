package com.example.translateconnector.presenter;

import android.content.Context;
import android.util.Log;

import com.darsh.multipleimageselect.models.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imoktranslator.asynctask.LoadCertificateTask;
import com.imoktranslator.model.PersonalInfo;
import com.imoktranslator.network.APIConstant;
import com.imoktranslator.network.param.PersonalInfoParam;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BaseProfilePresenter extends BaseImageUploadPresenter {

    private BaseProfileView view;
    private PersonalInfo oldProfileInfo;

    public BaseProfilePresenter(Context context, BaseProfileView view) {
        super(context, view);
        this.view = view;
        oldProfileInfo = LocalSharedPreferences.getInstance(getContext()).getPersonalInfo();
    }

    public void deleteImage() {
        view.showProgress();
        requestAPI(getAPI().deleteAvatar(), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(response.getPersonalInfo());
                view.onDeleteAvatar(response.getPersonalInfo());
                view.hideProgress();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void updateProfileInfo() {
        view.showProgress();
        if (view.isNeedUpdateProfileImage()) {
            updateImageToServer(view.getBytes(), view.getFileName(), false);
        } else {
            updateProfileInfoOnly();
        }
    }

    public void updateProfileInfoOnly() {
        requestAPI(getAPI().updateProfileInfo(view.createUserInfoParam()), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                if (!oldProfileInfo.getEmail().equals(response.getPersonalInfo().getEmail())) {
                    updateFirebaseEmail(response.getPersonalInfo());
                } else {
                    handleAfterUpdateProfile(response.getPersonalInfo());
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                if (errCode == APIConstant.WAIT_PREVIOUS_REQUEST_APPROVE) {
                    view.updateProfileInvalid();
                } else {
                    showNetworkError(view, errCode, errMessage);
                }
            }
        });
    }

    private void updateFirebaseEmail(PersonalInfo personalInfo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(personalInfo.getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleAfterUpdateProfile(personalInfo);
                    } else {
                        view.hideProgress();
                        Log.e("Firebase Error: ", task.getException().getMessage());
                    }
                });
    }

    private void handleAfterUpdateProfile(PersonalInfo personalInfo) {
        if (view.isNeedUploadMoreCertificates()) {
            loadCertificates();
        } else {
            updateProfileFinished(personalInfo, false);
        }
    }

    public void updateImageToServer(byte[] bytes, String fileName, boolean isUploadAvatarOnly) {
        if (isUploadAvatarOnly) {
            view.showProgress();
        }
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM,
                bytes);

        MultipartBody.Part multiPart = MultipartBody.Part.createFormData("avatar", fileName, requestFile);

        requestAPI(getAPI().updateProfileImage(multiPart), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                if (isUploadAvatarOnly) {
                    updateProfileFinished(response.getPersonalInfo(), isUploadAvatarOnly);
                } else {
                    updateProfileInfoOnly();
                }
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void uploadCertificates(List<MultipartBody.Part> parts) {
        requestAPI(getAPI().uploadCertificates(parts), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                updateProfileFinished(response.getPersonalInfo(), false);
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    private void updateProfileFinished(PersonalInfo personalInfo, boolean isUpdateAvatarOnly) {
        view.updateSuccessful(personalInfo, isUpdateAvatarOnly);
        LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(personalInfo);
        LocalSharedPreferences.getInstance(getContext()).removeData(Constants.KEY_USER_INFO_CACHED);
        view.hideProgress();
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

    public interface BaseProfileView extends BaseImageUploadView {

        void onDeleteAvatar(PersonalInfo personalInfo);

        PersonalInfoParam createUserInfoParam();

        boolean isNeedUpdateProfileImage();

        String getFileName();

        byte[] getBytes();

        void updateSuccessful(PersonalInfo personalInfo, boolean isUpdateAvatarOnly);

        List<Image> getImages();

        boolean isNeedUploadMoreCertificates();

        void updateProfileInvalid();
    }
}
