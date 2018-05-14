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

package com.dbeginc.dbweatherdata.proxies.remote.weather.locations

import com.google.gson.annotations.SerializedName

data class GeonamesItem(@SerializedName("adminCode1")
                        val adminCode: String = "",
                        @SerializedName("lng")
                        val lng: Double = 0.0,
                        @SerializedName("geonameId")
                        val geonameId: Int = 0,
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
                        val fcode: String = ""
)