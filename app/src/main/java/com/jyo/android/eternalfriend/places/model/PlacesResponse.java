package com.jyo.android.eternalfriend.places.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JohnTangarife on 8/08/16.
 */
public class PlacesResponse implements Parcelable{
    @SerializedName("results")
    private List<Place> places;

    protected PlacesResponse(Parcel in) {
        places = in.createTypedArrayList(Place.CREATOR);
    }

    public static final Creator<PlacesResponse> CREATOR = new Creator<PlacesResponse>() {
        @Override
        public PlacesResponse createFromParcel(Parcel in) {
            return new PlacesResponse(in);
        }

        @Override
        public PlacesResponse[] newArray(int size) {
            return new PlacesResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(places);
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
