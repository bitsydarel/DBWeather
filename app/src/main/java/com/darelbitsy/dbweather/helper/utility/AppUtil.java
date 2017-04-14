package com.darelbitsy.dbweather.helper.utility;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.receiver.AlarmWeatherReceiver;
import com.darelbitsy.dbweather.helper.services.KillCheckerService;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static com.darelbitsy.dbweather.helper.AlarmConfigHelper.MY_ACTION;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.IS_ACCOUNT_PERMISSION_GRANTED;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.IS_ALARM_ON;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.IS_GPS_PERMISSION_GRANTED;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.IS_WRITE_PERMISSION_GRANTED;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.LIST_OF_TYPEFACES;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_GET_ACCOUNT;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.USER_LANGUAGE;

/**
 * Created by Darel Bitsy on 22/02/17.
 */

public class AppUtil {

    public static final OkHttpClient translateOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    public static final OkHttpClient.Builder weatherOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);

    public static final OkHttpClient.Builder newsOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);

    public static final OkHttpClient.Builder geoNameOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);


    private AppUtil() {}

    public static Cache getCacheDirectory(Context context) {

        return new Cache(getFileCache(context), ConstantHolder.CACHE_SIZE);
    }

    public static File getFileCache(Context context) {
        return new File(context.getCacheDir(), "dbweather_cache_dir");
    }

    public static boolean isNetworkAvailable(final Context context) {
        final NetworkInfo networkInfo;
        final ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static boolean isAlarmSet(final Context context) {
        final int lastAlarm = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .getInt(AlarmConfigHelper.LAST_NOTIFICATION_PENDING_INTENT_ID, 0);
        if (lastAlarm == 0 ) { return false; }

        final Intent notificationLIntent = new Intent(context, AlarmWeatherReceiver.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            notificationLIntent.setFlags(0);
        } else {
            notificationLIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        notificationLIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        notificationLIntent.setAction(MY_ACTION);

        return PendingIntent.getBroadcast(context.getApplicationContext(),
                lastAlarm,
                notificationLIntent,
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void askWriteToExtPermIfNeeded(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            AppUtil.setWritePermissionValue(activity.getApplicationContext());
        }
    }

    public static void setWritePermissionValue(final Context context) {
        context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_WRITE_PERMISSION_GRANTED, true)
                .apply();
    }

    public static boolean isWritePermissionOn(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .getBoolean(IS_WRITE_PERMISSION_GRANTED, false);
    }

    public static void askLocationPermIfNeeded(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            AppUtil.setGpsPermissionValue(activity.getApplicationContext());
        }
    }

    public static void askAccountInfoPermIfNeeded(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_GET_ACCOUNT);

        } else {
            AppUtil.setAccountPermissionValue(activity.getApplicationContext());
        }
    }

    public static boolean isAccountPermissionOn(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .getBoolean(IS_ACCOUNT_PERMISSION_GRANTED, false);
    }

    public static void setAccountPermissionValue(final Context context) {
        context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_ACCOUNT_PERMISSION_GRANTED, true)
                .apply();
    }

    public static boolean isGpsPermissionOn(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .getBoolean(IS_GPS_PERMISSION_GRANTED, false);
    }

    public static void setGpsPermissionValue(final Context context) {
        context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_GPS_PERMISSION_GRANTED, true)
                .apply();
    }


    public static void setNextAlarm(final Context context) {
        final ExecutorService executorService;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            executorService = new ForkJoinPool();
            executorService.submit(new AlarmConfigHelper(context)::setClothingNotificationAlarm);
            Log.i(ConstantHolder.TAG, "Setted the alarm ");

            executorService.submit(() -> FeedDataInForeground.setNextSync(context.getApplicationContext()));
            Log.i(ConstantHolder.TAG, "Setted the hourly sync");

        } else {
            executorService = Executors.newCachedThreadPool();
            executorService.submit(new AlarmConfigHelper(context)::setClothingNotificationAlarm);
            Log.i(ConstantHolder.TAG, "Setted the alarm ");

            executorService.submit(() -> FeedDataInForeground.setNextSync(context.getApplicationContext()));
            Log.i(ConstantHolder.TAG, "Setted the hourly sync ");
        }

        context.startService(new Intent(context, KillCheckerService.class));
        executorService.shutdown();

        context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_ALARM_ON, true)
                .apply();
    }

    public static Typeface getAppGlobalTypeFace(final Context context) {
        Typeface typeface = null;

        for (final Map.Entry<List<String>, String> languages : LIST_OF_TYPEFACES.entrySet()) {
            if (languages.getKey().contains(USER_LANGUAGE)) {
                typeface = Typeface.createFromAsset(context.getAssets(),
                        languages.getValue());
            }
        }

        return typeface;
    }

    public static void setupVideoBackground(final int resourceId,
                                            final Context context,
                                            final View view) {
        final VideoView background = (VideoView) view.findViewById(R.id.backgroundVideo);
        background.stopPlayback();
        background.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId));
        background.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
        if (background.getVisibility() != View.VISIBLE) {
            background.setVisibility(View.VISIBLE);
        }
        background.start();
    }
}
