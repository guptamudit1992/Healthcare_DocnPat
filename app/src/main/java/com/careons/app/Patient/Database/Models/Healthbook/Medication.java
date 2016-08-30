package com.careons.app.Patient.Database.Models.Healthbook;

import com.orm.SugarRecord;

public class Medication extends SugarRecord {

    private String patientId;
    private String pId;
    private String problemId;
    private String medicationName;
    private String hyperlink;
    private String sinceWhen;
    private String units;
    private String doses;
    private String frequency;
    private boolean continuingMedicine= false;
    private String duration;
    private String endDate;
    private String comment;



    public Medication() {
        // Default Constructor
    }


    public Medication(String patientId, String pId, String problemId, String medicationName, String hyperlink, String sinceWhen,
                      String units, String doses, String frequency,
                      boolean continuingMedication, String duration, String endDate, String comment) {

        this.patientId = patientId;
        this.pId = pId;
        this.problemId = problemId;
        this.medicationName = medicationName;
        this.hyperlink = hyperlink;
        this.sinceWhen = sinceWhen;
        this.units = units;
        this.doses = doses;
        this.frequency = frequency;
        this.continuingMedicine = continuingMedication;
        this.duration = duration;
        this.endDate = endDate;
        this.comment = comment;
    }



    // Getters and Setters

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public String getSinceWhen() {
        return sinceWhen;
    }

    public void setSinceWhen(String sinceWhen) {
        this.sinceWhen = sinceWhen;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDoses() {
        return doses;
    }

    public void setDoses(String doses) {
        this.doses = doses;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isContinuingMedication() {
        return continuingMedicine;
    }

    public void setContinuingMedication(boolean continuingMedication) {
        this.continuingMedicine = continuingMedication;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getComments() {
        return comment;
    }

    public void setComments(String comments) {
        this.comment = comments;
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
