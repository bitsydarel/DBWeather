package com.dbeginc.dbweatherdomain.entities.weather

/**
 * Created by darel on 15.09.17.
 *
 * Weather Alert Info
 */
data class Alert(val time: Long, val title: String, val description: String, val uri: String,
                 val expires: Long, val regions: List<String>, val severity: String
)