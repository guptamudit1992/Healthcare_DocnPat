package com.careons.app.Patient.Database.Models.Healthbook;

import com.orm.SugarRecord;

public class MedicalProblem extends SugarRecord {

    private String patientId;
    private String pId;
    private String problemId;
    private String problemName;
    private String hyperlink;
    private boolean isStillSuffering;
    private String duration;
    private String startDate;
    private String endDate;
    private String comments;
    private boolean isUpdate = false;


    public MedicalProblem() {
        // Default Constructor
    }


    public MedicalProblem(String patientId, String pId, String problemId,
                          String problemName, String hyperlink, boolean isStillSuffering, String duration,
                          String startDate, String endDate, String comments,
                          boolean isUpdate) {

        this.patientId = patientId;
        this.pId = pId;
        this.problemId = problemId;
        this.problemName = problemName;
        this.hyperlink = hyperlink;
        this.isStillSuffering = isStillSuffering;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comments = comments;
        this.isUpdate = isUpdate;
    }




    /**
     *  Getters and Setters
     */

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isStillSuffering() {
        return isStillSuffering;
    }

    public void setIsStillSuffering(boolean isStillSuffering) {
        this.isStillSuffering = isStillSuffering;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getpId() {
        return pId;
    }

    public void setStillSuffering(boolean stillSuffering) {
        isStillSuffering = stillSuffering;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

}
