package com.example.translateconnector.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class FileModel implements Parcelable {
    private String type;
    private String urlFile;
    private String nameFile;
    private String sizeFile;

    public FileModel() {
    }

    public FileModel(String type, String url_file, String nameFile, String sizeFile) {
        this.type = type;
        this.urlFile = url_file;
        this.nameFile = nameFile;
        this.sizeFile = sizeFile;
    }

    public FileModel(String urlFile) {
        this.urlFile = urlFile;
    }

    protected FileModel(Parcel in) {
        type = in.readString();
        urlFile = in.readString();
        nameFile = in.readString();
        sizeFile = in.readString();
    }

    public static final Creator<FileModel> CREATOR = new Creator<FileModel>() {
        @Override
        public FileModel createFromParcel(Parcel in) {
            return new FileModel(in);
        }

        @Override
        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(String sizeFile) {
        this.sizeFile = sizeFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(urlFile);
        dest.writeString(nameFile);
        dest.writeString(sizeFile);
    }
}
