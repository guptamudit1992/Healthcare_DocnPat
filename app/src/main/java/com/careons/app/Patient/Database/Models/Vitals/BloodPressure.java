package com.careons.app.Patient.Database.Models.Vitals;

import com.orm.SugarRecord;

public class BloodPressure extends SugarRecord implements Comparable {

    private String patientId;
    private String bpId;
    private String systolicBp;
    private String diastolicBp;
    private String tag;
    private String date;
    private String time;
    private long timestamp;


    public BloodPressure() {
        // Default Constructor
    }

    public BloodPressure(String patientId, String bpId, String systolic, String diastolic,
                         String tag, String date, String time, long timestamp) {

        this.patientId = patientId;
        this.bpId = bpId;
        this.systolicBp = systolic;
        this.diastolicBp = diastolic;
        this.tag = tag;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
    }


    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getBpId() {
        return bpId;
    }

    public void setBpId(String bpId) {
        this.bpId = bpId;
    }

    public String getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(String systolicBp) {
        this.systolicBp = systolicBp;
    }

    public String getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(String diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

        long timestampCheck  = ((BloodPressure) another).getTimestamp();

        if(timestampCheck > timestamp) {
            return 1;
        } if(timestampCheck == timestamp) {
            return 0;
        } else {
            return -1;
        }
    }
}
