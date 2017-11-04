package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Hourly Weather Data
 */
data class Hourly(val summary: String, val icon: String, val data: List<HourlyData>)