package com.careons.app.Patient.Enums;

public enum AlbumTag {

    Bills("Bills"),
    Medicines("Medicines"),
    LabReports("Lab Reports"),
    Prescription("Prescription"),
    Others("Others");


    private final String albumTag;

    AlbumTag(String s) {
        albumTag = s;
    }

    public String getAlbumTag() {
        return albumTag;
    }
}


