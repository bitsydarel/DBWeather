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

package com.dbeginc.dbweatherdata.proxies.local

import android.arch.persistence.room.TypeConverter
import android.support.annotation.RestrictTo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Created by darel on 04.10.17.
 *
 * Common Local Converters
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class CommonLocalConverters {
    @TypeConverter
    fun jsonStringToListOfString(json: String): List<String> {
        val listOfHourlyData = Types.newParameterizedType(List::class.java, String::class.java)

        return Moshi.Builder()
                .build()
                .adapter<List<String>>(listOfHourlyData)
                .fromJson(json)!!
    }

    @TypeConverter
    fun listOfStringToJson(list: List<String>): String {
        val listOfStrings = Types.newParameterizedType(List::class.java, String::class.java)

        return Moshi.Builder()
                .build()
                .adapter<List<String>>(listOfStrings)
                .toJson(list)
    }
}