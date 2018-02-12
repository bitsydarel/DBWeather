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

package com.dbeginc.dbweather.base

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.utils.helper.ApplicationPreferences
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.google.android.gms.ads.MobileAds
import dagger.android.support.DaggerAppCompatActivity
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Darel Bitsy on 27/04/17.
 * Base DBWeather Activity
 */

open class BaseActivity : DaggerAppCompatActivity(), WithDependencies {
    @Inject lateinit var applicationPreferences: ApplicationPreferences

    protected fun isNetworkAvailable(): Boolean {
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = manager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        MobileAds.initialize(this, "ca-app-pub-3786486250382359~1426079826")

        /*if (mAppDataProvider.getCustomTabPackage().isEmpty()) {
            final String chromeTabSupported = AppUtil.isChromeTabSupported(getApplicationContext());
            if (chromeTabSupported != null) { mAppDataProvider.setCustomTabPackage(chromeTabSupported); }
            else { mAppDataProvider.setCustomTabPackage(CUSTOM_TAB_PACKAGE_NOT_FOUND); }
        }*/
    }

    @Throws(IOException::class)
    protected fun shareScreenShot() {
        val shareIntent = Intent(Intent.ACTION_SEND)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, takeScreenShot())

        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
    }

    @Throws(IOException::class)
    private fun takeScreenShot(): Uri? {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED, ignoreCase = true)) {
            try {
                val viewToShot = window.decorView.rootView

                val defaultDrawing = viewToShot.isDrawingCacheEnabled

                viewToShot.isDrawingCacheEnabled = true

                val screenShot = Bitmap.createBitmap(viewToShot.drawingCache)

                viewToShot.isDrawingCacheEnabled = defaultDrawing

                val values = ContentValues()

                values.put(MediaStore.Images.Media.TITLE, "db_weather")

                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                if (uri != null) {
                    contentResolver.openOutputStream(uri).use {
                        outputStream -> screenShot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    return uri
                }

            } catch (e: IOException) {
                Log.i(ConstantHolder.TAG, "Error while Creating screenshot File: " + e.message)

            }
        }
        return null
    }
}
