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

package com.dbeginc.dbweatherdata.proxies.mappers

import com.dbeginc.dbweatherdata.proxies.local.weather.*
import com.dbeginc.dbweatherdata.proxies.remote.weather.*
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.GeonamesItem
import com.dbeginc.dbweatherdomain.entities.weather.*

/**
 * Created by darel on 01.10.17.
 *
 * Type Converter for Data Model to domain Model
 */

internal fun GeonamesItem.toDomain() : Location = Location(
        name = name,
        latitude = lat,
        longitude = lng,
        countryCode = countryCode,
        countryName = countryName
)

internal fun toUnknownLocation(latitude: Double, longitude: Double): Location = Location(
        name = "Unknown",
        latitude = latitude,
        longitude = longitude,
        countryCode = "",
        countryName = ""
)

internal fun RemoteAlert.toDomain(): Alert = Alert(
        time = time,
        title = title,
        description = description,
        uri = uri,
        expires = expires,
        regions = regions,
        severity = severity
)


internal fun RemoteCurrently.toDomain(): Currently {
    return Currently(
            time = time,
            summary = summary,
            icon = icon,
            temperature = temperature,
            apparentTemperature = apparentTemperature,
            precipIntensity = precipIntensity,
            precipIntensityError = precipIntensityError,
            precipProbability = precipProbability,
            precipType = precipType,
            nearestStormDistance = nearestStormDistance,
            nearestStormBearing = nearestStormBearing,
            humidity = humidity,
            windSpeed = windSpeed,
            cloudCover = cloudCover,
            windBearing = windBearing,
            visibility = visibility,
            dewPoint = dewPoint,
            pressure = pressure
    )
}

internal fun RemoteDaily.toDomain(): Daily = Daily(
        summary = summary,
        icon = icon,
        data = data.map { dailyData -> dailyData.toDomain() }
)

internal fun RemoteDailyData.toDomain(): DailyData {
    return DailyData(
            time = time,
            summary = summary,
            icon = icon,
            temperatureHigh = temperatureHigh,
            temperatureHighTime = temperatureHighTime,
            temperatureLow = temperatureLow,
            temperatureLowTime = temperatureLowTime,
            apparentTemperatureHigh = apparentTemperatureHigh,
            apparentTemperatureHighTime = apparentTemperatureHighTime,
            apparentTemperatureLow = apparentTemperatureLow,
            apparentTemperatureLowTime = apparentTemperatureLowTime,
            dewPoint = dewPoint,
            humidity = humidity,
            pressure = pressure,
            windSpeed = windSpeed,
            windGust = windGust,
            windGustTime = windGustTime,
            windBearing = windBearing,
            cloudCover = cloudCover,
            moonPhase = moonPhase,
            visibility = visibility,
            uvIndex = uvIndex,
            uvIndexTime = uvIndexTime,
            sunsetTime = sunsetTime,
            sunriseTime = sunriseTime,
            precipIntensity = precipIntensity,
            precipIntensityMax = precipIntensityMax,
            precipProbability = precipProbability,
            precipType = precipType
    )
}

internal fun RemoteFlags.toDomain(): Flags = Flags(sources, units)

internal fun RemoteHourly.toDomain(): Hourly = Hourly(
        summary = summary,
        icon = icon,
        data = data.map { hourlyData -> hourlyData.toDomain() }
)

internal fun RemoteMinutely.toDomain(): Minutely = Minutely(
        summary = summary,
        icon = icon,
        data = data.map { minutelyData -> minutelyData.toDomain() }
)

internal fun RemoteMinutelyData.toDomain(): MinutelyData = MinutelyData(
        time = time,
        precipIntensity = precipIntensity,
        precipProbability = precipProbability
)

internal fun RemoteWeather.toDomain(): Weather {
    return Weather(
            location = location?.toDomain() ?: toUnknownLocation(latitude, longitude),
            latitude = latitude,
            longitude = longitude,
            timezone = timezone,
            currently = currently.toDomain(),
            minutely = minutely?.toDomain(),
            hourly = hourly.toDomain(),
            daily = daily.toDomain(),
            alerts = alerts?.map { alert -> alert.toDomain() },
            flags = flags.toDomain()
    )
}

