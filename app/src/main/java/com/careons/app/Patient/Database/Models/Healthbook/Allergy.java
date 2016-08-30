package com.careons.app.Patient.Database.Models.Healthbook;

import com.orm.SugarRecord;

public class Allergy extends SugarRecord {

    private String patientId;
    private String pId;
    private String allergyId;
    private String allergyName;
    private String hyperlink;
    private String type;
    private String reaction;
    private String startDate;
    private String comment;


    public Allergy() {
        // Default Constructor
    }


    public Allergy(String patientId, String pId, String allergyId, String allergyName, String hyperlink,
                   String type, String reaction, String startDate, String comment) {

        this.patientId = patientId;
        this.pId = pId;
        this.allergyId = allergyId;
        this.allergyName = allergyName;
        this.hyperlink = hyperlink;
        this.type = type;
        this.reaction = reaction;
        this.startDate = startDate;
        this.comment = comment;
    }


    // Getters and Setters
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

    public String getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(String allergyId) {
        this.allergyId = allergyId;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
