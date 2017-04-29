package com.darelbitsy.dbweather.utils.utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.utils.broadcastreceivers.AlarmWeatherReceiver;
import com.darelbitsy.dbweather.utils.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.utils.helper.FeedDataInForeground;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.utils.services.KillCheckerService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.darelbitsy.dbweather.utils.helper.AlarmConfigHelper.MY_ACTION;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.IS_ALARM_ON;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.PREFS_NAME;

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


    private AppUtil() {}

    public static File getFileCache(final Context context) {
        return new File(context.getCacheDir(), "dbweather_cache_dir");
    }

    public static boolean isAlarmSet(final Context context) {
        final int lastAlarm = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
