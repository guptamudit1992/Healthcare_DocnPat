package com.careons.app.Patient.Database.Models.Healthbook;

import com.orm.SugarRecord;

public class Gynaecology extends SugarRecord {

    private String patientId;
    private String pId;
    private String problemId;
    private String problemName;
    private String hyperlink;
    private String startDate;
    private boolean isStillSuffering;
    private String comments;
    private boolean isUpdate;



    public Gynaecology() {
        // Default Constructor
    }


    public Gynaecology(String patientId, String pId, String problemId,
                       String problemName, String hyperlink,
                       String startDate, boolean isStillSuffering,
                       String comments, boolean isUpdate) {

        this.patientId = patientId;
        this.pId = pId;
        this.problemId = problemId;
        this.problemName = problemName;
        this.hyperlink = hyperlink;
        this.startDate = startDate;
        this.isStillSuffering = isStillSuffering;
        this.comments = comments;
        this.isUpdate = isUpdate;
    }



    /**
     * Getters and Setters
     */

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getpId() {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isStillSuffering() {
        return isStillSuffering;
    }

    public void setStillSuffering(boolean stillSuffering) {
        isStillSuffering = stillSuffering;
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

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
