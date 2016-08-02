package com.jyo.android.eternalfriend.places.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 2/08/16.
 */
public class Location implements Parcelable{
    private double lat;
    private double lng;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
