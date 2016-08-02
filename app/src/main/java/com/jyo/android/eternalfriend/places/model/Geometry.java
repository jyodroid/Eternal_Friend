package com.jyo.android.eternalfriend.places.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 2/08/16.
 */
public class Geometry implements Parcelable{
    private Location location;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
