package com.darelbitsy.dbweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class Current extends WeatherData implements Parcelable {
    private String mWeekSummary;

    public static final Creator<Current> CREATOR = new Creator<Current>() {
        @Override
        public Current createFromParcel(Parcel in) { return new Current(in); }

        @Override
        public Current[] newArray(int size) { return new Current[size]; }
    };

    public Current() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCityName());
        dest.writeInt(getTemperature());
        dest.writeLong(getTime());
        dest.writeString(getSummary());
        dest.writeString(getIcon());
        dest.writeString(getTimeZone());
        dest.writeInt(getHumidity());
        dest.writeInt(getPrecipChance());
        dest.writeString(mWeekSummary);
        dest.writeInt(getCloudCover());
        dest.writeString(getPrecipType());
        dest.writeInt(getWindSpeed());
    }

    private Current(Parcel in) {
        setCityName(in.readString());
        setTemperature(in.readInt());
        setTime(in.readLong());
        setSummary(in.readString());
        setIcon(in.readString());
        setTimeZone(in.readString());
        setHumidity(in.readInt());
        setPrecipChance(in.readInt());
        mWeekSummary = in.readString();
        setCloudCover(in.readInt());
        setPrecipType(in.readString());
        setWindSpeed(in.readInt());
    }

    public String getFormattedTime() {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h:mm a");

        return Instant.ofEpochSecond(getTime())
                .atZone(ZoneId.of(getTimeZone()))
                .format(format);
    }
    public void setWeekSummary(final String weekSummary) { mWeekSummary = weekSummary; }

    public String getWeekSummary() { return mWeekSummary == null ? "--" : mWeekSummary ; }
}
