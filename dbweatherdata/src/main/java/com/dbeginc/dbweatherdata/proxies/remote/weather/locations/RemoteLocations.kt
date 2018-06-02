package com.dbeginc.dbweatherdata.proxies.remote.weather.locations

import com.google.gson.annotations.SerializedName

data class RemoteLocations(@SerializedName("geonames")
                           val geonames: List<GeonamesItem>?)