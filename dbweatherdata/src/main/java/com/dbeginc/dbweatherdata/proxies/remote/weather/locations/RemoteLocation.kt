/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.proxies.remote.weather.locations

import android.support.annotation.RestrictTo
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * Created by darel on 16.09.17.
 *
 * RemoteLocation
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Root(name = "geoname", strict=false)
data class RemoteLocation(@field:Element(name="name", type=String::class) var name: String = "",
                          @field:Element(name="lat", type=Double::class) var latitude: Double = 0.0,
                          @field:Element(name="lng", type=Double::class) var longitude: Double= 0.0,
                          @field:Element(name="countryCode", required=false, type=String::class) var countryCode: String = "",
                          @field:Element(name="countryName", required=false, type=String::class) var countryName: String = ""
)