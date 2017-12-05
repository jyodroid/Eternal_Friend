package com.jyo.android.eternalfriend.widget.sync.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class ResponseObject implements Parcelable {

    @SerializedName("docs")
    private List<Doc> docs;

    protected ResponseObject(Parcel in) {
        docs = in.createTypedArrayList(Doc.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(docs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseObject> CREATOR = new Creator<ResponseObject>() {
        @Override
        public ResponseObject createFromParcel(Parcel in) {
            return new ResponseObject(in);
        }

        @Override
        public ResponseObject[] newArray(int size) {
            return new ResponseObject[size];
        }
    };

    public List<Doc> getDocs() {
        return docs;
    }

    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }
}
