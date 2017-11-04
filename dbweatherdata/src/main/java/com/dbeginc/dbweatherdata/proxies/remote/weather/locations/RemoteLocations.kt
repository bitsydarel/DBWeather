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
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

/**
 * Created by darel on 16.09.17.
 *
 * Remote Locations
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Root(name="geonames", strict=false)
data class RemoteLocations(
        @field:ElementList(entry="geoname", inline=true, type=RemoteLocation::class, required=false, empty=false) var locations: MutableList<RemoteLocation> = mutableListOf()
)