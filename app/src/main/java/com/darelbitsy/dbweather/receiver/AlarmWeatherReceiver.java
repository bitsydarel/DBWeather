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
import com.darelbitsy.dbweather.alert.NotificationActivity;
import com.darelbitsy.dbweather.alert.NotificationHelper;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.ui.MainActivity;
import com.darelbitsy.dbweather.weather.Hour;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.darelbitsy.dbweather.alert.NotificationHelper.NOTIFICATION_DESC;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmWeatherReceiver extends BroadcastReceiver {
    public static final String NOTIF_HOUR = "notif_hour";
    private Context mContext;
    private Hour mHour;
    private NotificationHelper mNotifAdvice;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(AlarmConfigHelper.MY_ACTION)) {
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(FLAG_KEEP_SCREEN_ON | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE,
                    "notification_lock");
            wakeLock.acquire(900000);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                new AlarmConfigHelper(context).setClothingNotificationAlarm();
            }
            mContext = context;
            mHour = new DatabaseOperation(context).getNotificationHour(System.currentTimeMillis());
            mNotifAdvice = new NotificationHelper(context, mHour);
            Log.i(MainActivity.TAG, "Feed Alarm broadcast receiver");

            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra(NOTIF_HOUR, mHour);
            setNotification();
            context.startActivity(notificationIntent, null);
            wakeLock.release();
        }
    }


    private void setNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        Intent barNotification = new Intent(mContext, MainActivity.class);
        barNotification.putExtra(NOTIFICATION_DESC, mNotifAdvice.getDescription());
        barNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                7129,
                barNotification,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(mContext)
                .setSmallIcon(mHour.getIconId())
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