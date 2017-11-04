/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.proxies.mappers

import com.dbeginc.dbweatherdata.proxies.local.weather.*
import com.dbeginc.dbweatherdata.proxies.remote.weather.*
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocation
import com.dbeginc.dbweatherdomain.entities.weather.*

/**
 * Created by darel on 01.10.17.
 *
 * Type Converter for Data Model to domain Model
 */

fun RemoteLocation.toDomain() : Location = Location(name, latitude, longitude, countryCode, countryName)

fun toUnknownLocation(latitude: Double, longitude: Double) : Location = Location("Unknown", latitude = latitude, longitude = longitude, countryCode = "", countryName = "")

fun RemoteAlert.toDomain() : Alert = Alert(time, title, description, uri, expires, regions, severity)

fun RemoteCurrently.toDomain() : Currently {
    return Currently(time, summary, icon, temperature, apparentTemperature, precipIntensity, precipIntensityError, precipProbability,
            precipType, nearestStormDistance, nearestStormBearing, humidity, windSpeed, cloudCover, windBearing, visibility, dewPoint, pressure
    )
}

fun RemoteDaily.toDomain() : Daily = Daily(summary, icon, data.map { dailyData -> dailyData.toDomain() })

fun RemoteDailyData.toDomain(): DailyData {
    return DailyData(time, summary, icon, temperatureHigh, temperatureHighTime,
            temperatureLow, temperatureLowTime, apparentTemperatureHigh, apparentTemperatureHighTime,
            apparentTemperatureLow, apparentTemperatureLowTime, dewPoint, humidity,
            pressure, windSpeed, windGust, windGustTime, windBearing, cloudCover, moonPhase, visibility,
            uvIndex, uvIndexTime, sunsetTime, sunriseTime, precipIntensity, precipIntensityMax, precipProbability,
            precipType
    )
}

fun RemoteFlags.toDomain() : Flags = Flags(sources, units)

fun RemoteHourly.toDomain() : Hourly = Hourly(summary, icon, data.map { hourlyData -> hourlyData.toDomain() })

fun RemoteMinutely.toDomain() : Minutely = Minutely(summary, icon, data.map { minutelyData -> minutelyData.toDomain() })

fun RemoteMinutelyData.toDomain() : MinutelyData =MinutelyData(time, precipIntensity, precipProbability)

fun RemoteWeather.toDomain() : Weather {
    return Weather(location = location?.toDomain() ?: toUnknownLocation(latitude, longitude),
            latitude = latitude, longitude = longitude,
            timezone = timezone, currently = currently.toDomain(), minutely = minutely?.toDomain(),
            hourly = hourly.toDomain(), daily = daily.toDomain(), alerts = alerts?.map { alert -> alert.toDomain() }, flags = flags.toDomain()
    )
}

fun LocalAlert.toDomain() : Alert = Alert(time, title, description, uri, expires, regions, severity)

fun LocalCurrently.toDomainWeather() : Currently = Currently(time, summary, icon, temperature, apparentTemperature, precipIntensity, precipIntensityError, precipProbability,
        precipType, nearestStormDistance, nearestStormBearing, humidity, windSpeed, cloudCover, windBearing, visibility, dewPoint, pressure
)

fun LocalDaily.toDomain() : Daily = Daily(summary, icon, data.map { dailyData -> dailyData.toDomain() })


fun LocalDailyData.toDomain() : DailyData {
    return DailyData(time, summary, icon, temperatureHigh, temperatureHighTime,
            temperatureLow, temperatureLowTime, apparentTemperatureHigh, apparentTemperatureHighTime,
            apparentTemperatureLow, apparentTemperatureLowTime, dewPoint, humidity,
            pressure, windSpeed, windGust, windGustTime, windBearing, cloudCover, moonPhase, visibility,
            uvIndex, uvIndexTime, sunsetTime, sunriseTime, precipIntensity, precipIntensityMax, precipProbability,
            precipType
    )
}

fun LocalFlags.toDomain() : Flags = Flags(sources, units)

fun LocalHourly.toDomain() : Hourly = Hourly(summary, icon, data.map { hourlyData -> hourlyData.toDomain() })

fun LocalHourlyData.toDomain() : HourlyData {
    return HourlyData(time, summary, icon, temperature, apparentTemperature, dewPoint, humidity, pressure,
            windSpeed, windGust, windBearing, cloudCover, precipIntensity, precipProbability, precipType, uvIndex, ozone
    )
}

fun LocalLocation.toDomain() : Location = Location(name=locationName, latitude= locationLatitude, longitude= locationLongitude, countryCode=countryCode, countryName=countryName)

fun LocalMinutely.toDomain() : Minutely = Minutely(summary, icon, data.map { minutelyData -> minutelyData.toDomain() })

fun LocalMinutelyData.toDomain() : MinutelyData  = MinutelyData(time, precipIntensity, precipProbability)

fun LocalWeather.toDomain() : Weather {
    return Weather(location.toDomain(), latitude, longitude, timezone,
            currently.toDomainWeather(), minutely?.toDomain(), hourly.toDomain(),
            daily.toDomain(), alerts?.map { alert -> alert.toDomain() }, flags.toDomain()
    )
}