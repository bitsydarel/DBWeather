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

package com.dbeginc.dbweatherdata.proxies.remote.news

import android.support.annotation.RestrictTo
import com.google.gson.annotations.SerializedName

/**
 * Created by darel on 04.10.17.
 *
 * Remote NewsPaper
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class RemoteSource(@SerializedName("id") val id: String,
                        @SerializedName("name") val name: String,
                        @SerializedName("description") val description: String,
                        @SerializedName("url") val url: String,
                        @SerializedName("category") val category: String,
                        @SerializedName("language") val language: String,
                        @SerializedName("country") val country: String
)