package com.jyo.android.eternalfriend.profile_summarize.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by johntangarife on 8/5/16.
 */
public class Profile implements Parcelable{
    private String name;
    private Date birth_date;
    private String breed;
    private int age;
    private Date dayAgeSetted;

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

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
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


    public String getAge(){
        return "two years";
    }
}
