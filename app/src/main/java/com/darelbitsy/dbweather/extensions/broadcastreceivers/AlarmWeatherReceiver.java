package com.darelbitsy.dbweather.models.broadcastreceivers;

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

import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.provider.weather.NetworkWeatherProvider;
import com.darelbitsy.dbweather.models.utility.AppUtil;
import com.darelbitsy.dbweather.models.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.views.activities.NotificationActivity;
import com.darelbitsy.dbweather.presenters.helper.NotificationHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.darelbitsy.dbweather.models.holder.ConstantHolder.NOTIF_ICON;
import static com.darelbitsy.dbweather.models.holder.ConstantHolder.NOTIF_SUMMARY;
import static com.darelbitsy.dbweather.models.holder.ConstantHolder.NOTIF_TEMPERATURE;

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
        public void onSuccess(final Weather weather) {
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

            final Bundle data = new Bundle();
            data.putString(NOTIF_ICON, weather.getCurrently().getIcon());
            data.putString(NOTIF_SUMMARY, weather.getCurrently().getSummary());
            data.putInt(NOTIF_TEMPERATURE, WeatherUtil.getTemperatureInInt(weather.getCurrently().getTemperature()));

            notificationIntent.putExtras(data);

            setNotification(weather.getCurrently().getIcon(), weather.getCurrently().getSummary());

            Log.i(ConstantHolder.TAG, "Feed Alarm broadcast receiver");
            mContext.startActivity(notificationIntent, null);
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error while setting up the notification");
        }
    }

    private void setNotificationWithoutNetwork() {
        final HourlyData hour = mDatabase.getNotificationHour(System.currentTimeMillis(),
                mDatabase.getWeatherData().getTimezone());

        mNotifAdvice = new NotificationHelper(mContext,
                hour.getIcon(),
                hour.getTemperature());

        mTemperature = WeatherUtil.getTemperatureInInt(hour.getTemperature());

        Log.i("RECEIVER", "HourlyData Received icon: " + hour.getIcon() +
                " Summary: " + hour.getSummary() +
                " Temperature: " + hour.getTemperature());

        final Bundle data = new Bundle();
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
    public void onReceive(final Context context, final Intent intent) {
        final PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(FLAG_KEEP_SCREEN_ON | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE,
                "notification_lock");

        wakeLock.acquire(900000);

        try {
            Log.i("RECEIVER", "Inside the broadcast receiver");
            mContext = context;
            mDatabase = DatabaseOperation.newInstance(context);

            if (AppUtil.isNetworkAvailable(context)) {
                new NetworkWeatherProvider(context)
                        .getWeather()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new GetWeather());

            } else { setNotificationWithoutNetwork(); }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                new AlarmConfigHelper(context)
                        .setClothingNotificationAlarm();
            }

        } catch (final Exception e) {
            Log.i(ConstantHolder.TAG, "Error in weather receiver : " + e.getMessage());

        } finally {
            wakeLock.release();
        }

    }


    private void setNotification(final String iconName, final String summary) {
        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent barNotification = new Intent(mContext, NotificationActivity.class);

        final Bundle data = new Bundle();
        data.putString(NOTIF_ICON, iconName);
        data.putString(NOTIF_SUMMARY, summary);
        data.putInt(NOTIF_TEMPERATURE, mTemperature);

        barNotification.putExtras(data);

        barNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                7129,
                barNotification,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(mContext)
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