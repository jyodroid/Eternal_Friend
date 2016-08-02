package com.jyo.android.eternalfriend.places.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 1/08/16.
 */
public class Place implements Parcelable{
    private String place_id;
    private String name;
    private Geometry geometry;
    private String icon;
    private boolean open_now;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
