package com.darelbitsy.dbweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.darelbitsy.dbweather.WeatherApi;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Darel Bitsy on 07/01/17.
 */

public class Day implements Parcelable {
    private long mTime;
    private String mSummary;
    private double mTemperatureMax;
    private String mIcon;
    private String mTimeZone;

    public Day() { }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperatureMax() { return (int) Math.round(mTemperatureMax); }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public int getIconId() { return WeatherApi.getIconId(getIcon()); }

    public String getDayOfTheWeek() {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("EEEE");

        return Instant.ofEpochSecond(getTime())
                .atZone(ZoneId.of(getTimeZone()))
                .format(format);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(getTemperatureMax());
        dest.writeLong(getTime());
        dest.writeString(getSummary());
        dest.writeString(getIcon());
        dest.writeString(getTimeZone());
    }

    private Day(Parcel in) {
        mTemperatureMax = in.readDouble();
        mTime = in.readLong();
        mSummary = in.readString();
        mIcon = in.readString();
        mTimeZone = in.readString();
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
}
