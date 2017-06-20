package com.dbeginc.dbweather.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.firebase.IAnalyticProvider;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;
import com.dbeginc.dbweather.utils.utility.AppUtil;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CUSTOM_TAB_PACKAGE_NOT_FOUND;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_PERMISSION_DECLINED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_PERMISSION_GRANTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_GET_ACCOUNT;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_PERMISSION_DECLINED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_PERMISSION_GRANTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PERMISSION_EVENT;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.VOICE_QUERY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WRITE_PERMISSION_DECLINED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WRITE_PERMISSION_GRANTED;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Base DBWeather Activity
 */

public class BaseActivity extends AppCompatActivity {

    @Inject
    public AppDataProvider mAppDataProvider;
    @Inject
    public IAnalyticProvider mAnalyticProvider;

    @Inject
    @Named(LOCATION_UPDATE)
    public PublishSubject<String> locationUpdateEvent;
    @Inject
    @Named(VOICE_QUERY)
    public PublishSubject<String> voiceQuery;

    @Inject
    @Named(PERMISSION_EVENT)
    public PublishSubject<Boolean> permissionEvent;

    @Inject
    public PublishSubject<WeatherData> weatherDataUpdateEvent;

    @Inject
    public PublishSubject<List<Article>> newsUpdateEvent;

    @Inject
    public PublishSubject<Pair<DataSnapshot, String>> liveDatabaseUpdateEvent;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWeatherApplication.getComponent().inject(this);
        MobileAds.initialize(this, "ca-app-pub-3786486250382359~1426079826");

        if (mAppDataProvider.getCustomTabPackage().isEmpty()) {
            final String chromeTabSupported = AppUtil.isChromeTabSupported(getApplicationContext());
            if (chromeTabSupported != null) { mAppDataProvider.setCustomTabPackage(chromeTabSupported); }
            else { mAppDataProvider.setCustomTabPackage(CUSTOM_TAB_PACKAGE_NOT_FOUND); }
        }
    }

    protected boolean isNetworkAvailable() {
        final NetworkInfo networkInfo;
        final ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

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
            mAppDataProvider.setWritePermissionStatus(true);
            Log.i(ConstantHolder.TAG, "the permission was already provided");
        }
    }

    protected void askLocationPermIfNeeded() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mAppDataProvider.setGpsPermissionStatus(true);
        }
    }

    protected void askAccountInfoPermIfNeeded() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_GET_ACCOUNT);
        } else {
            mAppDataProvider.setAccountPermissionStatus(true);
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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final Bundle message = new Bundle();
        message.putInt(ConstantHolder.PERMISSION_ID, View.generateViewId());
        String EVENT = "NO_PERMISSION";

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mAppDataProvider.setGpsPermissionStatus(true);
                EVENT = LOCATION_PERMISSION_GRANTED;

            } else {
                EVENT = LOCATION_PERMISSION_DECLINED;
            }

        } else if (requestCode == MY_PERMISSIONS_REQUEST_GET_ACCOUNT) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mAppDataProvider.setAccountPermissionStatus(true);
                EVENT = NEWS_PERMISSION_GRANTED;

            } else {
                EVENT = NEWS_PERMISSION_DECLINED;
            }

        } else if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mAppDataProvider.setWritePermissionStatus(true);
                EVENT = WRITE_PERMISSION_GRANTED;

            } else { EVENT = WRITE_PERMISSION_DECLINED; }
        }

        mAnalyticProvider.logEvent(EVENT, message);
    }
}
