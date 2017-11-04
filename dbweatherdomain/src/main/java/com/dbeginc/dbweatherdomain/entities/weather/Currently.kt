package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Current Weather Data
 */
data class Currently(val time: Long, val summary: String, val icon: String, val temperature: Double, val apparentTemperature: Double?,
                     val precipIntensity: Double, val precipIntensityError: Double?, val precipProbability: Double, val precipType: String?,
                     val nearestStormDistance: Long?, val nearestStormBearing: Long?, val humidity: Double, val windSpeed: Double, val cloudCover: Double,
                     val windBearing: Long?, val visibility: Double, val dewPoint: Double?, val pressure: Double)