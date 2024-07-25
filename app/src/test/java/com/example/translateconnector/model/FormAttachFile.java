package com.example.translateconnector.model;

import com.darsh.multipleimageselect.models.Image;

import java.util.List;

public class FormAttachFile {
    private List<Image> paths;

    public FormAttachFile(List<Image> paths) {
        this.paths = paths;
    }

    public List<Image> getPaths() {
        return paths;
    }
}
