package com.dbeginc.dbweather.models.datatypes.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class Weather implements Parcelable {

    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("currently")
    @Expose
    private Currently currently;
    @SerializedName("minutely")
    @Expose
    private Minutely minutely;
    @SerializedName("hourly")
    @Expose
    private Hourly hourly;
    @SerializedName("daily")
    @Expose
    private Daily daily;
    @SerializedName("alerts")
    @Expose
    private List<Alert> alerts = null;

    @SerializedName("flags")
    @Expose
    private Flags flags;

    private String cityName;

    public Weather() {}

    protected Weather(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        timezone = in.readString();
        currently = in.readParcelable(Currently.class.getClassLoader());
        hourly = in.readParcelable(Hourly.class.getClassLoader());
        daily = in.readParcelable(Daily.class.getClassLoader());
        alerts = in.createTypedArrayList(Alert.CREATOR);
        flags = in.readParcelable(Flags.class.getClassLoader());
        cityName = in.readString();
    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public Currently getCurrently() {
        return currently;
    }

    public void setCurrently(final Currently currently) {
        this.currently = currently;
    }

    public Minutely getMinutely() {
        return minutely;
    }

    public void setMinutely(final Minutely minutely) {
        this.minutely = minutely;
    }

    public Hourly getHourly() {
        return hourly;
    }

    public void setHourly(final Hourly hourly) {
        this.hourly = hourly;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(final Daily daily) {
        this.daily = daily;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(final List<Alert> alerts) {
        this.alerts = alerts;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(timezone);
        dest.writeParcelable(currently, flags);
        dest.writeParcelable(hourly, flags);
        dest.writeParcelable(daily, flags);
        dest.writeTypedList(alerts);
        dest.writeParcelable(this.flags, flags);
        dest.writeString(cityName);
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(final String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", timezone='" + timezone + '\'' +
                ", currently=" + currently +
                ", minutely=" + minutely +
                ", hourly=" + hourly +
                ", daily=" + daily +
                ", alerts=" + alerts +
                ", cityName='" + cityName + '\'' +
                '}';
    }
}
