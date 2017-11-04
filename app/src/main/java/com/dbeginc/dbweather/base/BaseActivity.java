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

package com.dbeginc.dbweather.base;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Base DBWeather Activity
 */

public class BaseActivity extends AppCompatActivity {

    @Inject
    public SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWeatherApplication.getComponent().inject(this);
        MobileAds.initialize(this, "ca-app-pub-3786486250382359~1426079826");

        /*if (mAppDataProvider.getCustomTabPackage().isEmpty()) {
            final String chromeTabSupported = AppUtil.isChromeTabSupported(getApplicationContext());
            if (chromeTabSupported != null) { mAppDataProvider.setCustomTabPackage(chromeTabSupported); }
            else { mAppDataProvider.setCustomTabPackage(CUSTOM_TAB_PACKAGE_NOT_FOUND); }
        }*/
    }

    protected boolean isNetworkAvailable() {
        final NetworkInfo networkInfo;
        final ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    protected void askWriteToExtPermIfNeeded() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            /*mAppDataProvider.setWritePermissionStatus(true);*/
            Log.i(ConstantHolder.TAG, "the permission was already provided");
        }
    }

    public void askLocationPermIfNeeded() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
//            mAppDataProvider.setGpsPermissionStatus(true);
        }
    }

    protected void shareScreenShot() throws IOException {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, takeScreenShot());

        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));
    }

    private Uri takeScreenShot() throws IOException {
        OutputStream outputStream = null;

        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            try {
                final View viewToShot = getWindow().getDecorView().getRootView();
                final boolean defaultDrawing = viewToShot.isDrawingCacheEnabled();
                viewToShot.setDrawingCacheEnabled(true);
                final Bitmap screenShot = Bitmap.createBitmap(viewToShot.getDrawingCache());
                viewToShot.setDrawingCacheEnabled(defaultDrawing);

                final ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "db_weather");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                final Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);

                if (uri != null) {
                    outputStream = getContentResolver().openOutputStream(uri);
                    screenShot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    return uri;

                }

            } catch (final IOException e) {
                Log.i(ConstantHolder.TAG, "Error while Creating screenshot File: " + e.getMessage());

            } finally { if (outputStream != null) {
                    outputStream.close();
                } }
        }
        return null;
    }
}
