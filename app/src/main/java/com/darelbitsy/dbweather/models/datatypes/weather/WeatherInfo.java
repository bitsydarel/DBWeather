package com.darelbitsy.dbweather.models.datatypes.weather;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

/**
 * Created by Darel Bitsy on 21/04/17.
 * Class representing an Weather Info
 */

public class WeatherInfo {
    public final ObservableBoolean isCurrentWeather = new ObservableBoolean(false);

    public final ObservableField<String> icon = new ObservableField<>();
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
}
