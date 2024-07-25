package com.example.translateconnector.utils;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultiPartRequestHelper {

    public static MultipartBody.Part prepareFilePart(String partName, String pathImage) {
        // use the FileUtils to get the actual file by uri
        File file = new File(pathImage);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}
