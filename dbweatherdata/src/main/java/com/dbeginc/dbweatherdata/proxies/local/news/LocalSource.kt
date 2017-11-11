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

package com.dbeginc.dbweatherdata.proxies.local.news

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.ConstantHolder.SOURCE_TABLE

/**
 * Created by darel on 04.10.17.
 *
 * Local Source
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Entity(tableName=SOURCE_TABLE)
data class LocalSource(@PrimaryKey val id: String, val name: String,
                       val description: String, val url: String,
                       val category: String, val language: String,
                       val country: String, var subscribed: Boolean,
                       @ColumnInfo(name="sort_bys_available") val sortBysAvailable: List<String>
)