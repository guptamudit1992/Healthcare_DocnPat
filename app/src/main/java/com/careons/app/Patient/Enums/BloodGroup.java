package com.careons.app.Patient.Enums;

public enum BloodGroup {

    APositive("A Positive"),
    ANegative("A Negative"),
    BPositive("B Positive"),
    BNegative("B Negative"),
    ABPositive("AB Positive"),
    ABNegative("AB Negative"),
    OPositive("O Positive"),
    ONegative("O Negative");


    private final String bloodGroup;

    BloodGroup(String s) {
        bloodGroup = s;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }
}
