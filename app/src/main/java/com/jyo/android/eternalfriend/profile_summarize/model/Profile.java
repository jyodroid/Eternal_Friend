package com.jyo.android.eternalfriend.profile_summarize.model;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by johntangarife on 8/5/16.
 */
public class Profile {
    private String name;
    private Date birth_date;
    private String breed;
    private int age;
    private Date dayAgeSetted;

    private Bitmap picture;

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

    //TODO: calculate age based on birth date in case age is null
    public String getAge(){
        return "2 years";
    }

    public void setAge(int age) {
        this.age = age;
        Calendar calendar = Calendar.getInstance();
        this.dayAgeSetted = calendar.getTime();
    }

    //    https://www.petfinder.com/developers/api-docs
}
