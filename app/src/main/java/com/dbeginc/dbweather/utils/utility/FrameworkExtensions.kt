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

package com.dbeginc.dbweather.utils.utility

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by darel on 18.09.17.
 *
 * Framework extensions or helpers
 */
fun Disposable.addTo(subscription: CompositeDisposable) = subscription.add(this)

fun Double.round() : Int = Math.round(this).toInt()

fun Long.toDate() : Date = Date(this)

fun <T : Parcelable> Bundle.putList(key: String, list: List<T>) {
    val arrayList : ArrayList<T> = ArrayList(list)
    putParcelableArrayList(key, arrayList)
}

fun <T : Parcelable> Bundle.getList(key: String) : List<T> = getParcelableArrayList(key)

fun SharedPreferences.Editor.putDouble(key: String, value: Double): SharedPreferences.Editor {
    putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
    return this
}

fun SharedPreferences.getDouble(key: String) : Double {
    return java.lang.Double.longBitsToDouble(getLong(key, 0))
}