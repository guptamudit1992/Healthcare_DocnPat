package com.careons.app.Patient.Database.Models.Healthbook;

import com.orm.SugarRecord;

public class FamilyHistory extends SugarRecord {

    private String patientId;
    private String pId;
    private String problemId;
    private String problemName;
    private String hyperlink;

    private boolean isFatherStillSuffering;
    private String fatherAge;
    private boolean isMotherStillSuffering;
    private String motherAge;
    private boolean isBrotherStillSuffering;
    private String brotherAge;
    private boolean isSisterStillSuffering;
    private String sisterAge;
    private boolean isGrandFatherStillSuffering;
    private String grandFatherAge;
    private boolean isGrandMotherStillSuffering;
    private String grandMotherAge;

    private String comments;
    private boolean isUpdate = false;


    public FamilyHistory() {
        // Default Constructor
    }


    public FamilyHistory(String patientId, String pId, String problemId, String problemName, String hyperlink,
                         boolean isFatherStillSuffering, String fatherAge,
                         boolean isMotherStillSuffering, String motherAge,
                         boolean isBrotherStillSuffering, String brotherAge,
                         boolean isSisterStillSuffering, String sisterAge,
                         boolean isGrandFatherStillSuffering, String grandFatherAge,
                         boolean isGrandMotherStillSuffering, String grandMotherAge, String comments, boolean isUpdate) {

        this.pId = pId;this.patientId = patientId;
        this.pId = pId;
        this.problemId = problemId;
        this.problemName = problemName;
        this.hyperlink = hyperlink;

        this.isFatherStillSuffering = isFatherStillSuffering;
        this.fatherAge = fatherAge;

        this.isMotherStillSuffering = isMotherStillSuffering;
        this.motherAge = motherAge;

        this.isBrotherStillSuffering = isBrotherStillSuffering;
        this.brotherAge = brotherAge;

        this.isSisterStillSuffering = isSisterStillSuffering;
        this.sisterAge = sisterAge;

        this.isGrandFatherStillSuffering = isGrandFatherStillSuffering;
        this.grandFatherAge = grandFatherAge;

        this.isGrandMotherStillSuffering = isGrandMotherStillSuffering;
        this.grandMotherAge = grandMotherAge;

        this.comments = comments;
        this.isUpdate = isUpdate;
    }

    // Getters and Setters

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public boolean isFatherStillSuffering() {
        return isFatherStillSuffering;
    }

    public void setIsFatherStillSuffering(boolean isFatherStillSuffering) {
        this.isFatherStillSuffering = isFatherStillSuffering;
    }

    public String getFatherAge() {
        return fatherAge;
    }

    public void setFatherAge(String fatherAge) {
        this.fatherAge = fatherAge;
    }

    public boolean isMotherStillSuffering() {
        return isMotherStillSuffering;
    }

    public void setIsMotherStillSuffering(boolean isMotherStillSuffering) {
        this.isMotherStillSuffering = isMotherStillSuffering;
    }

    public String getMotherAge() {
        return motherAge;
    }

    public void setMotherAge(String motherAge) {
        this.motherAge = motherAge;
    }

    public boolean isBrotherStillSuffering() {
        return isBrotherStillSuffering;
    }

    public void setIsBrotherStillSuffering(boolean isBrotherStillSuffering) {
        this.isBrotherStillSuffering = isBrotherStillSuffering;
    }

    public String getBrotherAge() {
        return brotherAge;
    }

    public void setBrotherAge(String brotherAge) {
        this.brotherAge = brotherAge;
    }

    public boolean isSisterStillSuffering() {
        return isSisterStillSuffering;
    }

    public void setIsSisterStillSuffering(boolean isSisterStillSuffering) {
        this.isSisterStillSuffering = isSisterStillSuffering;
    }

    public String getSisterAge() {
        return sisterAge;
    }

    public void setSisterAge(String sisterAge) {
        this.sisterAge = sisterAge;
    }

    public boolean isGrandFatherStillSuffering() {
        return isGrandFatherStillSuffering;
    }

    public void setIsGrandFatherStillSuffering(boolean isGrandFatherStillSuffering) {
        this.isGrandFatherStillSuffering = isGrandFatherStillSuffering;
    }

    public String getGrandFatherAge() {
        return grandFatherAge;
    }

    public void setGrandFatherAge(String grandFatherAge) {
        this.grandFatherAge = grandFatherAge;
    }

    public boolean isGrandMotherStillSuffering() {
        return isGrandMotherStillSuffering;
    }

    public void setIsGrandMotherStillSuffering(boolean isGrandMotherStillSuffering) {
        this.isGrandMotherStillSuffering = isGrandMotherStillSuffering;
    }

    public String getGrandMotherAge() {
        return grandMotherAge;
    }

    public void setGrandMotherAge(String grandMotherAge) {
        this.grandMotherAge = grandMotherAge;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

}
