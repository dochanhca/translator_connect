package com.example.translateconnector.asynctask;

import android.os.AsyncTask;

import com.imoktranslator.utils.ImagesManager;

import java.io.File;

public class LoadBitmapTask extends AsyncTask<File, Void, byte[]> {
    @Override
    protected byte[] doInBackground(File... files) {

        return ImagesManager.getStreamByteFromImage(files[0]);
    }
}
