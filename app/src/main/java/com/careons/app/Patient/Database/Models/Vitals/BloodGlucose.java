package com.careons.app.Patient.Database.Models.Vitals;

import com.orm.SugarRecord;

public class BloodGlucose extends SugarRecord implements Comparable {

    private String patientId;
    private String bgId;
    private String bloodGlucose;
    private String checkup;
    private String tag;
    private String date;
    private String time;
    private long timestamp;


    public BloodGlucose() {
        // Default Constructor
    }

    // Parametrized Constructor
    public BloodGlucose(String patientId, String bgId, String bloodGlucose,
                        String checkup, String tag, String date, String time, long timestamp) {

        this.patientId = patientId;
        this.bgId = bgId;
        this.bloodGlucose = bloodGlucose;
        this.checkup = checkup;
        this.tag = tag;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
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

    public String getBgId() {
        return bgId;
    }

    public void setBgId(String bgId) {
        this.bgId = bgId;
    }

    public String getBloodGlucose() {
        return bloodGlucose;
    }

    public void setBloodGlucose(String bloodGlucose) {
        this.bloodGlucose = bloodGlucose;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheckup() {
        return checkup;
    }

    public void setCheckup(String checkup) {
        this.checkup = checkup;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }





    @Override
    public int compareTo(Object another) {

        long timestampCheck = ((BloodGlucose) another).getTimestamp();

        if(timestampCheck > timestamp) {
            return 1;
        } if(timestampCheck == timestamp) {
            return 0;
        } else {
            return -1;
        }
    }
}
