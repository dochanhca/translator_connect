package com.example.translateconnector.asynctask;

import android.os.AsyncTask;

import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.utils.ImagesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LoadCertificateTask extends AsyncTask<List<Image>, Void, List<MultipartBody.Part>> {
    @Override
    protected List<MultipartBody.Part> doInBackground(List<Image>... lists) {

        List<Image> images = lists[0];
        List<MultipartBody.Part> parts = new ArrayList<>();

        // create RequestBody instance from file
        for (Image image : images) {
            File file = new File(image.path);

            RequestBody requestFile = RequestBody.create(MultipartBody.FORM,
                    ImagesManager.getCertificateStreams(file));
            parts.add(MultipartBody.Part.createFormData("file[]", file.getName(), requestFile));
        }

        // MultipartBody.Part is used to send also the actual file name
        return parts;
    }
}
