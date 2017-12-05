package com.jyo.android.eternalfriend.widget.sync.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class ByLine implements Parcelable {
    private String original;

    protected ByLine(Parcel in) {
        original = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ByLine> CREATOR = new Creator<ByLine>() {
        @Override
        public ByLine createFromParcel(Parcel in) {
            return new ByLine(in);
        }

        @Override
        public ByLine[] newArray(int size) {
            return new ByLine[size];
        }
    };

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
