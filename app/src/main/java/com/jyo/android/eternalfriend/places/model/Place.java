package com.jyo.android.eternalfriend.places.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JohnTangarife on 1/08/16.
 */
public class Place implements Parcelable{

    @SerializedName("place_id")
    private String placeId;
    private String name;
    private Geometry geometry;
    @SerializedName("opening_hours")
    private OpeningHours openingHours;
    private List<String> types;

    protected Place(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        geometry = in.readParcelable(Geometry.class.getClassLoader());
        openingHours = in.readParcelable(OpeningHours.class.getClassLoader());
        types = in.createStringArrayList();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeId);
        parcel.writeString(name);
        parcel.writeParcelable(geometry, i);
        parcel.writeParcelable(openingHours, i);
        parcel.writeStringList(types);
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
