package com.dbeginc.dbweatherdata.proxies.remote.weather.locations

import com.google.gson.annotations.SerializedName

data class GeonamesItem(@SerializedName("adminCode1")
                        val adminCode: String? = "",
                        @SerializedName("lng")
                        val lng: Double = 0.0,
                        @SerializedName("distance")
                        val distance: String? = "",
                        @SerializedName("geonameId")
                        val geonameId: Int? = 0,
                        @SerializedName("toponymName")
                        val toponymName: String = "",
                        @SerializedName("countryId")
                        val countryId: String = "",
                        @SerializedName("fcl")
                        val fcl: String = "",
                        @SerializedName("population")
                        val population: Long = 0,
                        @SerializedName("countryCode")
                        val countryCode: String = "",
                        @SerializedName("name")
                        val name: String = "",
                        @SerializedName("fclName")
                        val fclName: String = "",
                        @SerializedName("adminCodes1")
                        val adminCodes: AdminCodes,
                        @SerializedName("countryName")
                        val countryName: String = "",
                        @SerializedName("fcodeName")
                        val fcodeName: String = "",
                        @SerializedName("adminName1")
                        val adminName: String = "",
                        @SerializedName("lat")
                        val lat: Double = 0.0,
                        @SerializedName("fcode")
                        val fcode: String = "")