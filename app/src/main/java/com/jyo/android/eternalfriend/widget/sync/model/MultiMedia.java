package com.jyo.android.eternalfriend.widget.sync.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class MultiMedia implements Parcelable {
    private String url;

    protected MultiMedia(Parcel in) {
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MultiMedia> CREATOR = new Creator<MultiMedia>() {
        @Override
        public MultiMedia createFromParcel(Parcel in) {
            return new MultiMedia(in);
        }

        @Override
        public MultiMedia[] newArray(int size) {
            return new MultiMedia[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
