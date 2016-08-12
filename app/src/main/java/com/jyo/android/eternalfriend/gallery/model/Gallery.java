package com.jyo.android.eternalfriend.gallery.model;

import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by johntangarife on 8/12/16.
 */
public class Gallery {
    private int galleryId;
    private int profileId;
    private String imagePath;
    private int rangeAge;

    public int getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(int galleryId) {
        this.galleryId = galleryId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getRangeAge() {
        return rangeAge;
    }

    public void setRangeAge(String birthDate) throws Exception {

        if (birthDate == null) {
            throw new Exception("No birth date set");
        }

        Date today = new Date(System.currentTimeMillis());
        Date dBirthDate = Profile.dateFormat.parse(birthDate);

        if (today.before(dBirthDate)) {
            throw new Exception("Wrong birth date set");
        }

        Calendar birthDateCal = Calendar.getInstance();
        birthDateCal.setTime(dBirthDate);
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(today);

        //Check if years are equal
        if (birthDateCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
            this.rangeAge = 1;
        } else {
            if (birthDateCal.get(Calendar.MONTH) > todayCal.get(Calendar.MONTH) &&
                    todayCal.get(Calendar.MONTH) - birthDateCal.get(Calendar.YEAR) == 1) {
                this.rangeAge = 1;
            } else if (todayCal.get(Calendar.MONTH) >= birthDateCal.get(Calendar.YEAR)) {
                //Just count years
                int years = todayCal.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR);
                this.rangeAge = years;
            } else {
                int years = todayCal.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR) - 1;
                this.rangeAge = years;
            }
        }

    }
}
