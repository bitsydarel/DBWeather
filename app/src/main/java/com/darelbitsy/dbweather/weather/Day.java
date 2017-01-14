package com.darelbitsy.dbweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Darel Bitsy on 07/01/17.
 */

public class Day extends WeatherData implements Parcelable {
    private double mTemperatureMax;

    public Day() { }

    private Day(Parcel in) {
        setTime(in.readLong());
        setSummary(in.readString());
        mTemperatureMax = in.readDouble();
        setIcon(in.readString());
        setTimeZone(in.readString());
        setCityName(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getTime());
        dest.writeString(getSummary());
        dest.writeDouble(getTemperatureMax());
        dest.writeString(getIcon());
        dest.writeString(getTimeZone());
        dest.writeString(getCityName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public int getTemperatureMax() { return (int) Math.round(mTemperatureMax); }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public String getDayOfTheWeek() {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE");

        return Instant.ofEpochSecond(getTime())
                .atZone(ZoneId.of(getTimeZone()))
                .format(format);
    }

    //Template for me to present data
    /*public String getFormattedTime() {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h a");

        return Instant.ofEpochSecond(getTime())
                .atZone(ZoneId.of(getTimeZone()))
                .format(format);
    }*/
}
