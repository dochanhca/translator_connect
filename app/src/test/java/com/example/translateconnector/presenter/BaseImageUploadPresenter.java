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

import java.io.File;
import java.util.List;

public class BaseImageUploadPresenter extends BasePresenter {

    private BaseImageUploadView baseImageUploadView;

    public BaseImageUploadPresenter(Context context, BaseImageUploadView baseImageUploadView) {
        super(context);
        this.baseImageUploadView = baseImageUploadView;
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
                            baseImageUploadView.openCamera();
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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        baseImageUploadView.openGallery();
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
                baseImageUploadView.showProgress();
            }

            @Override
            protected void onPostExecute(byte[] bytes) {
                super.onPostExecute(bytes);
                baseImageUploadView.onLoadBitmap(bytes);
                baseImageUploadView.hideProgress();
            }
        };
        loadBitmapTask.execute(file);
    }

    public interface BaseImageUploadView extends BaseView {
        void openCamera();

        void openGallery();

        void onLoadBitmap(byte[] bytes);
    }
}
