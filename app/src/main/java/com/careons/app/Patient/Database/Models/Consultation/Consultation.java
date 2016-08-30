package com.careons.app.Patient.Database.Models.Consultation;

public class Consultation {

    private String name;
    private boolean isSelected = false;


    // Constructor
    public Consultation(String name) {
        this.name = name;
    }


    /**
     *  Getter and Setter
     */
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
