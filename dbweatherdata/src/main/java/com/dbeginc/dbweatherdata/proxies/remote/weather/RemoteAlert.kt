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

package com.dbeginc.dbweatherdata.proxies.remote.weather

import android.support.annotation.RestrictTo
import com.google.gson.annotations.SerializedName

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Alert
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class RemoteAlert(@SerializedName("time") val time: Long, @SerializedName("title") val title: String,
                       @SerializedName("description") val description: String, @SerializedName("uri") val uri: String,
                       @SerializedName("expires") val expires: Long, @SerializedName("regions") val regions: List<String>,
                       @SerializedName("severity") val severity: String
)