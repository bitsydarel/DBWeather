package com.darelbitsy.dbweather.models.datatypes.weather;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Darel Bitsy on 21/04/17.
 * Class representing an Weather Info
 */

public class WeatherInfo implements Parcelable {
    public final ObservableBoolean isCurrentWeather = new ObservableBoolean(false);

    public final ObservableField<String> locationName = new ObservableField<>();
    public final ObservableInt icon = new ObservableInt();
    public final ObservableField<String> summary = new ObservableField<>();
    public final ObservableField<String> time = new ObservableField<>();

    public final ObservableInt temperature = new ObservableInt();
    public final ObservableInt apparentTemperature = new ObservableInt();

    public final ObservableField<String> windSpeed = new ObservableField<>();
    public final ObservableField<String> humidity = new ObservableField<>();

    public final ObservableField<String> cloudCover = new ObservableField<>();

    public final ObservableField<String> precipitationType = new ObservableField<>();
    public final ObservableField<String> precipitationProbability =
            new ObservableField<>();

    public final ObservableField<String> sunrise = new ObservableField<>();
    public final ObservableField<String> sunset = new ObservableField<>();

    public WeatherInfo() {
        //Empty Because i use it to initiate my instance
    }

    private WeatherInfo(final Parcel in) {
        isCurrentWeather.set(in.readInt() != 0);

        locationName.set(in.readString());
        icon.set(in.readInt());
        summary.set(in.readString());
        time.set(in.readString());

        temperature.set(in.readInt());
        apparentTemperature.set(in.readInt());

        windSpeed.set(in.readString());
        humidity.set(in.readString());

        cloudCover.set(in.readString());

        precipitationType.set(in.readString());
        precipitationProbability.set(in.readString());

        sunrise.set(in.readString());
        sunset.set(in.readString());
    }

    public static final Creator<WeatherInfo> CREATOR = new Creator<WeatherInfo>() {
        @Override
        public WeatherInfo createFromParcel(final Parcel in) {
            return new WeatherInfo(in);
        }

        @Override
        public WeatherInfo[] newArray(final int size) {
            return new WeatherInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {

        dest.writeInt(isCurrentWeather.get() ? 1 : 0);

        dest.writeString(locationName.get());
        dest.writeInt(icon.get());
        dest.writeString(summary.get());
        dest.writeString(time.get());

        dest.writeInt(temperature.get());
        dest.writeInt(apparentTemperature.get());

        dest.writeString(windSpeed.get());
        dest.writeString(humidity.get());

        dest.writeString(cloudCover.get());

        dest.writeString(precipitationType.get());
        dest.writeString(precipitationProbability.get());

        dest.writeString(sunrise.get());
        dest.writeString(sunset.get());
    }
}
