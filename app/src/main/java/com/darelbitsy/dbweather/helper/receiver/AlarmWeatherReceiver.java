package com.darelbitsy.dbweather.helper.receiver;

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

import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.HourlyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.ui.alert.NotificationActivity;
import com.darelbitsy.dbweather.ui.alert.NotificationHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NOTIF_ICON;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NOTIF_SUMMARY;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NOTIF_TEMPERATURE;

/**
 * Created by Darel Bitsy on 30/01/17.
 * Alarm weather receiver and notification
 * display
 */

public class AlarmWeatherReceiver extends BroadcastReceiver {
    private Context mContext;
    private DatabaseOperation mDatabase;
    private NotificationHelper mNotifAdvice;
    private Intent notificationIntent;
    private int mTemperature;

    private class GetWeather extends DisposableSingleObserver<Weather> {
        @Override
        public void onSuccess(Weather weather) {
            notificationIntent = new Intent(mContext, NotificationActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            mTemperature = WeatherUtil
                    .getTemperatureInInt(weather.getCurrently().getTemperature());

            mNotifAdvice = new NotificationHelper(mContext,
                    weather.getCurrently().getIcon(),
                    weather.getCurrently().getTemperature());

            Log.i("RECEIVER", "Observer Received icon: " + weather.getCurrently().getIcon() +
                    " Summary: " + weather.getCurrently().getSummary() +
                    " Temperature: " + weather.getCurrently().getTemperature());

            Bundle data = new Bundle();
            data.putString(NOTIF_ICON, weather.getCurrently().getIcon());
            data.putString(NOTIF_SUMMARY, weather.getCurrently().getSummary());
            data.putInt(NOTIF_TEMPERATURE, WeatherUtil.getTemperatureInInt(weather.getCurrently().getTemperature()));

            notificationIntent.putExtras(data);

            setNotification(weather.getCurrently().getIcon(), weather.getCurrently().getSummary());

            Log.i(ConstantHolder.TAG, "Feed Alarm broadcast receiver");
            mContext.startActivity(notificationIntent, null);
        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error while setting up the notification");
        }
    }

    private void setNotificationWithoutNetwork() {
        HourlyData hour = mDatabase.getNotificationHour(System.currentTimeMillis(),
                mDatabase.getWeatherData().getTimezone());

        mNotifAdvice = new NotificationHelper(mContext,
                hour.getIcon(),
                hour.getTemperature());

        mTemperature = WeatherUtil.getTemperatureInInt(hour.getTemperature());

        Log.i("RECEIVER", "HourlyData Received icon: " + hour.getIcon() +
                " Summary: " + hour.getSummary() +
                " Temperature: " + hour.getTemperature());

        Bundle data = new Bundle();
        data.putString(NOTIF_ICON, hour.getIcon());
        data.putString(NOTIF_SUMMARY, hour.getSummary());
        data.putInt(NOTIF_TEMPERATURE,
                WeatherUtil.getTemperatureInInt(hour.getTemperature()));

        notificationIntent.putExtras(data);

        setNotification(hour.getIcon(), hour.getSummary());
        Log.i(ConstantHolder.TAG, "Feed Alarm broadcast receiver");

        mContext.startActivity(notificationIntent);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(FLAG_KEEP_SCREEN_ON | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE,
                "notification_lock");

        wakeLock.acquire(900000);

        Log.i("RECEIVER", "Inside the broadcast receiver");
        mContext = context;
        mDatabase = new DatabaseOperation(context);

        if (AppUtil.isNetworkAvailable(context)) {
            new GetWeatherHelper(context)
                    .getObservableWeatherFromApi(mDatabase, context)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetWeather());

        } else { setNotificationWithoutNetwork(); }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            new AlarmConfigHelper(context)
                    .setClothingNotificationAlarm();
        }
        wakeLock.release();

    }


    private void setNotification(String iconName, String summary) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);

        Intent barNotification = new Intent(mContext, NotificationActivity.class);

        Bundle data = new Bundle();
        data.putString(NOTIF_ICON, iconName);
        data.putString(NOTIF_SUMMARY, summary);
        data.putInt(NOTIF_TEMPERATURE, mTemperature);

        barNotification.putExtras(data);

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

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            notificationBuilder.setCategory(Notification.CATEGORY_REMINDER);
        }
        notificationManager.notify(7129, notificationBuilder.build());

    }
}