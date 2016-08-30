
package com.careons.app.Patient.Database.Models.Vitals;

import com.orm.SugarRecord;

public class BMI extends SugarRecord implements Comparable {

    private String patientId;
    private String bmiId;
    private String bmi;
    private String tag;
    private String heightFt;
    private String heightInc;
    private String weight;
    private String date;
    private long timestamp;


    public BMI() {
        // Default Constructor
    }

    public BMI(String patientId, String bmiId, String bmi, String tag,
               String heightFt, String heightInc, String weight, String date, long timestamp) {

        this.patientId = patientId;
        this.bmiId = bmiId;
        this.bmi = bmi;
        this.tag = tag;
        this.heightFt = heightFt;
        this.heightInc = heightInc;
        this.weight = weight;
        this.date = date;
        this.timestamp = timestamp;
    }


    // Getters and Setters
    public String getBmiId() {
        return bmiId;
    }

    public void setBmiId(String bmiId) {
        this.bmiId = bmiId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getBmi() {
        return String.valueOf(bmi);
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getHeightFt() {
        return heightFt;
    }

    public void setHeightFt(String heightFt) {
        this.heightFt = heightFt;
    }

    public String getHeightInc() {
        return heightInc;
    }

    public void setHeightInc(String heightInc) {
        this.heightInc = heightInc;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }




    @Override
    public int compareTo(Object another) {

        long timestampCheck  = ((BMI) another).getTimestamp();

        if(timestampCheck > timestamp) {
            return 1;
        } if(timestampCheck == timestamp) {
            return 0;
        } else {
            return -1;
        }
    }
}