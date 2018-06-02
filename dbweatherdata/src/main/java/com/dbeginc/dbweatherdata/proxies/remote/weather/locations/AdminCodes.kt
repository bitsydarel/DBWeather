package com.dbeginc.dbweatherdata.proxies.remote.weather.locations

import com.google.gson.annotations.SerializedName

data class AdminCodes(@SerializedName("ISO3166_2")
                      val iso: String = "")