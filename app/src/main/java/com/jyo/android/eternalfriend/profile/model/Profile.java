package com.jyo.android.eternalfriend.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by johntangarife on 8/5/16.
 */
public class Profile implements Parcelable{

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private int profileId;
    private String name;
    private String birthDate;
    private String breed;
    private String picturePath;

    public Profile(){}
    protected Profile(Parcel in) {
        profileId = in.readInt();
        name = in.readString();
        birthDate = in.readString();
        breed = in.readString();
        picturePath = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(profileId);
        parcel.writeString(name);
        parcel.writeString(birthDate);
        parcel.writeString(breed);
        parcel.writeString(picturePath);
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate(){
        return birthDate;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getPicture() {
        return picturePath;
    }

    public void setPicture(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getAge() throws Exception {

        if (birthDate == null) {
            throw new Exception("No birth date set");
        }

        Date today = new Date(System.currentTimeMillis());
        Date dBirthDate = dateFormat.parse(birthDate);

        if (today.before(dBirthDate)) {
            throw new Exception("Wrong birth date set");
        }

        Calendar birthDateCal = Calendar.getInstance();
        birthDateCal.setTime(dBirthDate);
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(today);

        //Check if years are equal
        if (birthDateCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
            //Check if months are equal
            if (birthDateCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)) {
                int days = todayCal.get(Calendar.DAY_OF_MONTH) - birthDateCal.get(Calendar.DAY_OF_MONTH);
                return days + " days";
            } else {
                int months = todayCal.get(Calendar.MONTH) - birthDateCal.get(Calendar.MONTH);
                return months + " months";
            }
        } else {
            if (birthDateCal.get(Calendar.MONTH) > todayCal.get(Calendar.MONTH) &&
                    todayCal.get(Calendar.MONTH) - birthDateCal.get(Calendar.YEAR) == 1) {

                //Months from birth month to december
                int secondHalf = Calendar.DECEMBER - birthDateCal.get(Calendar.MONTH);

                //Months from moth to december plus months from december to today month.
                return secondHalf + todayCal.get(Calendar.MONTH) + " months";
            } else if(todayCal.get(Calendar.MONTH) >= birthDateCal.get(Calendar.MONTH)){
                //Just count years
                int years = todayCal.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR);
                return years + " years";
            }else {
                int years = todayCal.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR) - 1;
                return years + " years";
            }
        }
    }
}
