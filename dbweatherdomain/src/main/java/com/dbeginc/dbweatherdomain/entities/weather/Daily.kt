package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Daily Weather
 */
data class Daily(val summary: String, val icon: String, val data: List<DailyData>)