package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Weather Object
 */
data class Weather(val location: Location, val latitude: Double, val longitude: Double, val timezone: String,
                   val currently: Currently, val minutely: Minutely?, val hourly: Hourly,
                   val daily: Daily, val alerts: List<Alert>?, val flags: Flags
)