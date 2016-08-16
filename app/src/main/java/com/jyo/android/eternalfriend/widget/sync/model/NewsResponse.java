package com.jyo.android.eternalfriend.widget.sync.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class NewsResponse implements Parcelable{
    @SerializedName("response")
    private ResponseObject responseObject;

    protected NewsResponse(Parcel in) {
        responseObject = in.readParcelable(ResponseObject.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(responseObject, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewsResponse> CREATOR = new Creator<NewsResponse>() {
        @Override
        public NewsResponse createFromParcel(Parcel in) {
            return new NewsResponse(in);
        }

        @Override
        public NewsResponse[] newArray(int size) {
            return new NewsResponse[size];
        }
    };

    public ResponseObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(ResponseObject responseObject) {
        this.responseObject = responseObject;
    }
}
