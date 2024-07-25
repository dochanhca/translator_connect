package com.example.translateconnector.model;

import java.io.File;

public class ResultImage {

    private File file;
    private byte[] bytes;

    public ResultImage(File file, byte[] bytes) {
        this.file = file;
        this.bytes = bytes;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
