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

package com.dbeginc.dbweathernews.viewmodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by darel on 27.10.17.
 *
 * Source Model
 */
@Parcelize
data class SourceModel(val id: String, val name: String, val description: String,
                       val url: String, val category: String, val language: String,
                       val country: String, var subscribed: Boolean
) : Parcelable