internal fun LocalAlert.toDomain(): Alert = Alert(
        time = time,
        title = title,
        description = description,
        uri = uri,
        expires = expires,
        regions = regions,
        severity = severity
)

internal fun LocalCurrently.toDomainWeather(): Currently = Currently(
        time = time,
        summary = summary,
        icon = icon,
        temperature = temperature,
        apparentTemperature = apparentTemperature,
        precipIntensity = precipIntensity,
        precipIntensityError = precipIntensityError,
        precipProbability = precipProbability,
        precipType = precipType,
        nearestStormDistance = nearestStormDistance,
        nearestStormBearing = nearestStormBearing,
        humidity = humidity,
        windSpeed = windSpeed,
        cloudCover = cloudCover,
        windBearing = windBearing,
        visibility = visibility,
        dewPoint = dewPoint,
        pressure = pressure
)

internal fun LocalDaily.toDomain(): Daily = Daily(
        summary = summary,
        icon = icon,
        data = data.map { dailyData -> dailyData.toDomain() }
)

internal fun LocalDailyData.toDomain(): DailyData {
    return DailyData(
            time = time,
            summary = summary,
            icon = icon,
            temperatureHigh = temperatureHigh,
            temperatureHighTime = temperatureHighTime,
            temperatureLow = temperatureLow,
            temperatureLowTime = temperatureLowTime,
            apparentTemperatureHigh = apparentTemperatureHigh,
            apparentTemperatureHighTime = apparentTemperatureHighTime,
            apparentTemperatureLow = apparentTemperatureLow,
            apparentTemperatureLowTime = apparentTemperatureLowTime,
            dewPoint = dewPoint,
            humidity = humidity,
            pressure = pressure,
            windSpeed = windSpeed,
            windGust = windGust,
            windGustTime = windGustTime,
            windBearing = windBearing,
            cloudCover = cloudCover,
            moonPhase = moonPhase,
            visibility = visibility,
            uvIndex = uvIndex,
            uvIndexTime = uvIndexTime,
            sunriseTime = sunsetTime,
            sunsetTime = sunriseTime,
            precipIntensity = precipIntensity,
            precipIntensityMax = precipIntensityMax,
            precipProbability = precipProbability,
            precipType = precipType
    )
}

internal fun LocalFlags.toDomain(): Flags = Flags(sources, units)

internal fun LocalHourly.toDomain(): Hourly = Hourly(
        summary = summary,
        icon = icon,
        data = data.map { hourlyData -> hourlyData.toDomain() }
)

internal fun LocalHourlyData.toDomain(): HourlyData {
    return HourlyData(
            time = time,
            summary = summary,
            icon = icon,
            temperature = temperature,
            apparentTemperature = apparentTemperature,
            dewPoint = dewPoint,
            humidity = humidity,
            pressure = pressure,
            windSpeed = windSpeed,
            windGust = windGust,
            windBearing = windBearing,
            cloudCover = cloudCover,
            precipIntensity = precipIntensity,
            precipProbability = precipProbability,
            precipType = precipType,
            uvIndex = uvIndex,
            ozone = ozone
    )
}

internal fun LocalLocation.toDomain(): Location = Location(
        name = locationName,
        latitude = locationLatitude,
        longitude = locationLongitude,
        countryCode = countryCode,
        countryName = countryName
)

internal fun LocalMinutely.toDomain(): Minutely = Minutely(
        summary = summary,
        icon = icon,
        data = data.map { minutelyData -> minutelyData.toDomain() }
)

internal fun LocalMinutelyData.toDomain(): MinutelyData = MinutelyData(
        time = time,
        precipIntensity = precipIntensity,
        precipProbability = precipProbability
)

internal fun LocalWeather.toDomain(): Weather {
    return Weather(
            location = location.toDomain(),
            latitude = latitude,
            longitude = longitude,
            timezone = timezone,
            currently = currently.toDomainWeather(),
            minutely = minutely?.toDomain(),
            hourly = hourly.toDomain(),
            daily = daily.toDomain(),
            alerts = alerts?.map { alert -> alert.toDomain() },
            flags = flags.toDomain()
    )
}