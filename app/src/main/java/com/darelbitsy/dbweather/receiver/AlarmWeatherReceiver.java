package com.darelbitsy.dbweather.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.alert.NotificationActivity;
import com.darelbitsy.dbweather.alert.NotificationHelper;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.ui.MainActivity;
import com.darelbitsy.dbweather.weather.Hour;

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
        mContext = context;
        mHour = new DatabaseOperation(context).getNotificationHour(System.currentTimeMillis());
        mNotifAdvice = new NotificationHelper(context, mHour);
        Toast.makeText(context, "RECEIVED THE BROADCAST RECEIVER "+intent.getAction(), Toast.LENGTH_LONG)
                .show();
        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(NOTIF_HOUR, mHour);
        setNotifaction();
        context.startActivity(notificationIntent, null);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            new AlarmConfigHelper(context).setClothingNotificationAlarm();
        }
    }


    private void setNotifaction() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        Intent barNotification = new Intent(mContext, MainActivity.class);
        barNotification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                7129,
                barNotification,
                PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v7.app.NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext)
                .setContentIntent(pendingIntent)
                .setSmallIcon(mHour.getIconId())
                .setContentTitle(mNotifAdvice.getTitleFromIcon())
                .setContentText(mNotifAdvice.getDescription())
                .setAutoCancel(true);

        notificationManager.notify(7129, notificationBuilder.build());
    }
}
