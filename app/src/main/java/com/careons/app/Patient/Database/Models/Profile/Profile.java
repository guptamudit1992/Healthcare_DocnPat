package com.careons.app.Patient.Database.Models.Profile;

import com.orm.SugarRecord;

public class Profile extends SugarRecord {

    private String patientId;
    private String username;
    private String email;
    private String phone;
    private String dob;
    private String age;
    private String gender;
    private String bloodGroup;

    private boolean isAccountCreated;
    private boolean isOnboardingComplete;


    public Profile() {
        // Default Constructor
    }

    // Parameterized Constructor
    public Profile(String patientId, String username, String email,
                   String phone, String dob, String age,
                   String gender, String bloodGroup,
                   boolean isAccountCreated, boolean isOnboardingComplete) {

        this.patientId = patientId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.isAccountCreated = isAccountCreated;
        this.isOnboardingComplete = isOnboardingComplete;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public boolean isOnboardingComplete() {
        return isOnboardingComplete;
    }

    public void setOnboardingComplete(boolean onboardingComplete) {
        isOnboardingComplete = onboardingComplete;
    }

    public boolean isAccountCreated() {
        return isAccountCreated;
    }

    public void setAccountCreated(boolean accountCreated) {
        isAccountCreated = accountCreated;
    }
}
