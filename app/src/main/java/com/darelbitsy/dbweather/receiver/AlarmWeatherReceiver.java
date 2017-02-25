package com.darelbitsy.dbweather.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.api.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.HourlyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.ui.alert.NotificationActivity;
import com.darelbitsy.dbweather.ui.alert.NotificationHelper;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.darelbitsy.dbweather.ui.alert.NotificationHelper.NOTIFICATION_DESC;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmWeatherReceiver extends BroadcastReceiver {
    private Context mContext;
    private DatabaseOperation mDatabase;
    private NotificationHelper mNotifAdvice;

    private class GetWeather  extends GetWeatherHelper {
        public GetWeather(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Weather weather) {
            Intent notificationIntent = new Intent(mContext, NotificationActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            if (weather.getCurrently() != null) {
                mNotifAdvice = new NotificationHelper(mContext,
                        weather.getCurrently().getIcon(),
                        weather.getCurrently().getTemperature());

                notificationIntent.putExtra(ConstantHolder.NOTIF_ICON, weather.getCurrently().getIcon());
                notificationIntent.putExtra(ConstantHolder.NOTIF_SUMMARY, weather.getCurrently().getSummary());
                notificationIntent.putExtra(ConstantHolder.NOTIF_TEMPERATURE, weather.getCurrently().getTemperature());

                setNotification(weather.getCurrently().getIcon());


            } else {
                HourlyData hour = mDatabase.getNotificationHour(System.currentTimeMillis(),
                        mDatabase.getWeatherData().getTimezone());

                mNotifAdvice = new NotificationHelper(mContext,
                        hour.getIcon(),
                        hour.getTemperature());

                notificationIntent.putExtra(ConstantHolder.NOTIF_ICON, hour.getIcon());
                notificationIntent.putExtra(ConstantHolder.NOTIF_SUMMARY, hour.getSummary());
                notificationIntent.putExtra(ConstantHolder.NOTIF_TEMPERATURE, hour.getTemperature());

                setNotification(hour.getIcon());

            }
            Log.i(ConstantHolder.TAG, "Feed Alarm broadcast receiver");
            mContext.startActivity(notificationIntent, null);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(AlarmConfigHelper.MY_ACTION)) {
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(FLAG_KEEP_SCREEN_ON | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE,
                    "notification_lock");
            wakeLock.acquire(900000);
            mContext = context;
            mDatabase = new DatabaseOperation(context);

            new GetWeather(context).execute();

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                new AlarmConfigHelper(context).setClothingNotificationAlarm();
            }

            wakeLock.release();
        }
    }


    private void setNotification(String iconName) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        Intent barNotification = new Intent(mContext, ConstantHolder.class);
        barNotification.putExtra(NOTIFICATION_DESC, mNotifAdvice.getDescription());
        barNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                7129,
                barNotification,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(mContext)
                .setSmallIcon(WeatherUtil.getIconId(iconName))
                .setContentTitle(mNotifAdvice.getTitleFromIcon())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mNotifAdvice.getDescription()))
                .setContentText(mNotifAdvice.getDescription())
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