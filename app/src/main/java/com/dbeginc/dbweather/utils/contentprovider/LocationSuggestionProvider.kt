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

package com.dbeginc.dbweather.utils.contentprovider

import android.app.SearchManager
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import com.dbeginc.dbweather.DBWeatherApp
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import com.dbeginc.dbweatherweather.viewmodels.toViewModel
import dagger.android.DaggerContentProvider
import javax.inject.Inject

/**
 * Created by Darel Bitsy on 04/04/17.
 *
 * Location Suggestion provider
 */
class LocationSuggestionProvider : DaggerContentProvider(), WithDependencies {
    @Inject
    lateinit var model: WeatherRepository

    override fun query(uri: Uri, projection: Array<String>?,
                       selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {


        val userQuery = uri.lastPathSegment

        val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2), 3)

        if (userQuery != null && userQuery != SearchManager.SUGGEST_URI_PATH_QUERY && userQuery.isNotEmpty()) {
            model.getLocations(userQuery)
                    .map { locations -> locations.map { location -> location.toViewModel() } }
                    .subscribe(
                            DBWeatherApp.WEATHER_SEARCH_RESULTS::onNext,
                            { error -> LogDispatcher.logError(error) }
                    )
        }

        DBWeatherApp.WEATHER_SEARCH_RESULTS.value?.forEachIndexed { index, location ->
            matrixCursor.addRow(arrayOf(index, R.drawable.city_location_icon, location.name, location.countryName))
        }

        return matrixCursor
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

}