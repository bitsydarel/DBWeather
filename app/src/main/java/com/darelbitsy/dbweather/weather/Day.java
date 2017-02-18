package com.darelbitsy.dbweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Darel Bitsy on 07/01/17.
 */

public class Day extends WeatherData implements Parcelable {
    private int mTemperatureMax;

    public Day() { }


    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }
        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    private Day(Parcel in) {
        setTime(in.readLong());
        setSummary(in.readString());
        mTemperatureMax = in.readInt();
        setIcon(in.readString());
        setTimeZone(in.readString());
        setHumidity(in.readInt());
        setPrecipChance(in.readInt());
        setCloudCover(in.readInt());
        setPrecipType(in.readString());
        setWindSpeed(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getTime());
        dest.writeString(getSummary());
        dest.writeInt(getTemperatureMax());
        dest.writeString(getIcon());
        dest.writeString(getTimeZone());
        dest.writeInt(getHumidity());
        dest.writeInt(getPrecipChance());
        dest.writeInt(getCloudCover());
        dest.writeString(getPrecipType());
        dest.writeInt(getWindSpeed());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getTemperatureMax() { return mTemperatureMax; }

    public void setTemperatureMax(final double temperatureMax) {
        setTemperatureMax((int) Math.round(temperatureMax));
    }
    public void setTemperatureMax(final int temperatureMax) { mTemperatureMax = temperatureMax; }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "DAY{ SUMMARY:%s; TIMEZONE:%s TEMPERATURE:%d; TIME:%d}",
                getSummary(),
                getTimeZone(),
                getTemperatureMax(),
                getTime());
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
