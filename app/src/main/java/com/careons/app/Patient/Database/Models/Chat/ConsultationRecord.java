package com.careons.app.Patient.Database.Models.Chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ConsultationRecord implements Parcelable {

    private String name;
    private String age;
    private String gender;
    private ArrayList<String> symptoms;
    private String description;

    // Constructor
    public ConsultationRecord(String name, String age, String gender, ArrayList<String> symptoms, String description) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.symptoms = symptoms;
        this.description = description;
    }


    /**
     * Getter and Setter
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.age);
        dest.writeString(this.gender);
        dest.writeStringList(this.symptoms);
        dest.writeString(this.description);
    }

    protected ConsultationRecord(Parcel in) {
        this.name = in.readString();
        this.age = in.readString();
        this.gender = in.readString();
        this.symptoms = in.createStringArrayList();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<ConsultationRecord> CREATOR = new Parcelable.Creator<ConsultationRecord>() {
        @Override
        public ConsultationRecord createFromParcel(Parcel source) {
            return new ConsultationRecord(source);
        }

        @Override
        public ConsultationRecord[] newArray(int size) {
            return new ConsultationRecord[size];
        }
    };
}
