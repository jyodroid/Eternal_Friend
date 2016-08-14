package com.jyo.android.eternalfriend.clinical_history.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by JohnTangarife on 14/08/16.
 */
public class ClinicalHistory {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private int clinicalHistoryId;
    private int profileId;
    private String hospital;
    private String date;
    private String diagnostic;
    private String treatment;

    public int getClinicalHistoryId() {
        return clinicalHistoryId;
    }

    public void setClinicalHistoryId(int clinicalHistoryId) {
        this.clinicalHistoryId = clinicalHistoryId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
}
