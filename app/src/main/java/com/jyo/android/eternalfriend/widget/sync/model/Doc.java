package com.jyo.android.eternalfriend.widget.sync.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class Doc implements Parcelable{

    private HeadLine headline;
    private List<MultiMedia> multimedia;
    @SerializedName("web_url")
    private String webUrl;
    @SerializedName("byline")
    private ByLine byLine;
    @SerializedName("pub_date")
    private String pubDate;
    private String snippet;

    protected Doc(Parcel in) {
        headline = in.readParcelable(HeadLine.class.getClassLoader());
        multimedia = in.createTypedArrayList(MultiMedia.CREATOR);
        webUrl = in.readString();
        byLine = in.readParcelable(ByLine.class.getClassLoader());
        pubDate = in.readString();
        snippet = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(headline, flags);
        dest.writeTypedList(multimedia);
        dest.writeString(webUrl);
        dest.writeParcelable(byLine, flags);
        dest.writeString(pubDate);
        dest.writeString(snippet);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Doc> CREATOR = new Creator<Doc>() {
        @Override
        public Doc createFromParcel(Parcel in) {
            return new Doc(in);
        }

        @Override
        public Doc[] newArray(int size) {
            return new Doc[size];
        }
    };

    public HeadLine getHeadline() {
        return headline;
    }

    public void setHeadline(HeadLine headline) {
        this.headline = headline;
    }

    public List<MultiMedia> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(List<MultiMedia> multimedia) {
        this.multimedia = multimedia;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public ByLine getByLine() {
        return byLine;
    }

    public void setByLine(ByLine byLine) {
        this.byLine = byLine;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
