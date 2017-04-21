package com.darelbitsy.dbweather.models.datatypes.weather;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.models.utility.weather.WeatherUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class DailyData implements Parcelable, Comparable<DailyData> {
    public DailyData() {}

    @SerializedName("time")
    @Expose
    private long time;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("sunriseTime")
    @Expose
    private long sunriseTime;

    @SerializedName("sunsetTime")
    @Expose
    private long sunsetTime;

    @SerializedName("moonPhase")
    @Expose
    private double moonPhase;

    @SerializedName("precipIntensity")
    @Expose
    private double precipIntensity;

    @SerializedName("precipIntensityMax")
    @Expose
    private double precipIntensityMax;

    @SerializedName("precipIntensityMaxTime")
    @Expose
    private long precipIntensityMaxTime;

    @SerializedName("precipProbability")
    @Expose
    private double precipProbability;

    @SerializedName("precipType")
    @Expose
    private String precipType;

    @SerializedName("temperatureMin")
    @Expose
    private double temperatureMin;

    @SerializedName("temperatureMinTime")
    @Expose
    private long temperatureMinTime;

    @SerializedName("temperatureMax")
    @Expose
    private double temperatureMax;

    @SerializedName("temperatureMaxTime")
    @Expose
    private long temperatureMaxTime;

    @SerializedName("apparentTemperatureMin")
    @Expose
    private double apparentTemperatureMin;

    @SerializedName("apparentTemperatureMinTime")
    @Expose
    private long apparentTemperatureMinTime;

    @SerializedName("apparentTemperatureMax")
    @Expose
    private double apparentTemperatureMax;

    @SerializedName("apparentTemperatureMaxTime")
    @Expose
    private long apparentTemperatureMaxTime;

    @SerializedName("dewPoint")
    @Expose
    private double dewPoint;

    @SerializedName("humidity")
    @Expose
    private double humidity;

    @SerializedName("windSpeed")
    @Expose
    private double windSpeed;

    @SerializedName("windBearing")
    @Expose
    private long windBearing;

    @SerializedName("visibility")
    @Expose
    private double visibility;

    @SerializedName("cloudCover")
    @Expose
    private double cloudCover;

    @SerializedName("pressure")
    @Expose
    private double pressure;

    @SerializedName("ozone")
    @Expose
    private double ozone;

    protected DailyData(Parcel in) {
        time = in.readLong();
        summary = in.readString();
        icon = in.readString();
        sunriseTime = in.readLong();
        sunsetTime = in.readLong();
        moonPhase = in.readDouble();
        precipIntensity = in.readDouble();
        precipIntensityMax = in.readDouble();
        precipIntensityMaxTime = in.readLong();
        precipProbability = in.readDouble();
        precipType = in.readString();
        temperatureMin = in.readDouble();
        temperatureMinTime = in.readLong();
        temperatureMax = in.readDouble();
        temperatureMaxTime = in.readLong();
        apparentTemperatureMin = in.readDouble();
        apparentTemperatureMinTime = in.readLong();
        apparentTemperatureMax = in.readDouble();
        apparentTemperatureMaxTime = in.readLong();
        dewPoint = in.readDouble();
        humidity = in.readDouble();
        windSpeed = in.readDouble();
        windBearing = in.readLong();
        visibility = in.readDouble();
        cloudCover = in.readDouble();
        pressure = in.readDouble();
        ozone = in.readDouble();
    }

    public static final Creator<DailyData> CREATOR = new Creator<DailyData>() {
        @Override
        public DailyData createFromParcel(Parcel in) {
            return new DailyData(in);
        }

        @Override
        public DailyData[] newArray(int size) {
            return new DailyData[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(final long time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(final long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public long getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(final long sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(final double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(final double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public double getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public void setPrecipIntensityMax(final double precipIntensityMax) {
        this.precipIntensityMax = precipIntensityMax;
    }

    public long getPrecipIntensityMaxTime() {
        return precipIntensityMaxTime;
    }

    public void setPrecipIntensityMaxTime(final long precipIntensityMaxTime) {
        this.precipIntensityMaxTime = precipIntensityMaxTime;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(final double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(final String precipType) {
        this.precipType = precipType;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(final double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public long getTemperatureMinTime() {
        return temperatureMinTime;
    }

    public void setTemperatureMinTime(final long temperatureMinTime) {
        this.temperatureMinTime = temperatureMinTime;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(final double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public long getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    public void setTemperatureMaxTime(final long temperatureMaxTime) {
        this.temperatureMaxTime = temperatureMaxTime;
    }

    public double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public void setApparentTemperatureMin(final double apparentTemperatureMin) {
        this.apparentTemperatureMin = apparentTemperatureMin;
    }

    public long getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    public void setApparentTemperatureMinTime(final long apparentTemperatureMinTime) {
        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
    }

    public double getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    public void setApparentTemperatureMax(final double apparentTemperatureMax) {
        this.apparentTemperatureMax = apparentTemperatureMax;
    }

    public long getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    public void setApparentTemperatureMaxTime(final long apparentTemperatureMaxTime) {
        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(final double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(final double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(final double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(final long windBearing) {
        this.windBearing = windBearing;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(final double visibility) {
        this.visibility = visibility;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(final double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(final double pressure) {
        this.pressure = pressure;
    }

    public double getOzone() {
        return ozone;
    }

    public void setOzone(final double ozone) {
        this.ozone = ozone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeString(summary);
        dest.writeString(icon);
        dest.writeLong(sunriseTime);
        dest.writeLong(sunsetTime);
        dest.writeDouble(moonPhase);
        dest.writeDouble(precipIntensity);
        dest.writeDouble(precipIntensityMax);
        dest.writeLong(precipIntensityMaxTime);
        dest.writeDouble(precipProbability);
        dest.writeString(precipType);
        dest.writeDouble(temperatureMin);
        dest.writeLong(temperatureMinTime);
        dest.writeDouble(temperatureMax);
        dest.writeLong(temperatureMaxTime);
        dest.writeDouble(apparentTemperatureMin);
        dest.writeLong(apparentTemperatureMinTime);
        dest.writeDouble(apparentTemperatureMax);
        dest.writeLong(apparentTemperatureMaxTime);
        dest.writeDouble(dewPoint);
        dest.writeDouble(humidity);
        dest.writeDouble(windSpeed);
        dest.writeLong(windBearing);
        dest.writeDouble(visibility);
        dest.writeDouble(cloudCover);
        dest.writeDouble(pressure);
        dest.writeDouble(ozone);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyData)) return false;

        final DailyData dailyData = (DailyData) o;

        return WeatherUtil.dayEquality(time, dailyData.time);
    }

    @Override
    public int hashCode() {
        return (int) (time ^ (time >>> 32));
    }

    @Override
    public int compareTo(@NonNull
                         final DailyData o) {
        return WeatherUtil.compareDay(time, o.getTime());
    }
}
