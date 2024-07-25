package com.example.translateconnector.asynctask;

import android.os.AsyncTask;

import com.imoktranslator.utils.ImagesManager;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LoadAvatarTask extends AsyncTask<File, Void, MultipartBody.Part> {

    @Override
    protected MultipartBody.Part doInBackground(File... files) {
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM,
                ImagesManager.getStreamByteFromImage(files[0]));

        // MultipartBody.Part is used to send also the actual file name

        return MultipartBody.Part.createFormData("avatar", files[0].getName(), requestFile);
    }
}
