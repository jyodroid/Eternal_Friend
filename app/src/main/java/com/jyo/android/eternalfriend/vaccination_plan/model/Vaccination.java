package com.jyo.android.eternalfriend.vaccination_plan.model;

/**
 * Created by JohnTangarife on 14/08/16.
 */
public class Vaccination {

    private int vaccinationId;
    private int profileId;
    private String date;
    private String name;
    private int status;

    public int getVaccinationId() {
        return vaccinationId;
    }

    public void setVaccinationId(int vaccinationId) {
        this.vaccinationId = vaccinationId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
