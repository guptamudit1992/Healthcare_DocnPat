package com.careons.app.Patient.Database.Models.Healthbook;

import com.orm.SugarRecord;

public class Pregnancy extends SugarRecord {

    private String patientId;
    private String pId;
    private String ageOfMenses;
    private String dateOfLastMensesCycle;
    private boolean haveYouPregnant;
    private String noOfFullTermPregnant;
    private String noOfPreTermPregnant;
    private String noOfAbortions;
    private String noOfLivingChildren;



    public Pregnancy() {
        // Default Constructor
    }


    public Pregnancy(String patientId, String pId, String ageOfMenses, String dateOfLastMensesCycle,
                     boolean haveYouPregnant, String noOfFullTermPregnant, String noOfPreTermPregnant,
                     String noOfAbortions, String noOfLivingChildren) {

        this.patientId = patientId;
        this.pId = pId;
        this.ageOfMenses = ageOfMenses;
        this.dateOfLastMensesCycle = dateOfLastMensesCycle;
        this.haveYouPregnant = haveYouPregnant;
        this.noOfFullTermPregnant = noOfFullTermPregnant;
        this.noOfPreTermPregnant = noOfPreTermPregnant;
        this.noOfAbortions = noOfAbortions;
        this.noOfLivingChildren = noOfLivingChildren;
    }




    // Getters and Setters

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getAgeOfMenses() {
        return ageOfMenses;
    }

    public void setAgeOfMenses(String ageOfMenses) {
        this.ageOfMenses = ageOfMenses;
    }

    public String getDateOfLastMensesCycle() {
        return dateOfLastMensesCycle;
    }

    public void setDateOfLastMensesCycle(String dateOfLastMensesCycle) {
        this.dateOfLastMensesCycle = dateOfLastMensesCycle;
    }

    public boolean isHaveYouPregnant() {
        return haveYouPregnant;
    }

    public void setHaveYouPregnant(boolean haveYouPregnant) {
        this.haveYouPregnant = haveYouPregnant;
    }

    public String getNoOfFullTermPregnant() {
        return noOfFullTermPregnant;
    }

    public void setNoOfFullTermPregnant(String noOfFullTermPregnant) {
        this.noOfFullTermPregnant = noOfFullTermPregnant;
    }

    public String getNoOfPreTermPregnant() {
        return noOfPreTermPregnant;
    }

    public void setNoOfPreTermPregnant(String noOfPreTermPregnant) {
        this.noOfPreTermPregnant = noOfPreTermPregnant;
    }

    public String getNoOfAbortions() {
        return noOfAbortions;
    }

    public void setNoOfAbortions(String noOfAbortions) {
        this.noOfAbortions = noOfAbortions;
    }

    public String getNoOfLivingChildren() {
        return noOfLivingChildren;
    }

    public void setNoOfLivingChildren(String noOfLivingChildren) {
        this.noOfLivingChildren = noOfLivingChildren;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }



}
