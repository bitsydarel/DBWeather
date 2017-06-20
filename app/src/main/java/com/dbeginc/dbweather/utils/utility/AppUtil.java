package com.dbeginc.dbweather.utils.utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsService;

import com.dbeginc.dbweather.utils.broadcastreceivers.AlarmWeatherReceiver;
import com.dbeginc.dbweather.utils.helper.AlarmConfigHelper;
import com.dbeginc.dbweather.utils.services.KillCheckerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.dbeginc.dbweather.utils.helper.AlarmConfigHelper.MY_ACTION;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 22/02/17.
 */

public class AppUtil {

    public static final OkHttpClient translateOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(35, TimeUnit.SECONDS)
            .writeTimeout(35, TimeUnit.SECONDS)
            .readTimeout(55, TimeUnit.SECONDS)
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
        context.startService(new Intent(context, KillCheckerService.class));
    }

    public static String isChromeTabSupported(final Context context) {
        final PackageManager pm = context.getPackageManager();

        // Get default VIEW intent handler that can view a web url.
        final Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));

        // Get all apps that can handle VIEW intents.
        final List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        for (final ResolveInfo info : resolvedActivityList) {
            final Intent serviceIntent = new Intent();
            serviceIntent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                return info.activityInfo.packageName;
            }
        }
        return null;
    }
}
