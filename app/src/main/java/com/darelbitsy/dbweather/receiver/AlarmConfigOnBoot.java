package com.darelbitsy.dbweather.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.ui.MainActivity;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmConfigOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean alarmUpMorning = PendingIntent.getBroadcast(context,
                7124,
                new Intent(context, AlarmWeatherReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null;

        boolean alarmUpAfternoon = PendingIntent.getBroadcast(context,
                7125,
                new Intent(context, AlarmWeatherReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null;

        boolean alarmUpNight = PendingIntent.getBroadcast(context,
                7126,
                new Intent(context, AlarmWeatherReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null;

        if(alarmUpMorning && alarmUpAfternoon && alarmUpNight) {
            new AlarmConfigHelper(context).setClothingNotificationAlarm();
            Log.i("Feed_Data", "Resetting data on reboot");
        }
        if (PendingIntent.getBroadcast(context,
                7127,
                new Intent(context, SyncDataReceiver.class),
                PendingIntent.FLAG_NO_CREATE) == null) {
            FeedDataInForeground.setNextSync(context);
            Log.i("Feed_Data", "Setted the hourly sync from Boot Up");
        }
    }
}
