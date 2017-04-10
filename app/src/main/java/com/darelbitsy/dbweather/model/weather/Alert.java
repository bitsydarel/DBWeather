package com.darelbitsy.dbweather.model.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class Alert implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("time")
    @Expose
    private long time;
    @SerializedName("expires")
    @Expose
    private long expires;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("uri")
    @Expose
    private String uri;

    public Alert() {}

    protected Alert(Parcel in) {
        title = in.readString();
        time = in.readLong();
        expires = in.readLong();
        description = in.readString();
        uri = in.readString();
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel in) {
            return new Alert(in);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(final long time) {
        this.time = time;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(final long expires) {
        this.expires = expires;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(time);
        dest.writeLong(expires);
        dest.writeString(description);
        dest.writeString(uri);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", expires=" + expires +
                ", description='" + description + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
