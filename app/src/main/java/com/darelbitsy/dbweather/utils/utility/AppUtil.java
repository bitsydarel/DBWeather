package com.darelbitsy.dbweather.utils.utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.darelbitsy.dbweather.utils.broadcastreceivers.AlarmWeatherReceiver;
import com.darelbitsy.dbweather.utils.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.utils.helper.FeedDataInForeground;
import com.darelbitsy.dbweather.utils.services.KillCheckerService;

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
        new AlarmConfigHelper(context).setClothingNotificationAlarm();
        FeedDataInForeground.setNextSync(context.getApplicationContext());

        context.startService(new Intent(context, KillCheckerService.class));
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_ALARM_ON, true)
                .apply();
    }
}
