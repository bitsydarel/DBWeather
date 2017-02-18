package com.darelbitsy.dbweather.weather;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 13/01/17.
 */

public class WeatherData {
    private String mIcon;
    private String mSummary;
    private String mTimeZone;
    private String mCityName;

    private long mTime;
    private int mTemperature, mHumidity;
    private  int mPrecipChance;
    private String mPrecipType;
    private int mCloudCover;
    private int mWindSpeed;

    String getPrecipType() { return mPrecipType; }

    public void setPrecipType(String precipType) { mPrecipType = precipType; }

    public String getTimeZone() {
        return mTimeZone;
    }
    public void setTimeZone(final String timeZone) {
        mTimeZone = timeZone;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(final String cityName) { mCityName = cityName; }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(final String summary) {
        mSummary = summary;
    }

    public String getIcon() { return mIcon == null ? "clear-day" : mIcon; }

    public void setIcon(final String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(final long time) {
        mTime = time;
    }

    public int getTemperature() { return mTemperature; }

    public void setTemperature(final int temperature) { mTemperature = temperature; }
    public void setTemperature(final double temperature) { setTemperature((int) Math.round(temperature)); }

    int getWindSpeed() { return mWindSpeed; }

    void setWindSpeed(final int windSpeed) { mWindSpeed = windSpeed; }
    public void setWindSpeed(final double windSpeed) { setWindSpeed((int) Math.round(windSpeed)); }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(final int humidity) { mHumidity = humidity; }
    public void setHumidity(final double humidity) { setHumidity((int) (humidity * 100)); }

    public int getPrecipChance() { return mPrecipChance; }

    private void setPrecipChance(final int precipChance) { mPrecipChance = precipChance; }
    public void setPrecipChance(final double precipChance) { setPrecipChance((int) (precipChance * 100)); }

    int getCloudCover() { return mCloudCover; }

    void setCloudCover(final int cloudCover) { mCloudCover = cloudCover; }
    public void setCloudCover(final double cloudCover) { setCloudCover((int) (cloudCover * 100)); }

    public int getIconId() {
        int iconId = R.drawable.clear_day;
        switch (getIcon()) {
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
            default:
                break;
        }
        return iconId;
    }

    public int getIconId(final String iconName) {
        setIcon(iconName);
        return getIconId();
    }
}
