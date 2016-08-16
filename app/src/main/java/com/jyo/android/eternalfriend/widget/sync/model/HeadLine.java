package com.jyo.android.eternalfriend.widget.sync.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class HeadLine implements Parcelable {

    private String main;

    protected HeadLine(Parcel in) {
        main = in.readString();
    }

    public static final Creator<HeadLine> CREATOR = new Creator<HeadLine>() {
        @Override
        public HeadLine createFromParcel(Parcel in) {
            return new HeadLine(in);
        }

        @Override
        public HeadLine[] newArray(int size) {
            return new HeadLine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(main);
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
