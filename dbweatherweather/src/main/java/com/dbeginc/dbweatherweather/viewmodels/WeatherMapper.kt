/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherweather.viewmodels

import com.dbeginc.dbweathercommon.utils.round
import com.dbeginc.dbweatherdomain.entities.weather.*
import com.dbeginc.dbweatherweather.R
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by darel on 18.09.17.
 *
 * Weather ViewModel Mapper
 */


fun Weather.toUi(): WeatherModel {
    val unit = if (flags.units == "us") "F" else "C"

    val currentDayName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

    val today = daily.data.mapIndexed { index, day -> index to day }
            .first { (_, day) -> day.time.dayOfWeek(timezone) == currentDayName }
            .second

    return WeatherModel(
            location = location.toUi(),
            current = currently.toUi(
                    location = location.name,
                    timeZone = timezone,
                    unit = unit,
                    sunrise = today.sunriseTime,
                    sunset = today.sunsetTime
            ),
            hourly = hourly.data.map { hour -> hour.toUi(unit, timezone) },
            daily = daily.data.map { day ->
                day.toUi(
                        dayName = day.time.dayOfWeek(timezone),
                        timeZone = timezone,
                        unit = unit
                )
            },
            alerts = alerts?.map { alert -> alert.toUi(timezone) }
    )
}


fun Currently.toUi(location: String, sunrise: Long, sunset: Long, unit: String, timeZone: String?): CurrentWeatherModel {
    return CurrentWeatherModel(
            location = location,
            temperature = temperature.round(),
            apparentTemperature = apparentTemperature?.round() ?: 0,
            icon = icon.getId(),
            summary = summary,
            time = time,
            windSpeed = windSpeed.toMps(),
            humidity = humidity.toPercent(),
            cloudCover = cloudCover.toPercent(),
            precipitationProbability = precipProbability.toPercent(),
            sunrise = sunrise.toFormattedTime(timeZone),
            sunset = sunset.toFormattedTime(timeZone),
            temperatureUnit = unit
    )
}

fun DailyData.toUi(dayName: String, timeZone: String?, unit: String): DayWeatherModel {
    return DayWeatherModel(
            dayName = dayName,
            time = time,
            summary = summary,
            icon = icon.getId(),
            temperatureUnit = unit,
            temperatureHigh = temperatureHigh.round(),
            temperatureHighTime = temperatureHighTime,
            temperatureLow = temperatureLow.round(),
            temperatureLowTime = temperatureLowTime,
            apparentTemperatureHigh = apparentTemperatureHigh.round(),
            apparentTemperatureLow = apparentTemperatureLow.round(),
            dewPoint = dewPoint,
            humidity = humidity.toPercent(),
            pressure = pressure,
            windSpeed = windSpeed.toPercent(),
            windGust = windGust,
            windGustTime = windGustTime,
            windBearing = windBearing,
            moonPhase = moonPhase,
            uvIndex = uvIndex,
            uvIndexTime = uvIndexTime,
            sunsetTime = sunsetTime.toFormattedTime(timeZone),
            sunriseTime = sunriseTime.toFormattedTime(timeZone),
            precipIntensity = precipIntensity,
            precipIntensityMax = precipIntensityMax,
            precipProbability = precipProbability,
            precipType = precipType
    )
}

fun HourlyData.toUi(unit: String, timeZone: String?): HourWeatherModel {
    return HourWeatherModel(
            hourlyTime = time.toHour(timeZone),
            time = time,
            icon = icon.getId(),
            temperature = temperature.round(),
            temperatureUnit = unit
    )
}

fun Alert.toUi(timeZone: String?): AlertWeatherModel {
    return AlertWeatherModel(
            time = time.toFormattedTime(timeZone),
            title = title,
            description = description,
            uri = uri,
            expires = expires.toFormattedTime(timeZone),
            severity = severity,
            regions = regions
    )
}

fun Location.toUi(): WeatherLocationModel {
    return WeatherLocationModel(
            name = name,
            latitude = latitude,
            longitude = longitude,
            countryName = countryName,
            countryCode = countryCode
    )
}

fun Long.dayOfWeek(timeZone: String?): String {
    val format = DateTimeFormatter.ofPattern("EEEE")

    return Instant.ofEpochSecond(this)
            .atZone(ZoneId.of(timeZone ?: TimeZone.getDefault().id))
            .format(format)
}

fun Long.toHour(timeZone: String?) : String {
    val format = DateTimeFormatter.ofPattern("h a")

    return Instant.ofEpochSecond(this)
            .atZone(ZoneId.of(timeZone ?: TimeZone.getDefault().id))
            .format(format)
}

fun Double.toMps() : String = "%d mps".format(this.round())

fun Double.toPercent() : String = "%d%%".format(this.round())

fun Long.toFormattedTime(timeZone: String?) : String {
    val format = DateTimeFormatter.ofPattern("h:mm a")

    return Instant.ofEpochSecond(this)
            .atZone(ZoneId.of(timeZone ?: TimeZone.getDefault().id))
            .format(format)
}

fun String.getId(): Int {
    return when (this) {
        "clear-night" ->  R.drawable.clear_night
        "rain" ->  R.drawable.rain
        "snow" -> R.drawable.snow
        "sleet" -> R.drawable.sleet
        "wind" -> R.drawable.wind
        "fog" -> R.drawable.fog
        "cloudy" -> R.drawable.cloudy
        "partly-cloudy-day" -> R.drawable.partly_cloudy
        "partly-cloudy-night" -> R.drawable.cloudy_night
        else -> R.drawable.clear_day
    }
}