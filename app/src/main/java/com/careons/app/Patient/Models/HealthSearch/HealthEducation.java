package com.careons.app.Patient.Models.HealthSearch;

public class HealthEducation {

    private String hsId;
    private String name;
    private String category;
    private String hyperlink;


    public HealthEducation() {
        // Default constructor
    }

    public HealthEducation(String hsId, String name, String category, String hyperlink) {
        this.hsId = hsId;
        this.name = name;
        this.category = category;
        this.hyperlink = hyperlink;
    }


    // Getter and Setter
    public String getHsId() {
        return hsId;
    }

    public String getName() {
        return name;
    }

    public void setHsId(String hsId) {
        this.hsId = hsId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }
}
