package com.dbeginc.dbweather.models.datatypes.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitsy Darel on 11.05.17.
 * Weather Data Object representing complete weather
 * info of a location
 */

public class WeatherData implements Parcelable {
    private final List<WeatherInfo> weatherInfoList;
    private final List<HourlyData> hourlyWeatherList;
    private List<Alert> alertList;

    public WeatherData() {
        weatherInfoList = new ArrayList<>();
        hourlyWeatherList = new ArrayList<>();
        alertList = null;
    }

    public static final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(final Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(final int size) {
            return new WeatherData[size];
        }
    };

    public List<WeatherInfo> getWeatherInfoList() {
        return weatherInfoList;
    }

    public List<HourlyData> getHourlyWeatherList() {
        return hourlyWeatherList;
    }

    public List<Alert> getAlertList() {
        return alertList;
    }

    public void setWeatherInfoList(final List<WeatherInfo> weatherInfoList) {
        this.weatherInfoList.clear();
        this.weatherInfoList.addAll(weatherInfoList);
    }

    public void setHourlyWeatherList(final List<HourlyData> hourlyWeatherList) {
        this.hourlyWeatherList.clear();
        this.hourlyWeatherList.addAll(hourlyWeatherList);
    }

    public void setAlertList(final List<Alert> alertList) {
        if (alertList != null) {
            if (this.alertList == null) { this.alertList = alertList; }
            else {
                this.alertList.clear();
                this.alertList.addAll(alertList);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private WeatherData(final Parcel in) {
        weatherInfoList = in.createTypedArrayList(WeatherInfo.CREATOR);
        hourlyWeatherList = in.createTypedArrayList(HourlyData.CREATOR);
        alertList = in.createTypedArrayList(Alert.CREATOR);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeTypedList(weatherInfoList);
        dest.writeTypedList(hourlyWeatherList);
        dest.writeTypedList(alertList);
    }
}
