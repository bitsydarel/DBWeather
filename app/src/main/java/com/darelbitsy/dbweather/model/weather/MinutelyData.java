package com.darelbitsy.dbweather.model.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class MinutelyData implements Parcelable {
    @SerializedName("time")
    @Expose
    private long time;
    @SerializedName("precipIntensity")
    @Expose
    private double precipIntensity;
    @SerializedName("precipIntensityError")
    @Expose
    private double precipIntensityError;
    @SerializedName("precipProbability")
    @Expose
    private double precipProbability;
    @SerializedName("precipType")
    @Expose
    private String precipType;

    public MinutelyData() {}

    protected MinutelyData(Parcel in) {
        time = in.readLong();
        precipIntensity = in.readDouble();
        precipIntensityError = in.readDouble();
        precipProbability = in.readDouble();
        precipType = in.readString();
    }

    public static final Creator<MinutelyData> CREATOR = new Creator<MinutelyData>() {
        @Override
        public MinutelyData createFromParcel(Parcel in) {
            return new MinutelyData(in);
        }

        @Override
        public MinutelyData[] newArray(int size) {
            return new MinutelyData[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public double getPrecipIntensityError() {
        return precipIntensityError;
    }

    public void setPrecipIntensityError(double precipIntensityError) {
        this.precipIntensityError = precipIntensityError;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeDouble(precipIntensity);
        dest.writeDouble(precipIntensityError);
        dest.writeDouble(precipProbability);
        dest.writeString(precipType);
    }
}
