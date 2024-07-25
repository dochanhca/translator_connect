package com.example.translateconnector.asynctask;

import android.os.AsyncTask;

import com.darsh.multipleimageselect.models.Image;
import com.imoktranslator.model.ResultImage;
import com.imoktranslator.utils.ImagesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadMultiImagesTask extends AsyncTask<List<Image>, Void, List<ResultImage>> {

    @Override
    protected List<ResultImage> doInBackground(List<Image>... lists) {
        List<Image> images = lists[0];
        List<ResultImage> listBytes = new ArrayList<>();

        // create RequestBody instance from file
        for (Image image : images) {
            File file = new File(image.path);

            byte[] bytes = ImagesManager.getStreamByteFromImage(file);
            if (bytes != null) {
                listBytes.add(new ResultImage(file, bytes));
            } else {
                listBytes.add(null);
            }
        }

        // MultipartBody.Part is used to send also the actual file name
        return listBytes;
    }
}
