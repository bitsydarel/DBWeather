package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Minutely Weather Data
 */
data class MinutelyData(val time: Long, val precipIntensity: Double, val precipProbability: Double)