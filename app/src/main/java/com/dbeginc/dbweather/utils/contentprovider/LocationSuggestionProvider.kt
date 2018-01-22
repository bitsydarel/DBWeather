package com.dbeginc.dbweather.utils.contentprovider

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.di.modules.DBWeatherApplicationModule
import com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweatherdomain.entities.requests.weather.LocationRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.usecases.weather.GetLocations
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Created by Darel Bitsy on 04/04/17.
 *
 * Location Suggestion provider
 */
class LocationSuggestionProvider : ContentProvider() {
    @Inject lateinit var mGetLocationsCommand: GetLocations
    @Inject lateinit var queryEventResult: BehaviorSubject<List<Location>>

    override fun onCreate(): Boolean {

        Injector.injectLocationProviderDep(this)

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?,
                       selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {


        val userQuery = uri.lastPathSegment

        val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2), 3)

        if (userQuery != null && userQuery != SearchManager.SUGGEST_URI_PATH_QUERY && userQuery.isNotEmpty()) {
            mGetLocationsCommand.execute(LocationRequest(userQuery))
                    .subscribe(
                            { locations -> queryEventResult.onNext(locations) },
                            { error -> Log.e(TAG, error.localizedMessage, error) }
                    )
        }

        queryEventResult.value?.forEachIndexed {
            index, location -> matrixCursor.addRow(arrayOf(index, R.drawable.city_location_icon, location.name, location.countryName))
        }

        return matrixCursor
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

}