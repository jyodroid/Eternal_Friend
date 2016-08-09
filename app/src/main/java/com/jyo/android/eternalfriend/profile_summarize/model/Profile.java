package com.jyo.android.eternalfriend.profile_summarize.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by johntangarife on 8/5/16.
 */
public class Profile implements Parcelable{
    private String name;
    private Date birthDate;
    private String breed;
    private int age;

    private Bitmap picture;

    public Profile(){}
    protected Profile(Parcel in) {
        name = in.readString();
        breed = in.readString();
        age = in.readInt();
        picture = in.readParcelable(Bitmap.class.getClassLoader());
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
        parcel.writeString(name);
        parcel.writeString(breed);
        parcel.writeInt(age);
        parcel.writeParcelable(picture, i);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }


    public String getAge() throws Exception {

//        if (birthDate == null) {
//            throw new Exception("No birth date set");
//        }
//
//        Date today = new Date(System.currentTimeMillis());
//        if (today.after(birthDate)) {
//            throw new Exception("Wrong birth date set");
//        }
//
//        Calendar birthDateCal = Calendar.getInstance();
//        birthDateCal.setTime(birthDate);
//        Calendar todayCal = Calendar.getInstance();
//        todayCal.setTime(today);
//
//        //Check if years are equal
//        if (birthDateCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
//            //Check if months are equal
//            if (birthDateCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)) {
//                int days = todayCal.get(Calendar.DAY_OF_MONTH) - birthDateCal.get(Calendar.DAY_OF_MONTH);
//                return days + " days";
//            } else {
//                int months = todayCal.get(Calendar.MONTH) - birthDateCal.get(Calendar.MONTH);
//                return months + " months";
//            }
//        } else {
//            if (birthDateCal.get(Calendar.MONTH) > todayCal.get(Calendar.MONTH)) {
//                //Months from moth to december plus months from december to today month.
//                return "";
//            } else {
//                int years = todayCal.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR);
//                return years + " years";
//            }
//        }
        return "2 years";
    }
}
