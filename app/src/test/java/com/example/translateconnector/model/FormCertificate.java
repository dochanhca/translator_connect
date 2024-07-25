package com.example.translateconnector.model;

import com.darsh.multipleimageselect.models.Image;

import java.util.List;

public class FormCertificate {
    private String university;
    private int graduationType;
    private String graduationYear;
    private List<String> certificateList;
    private List<Image> attachPaths;
    private int experience;
    private String otherInfo;

    public FormCertificate(String university, int graduationType, String graduationYear, List<String> certificateList, List<Image> attachPaths, int experience, String otherInfo) {
        this.university = university;
        this.graduationType = graduationType;
        this.graduationYear = graduationYear;
        this.certificateList = certificateList;
        this.attachPaths = attachPaths;
        this.experience = experience;
        this.otherInfo = otherInfo;
    }


    public String getUniversity() {
        return university;
    }

    public int getGraduationType() {
        return graduationType;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public List<String> getCertificateList() {
        return certificateList;
    }

    public List<Image> getAttachPaths() {
        return attachPaths;
    }

    public int getExperience() {
        return experience;
    }

    public String getOtherInfo() {
        return otherInfo;
    }
}
