package com.example.translateconnector.utils;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.imoktranslator.model.firebase.FileModel;

import java.io.File;

public class FirebaseStorageUtils {
    private static final String SOCIAL_STORAGE = "social_storage";
    private static FirebaseStorageUtils sInstance;

    private FirebaseStorageUtils() {

    }

    public static FirebaseStorageUtils getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseStorageUtils();
        }
        return sInstance;
    }

    public void sendFileByUri(Uri file, String fileName, String fileType, OnUploadFileListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileStorageRef = storage.getReference()
                .child(SOCIAL_STORAGE)
                .child(fileName);

        UploadTask uploadTask = fileStorageRef.putFile(file);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                listener.onFail(task.getException());
            }
            // Continue with the task to get the download URL
            return fileStorageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            Uri downloadUrl = task.getResult();
            File f = new File(downloadUrl.getPath());
            long size = f.length();
            FileModel fileModel = new FileModel(fileType, downloadUrl.toString(), fileName, size + "");
            listener.onSuccess(fileModel);
        });
    }

    public void sendImageByBytes(byte[] bytes, File file, OnUploadFileListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileStorageRef = storage.getReference()
                .child(SOCIAL_STORAGE).child(file.getName());

        fileStorageRef.putBytes(bytes).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                listener.onFail(task.getException());
            }
            // Continue with the task to get the download URL
            return fileStorageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                FileModel fileModel = new FileModel(FireBaseDataUtils.TYPE_IMAGE,
                        downloadUri.toString(), file.getName(),
                        file.length() + "");
                listener.onSuccess(fileModel);
            } else {
                // Handle failures
                listener.onFail(task.getException());
            }
        });
    }

    public void deleteFile(FileModel fileModel) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference fileStorageRef = storage.getReference()
                .child(SOCIAL_STORAGE).child(fileModel.getNameFile());
        fileStorageRef.delete();
    }

    public interface OnUploadFileListener {
        void onSuccess(FileModel fileModel);

        void onFail(Exception e);
    }
}
