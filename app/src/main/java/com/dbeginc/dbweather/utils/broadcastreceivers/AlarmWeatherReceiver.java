package com.dbeginc.dbweather.utils.broadcastreceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.weather.HourlyData;
import com.dbeginc.dbweather.ui.notifications.NotificationActivity;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.helper.NotificationHelper;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIF_ICON;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIF_SUMMARY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIF_TEMPERATURE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

/**
 * Created by Darel Bitsy on 30/01/17.
 * Alarm weather receiver and notification
 * display
 */

public class AlarmWeatherReceiver extends BroadcastReceiver {
    private NotificationHelper mNotifyAdvice;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PARTIAL_WAKE_LOCK ,
                "notification_lock");

        wakeLock.acquire(900000);
        try { setNotificationWithoutNetwork(context); }
        catch (final Exception e) { Crashlytics.logException(e); }
        finally { wakeLock.release(); }
    }

    private void setNotificationWithoutNetwork(final Context context) {
        final DatabaseOperation database = DatabaseOperation.getInstance(context);
        final String timezone = database.getWeatherData().getTimezone();

        final HourlyData hour = database.getNotificationHour(timezone);
        final int temperatureInInt = WeatherUtil.getTemperatureInInt(hour.getTemperature());
        final String icon = hour.getIcon();
        final String summary = hour.getSummary();

        mNotifyAdvice = new NotificationHelper(context, icon, temperatureInInt);

        final Bundle data = new Bundle();
        data.putString(NOTIF_ICON, icon);
        data.putString(NOTIF_SUMMARY, summary);
        data.putInt(NOTIF_TEMPERATURE, temperatureInInt);

        final Intent notificationIntent = new Intent(context, NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        notificationIntent.putExtras(data);

        setNotification(context, icon, summary, temperatureInInt);
        context.startActivity(notificationIntent);
    }


    private void setNotification(final Context context, final String iconName, final String summary, final int temperature) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent barNotification = new Intent(context, NotificationActivity.class);

        final Bundle data = new Bundle();
        data.putString(NOTIF_ICON, iconName);
        data.putString(NOTIF_SUMMARY, summary);
        data.putInt(NOTIF_TEMPERATURE, temperature);

        barNotification.putExtras(data);

        barNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context,
                7129,
                barNotification,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(context)
                .setSmallIcon(WeatherUtil.getIconId(iconName))
                .setContentTitle(mNotifyAdvice.getTitleFromIcon())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mNotifyAdvice.getDescription()))
                .setContentText(mNotifyAdvice.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle())
                .setDefaults(Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_VIBRATE
                        | Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) { notificationBuilder.setCategory(Notification.CATEGORY_REMINDER); }
        notificationManager.notify(7129, notificationBuilder.build());
    }
}