package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Minutely Weather Object
 */
data class Minutely(val summary: String, val icon: String, val data: List<MinutelyData>)