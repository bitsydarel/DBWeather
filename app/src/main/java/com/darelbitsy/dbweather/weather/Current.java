package com.darelbitsy.dbweather.weather;

import com.darelbitsy.dbweather.WeatherApi;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class Current {

    private String mIcon;
    private String mSummary;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    private String mTimeZone;
    private long mTime;
    private double mTemperature, mHumidity, mPrecipChance;

    private String mCityName;

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        return WeatherApi.getIconId(getIcon());
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperature() { return (int) Math.round(mTemperature); }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() { return (int) Math.round(mPrecipChance * 100); }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getFormattedTime() {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h:mm a");

        return Instant.ofEpochSecond(getTime())
                .atZone(ZoneId.of(getTimeZone()))
                .format(format);
    }
}
