package com.example.translateconnector.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.imoktranslator.asynctask.LoadBitmapTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.imoktranslator.network.request.BaseRequest;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.utils.LocalSharedPreferences;

import java.io.File;
import java.util.List;

public class BasicInfoPresenter extends BasePresenter {
    private BasicInfoView view;

    public BasicInfoPresenter(Context context, BasicInfoView view) {
        super(context);
        this.view = view;
    }

    public void deleteImage() {
        view.showProgress();
        requestAPI(getAPI().deleteAvatar(), new BaseRequest<PersonalInfoResponse>() {
            @Override
            public void onSuccess(PersonalInfoResponse response) {
                view.hideProgress();
                LocalSharedPreferences.getInstance(getContext()).savePersonalInfo(response.getPersonalInfo());
                view.onAvatarDeleted();
            }

            @Override
            public void onFailure(int errCode, String errMessage) {
                view.hideProgress();
                showNetworkError(view, errCode, errMessage);
            }
        });
    }

    public void requestCameraPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            view.openCamera();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void requestReadSDCardPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        view.openGallery();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    public void loadBitmapFormFile(File file) {
        LoadBitmapTask loadBitmapTask = new LoadBitmapTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                view.showProgress();
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                view.onLoadBitmap(bytes);
                view.hideProgress();
            }
        };
        loadBitmapTask.execute(file);
    }

    public interface BasicInfoView extends BaseView {

        void onAvatarDeleted();

        void openCamera();

        void openGallery();

        void onLoadBitmap(byte[] bytes);
    }
}
