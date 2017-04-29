package com.darelbitsy.dbweather.utils.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.darelbitsy.dbweather.utils.helper.FeedDataInForeground;
import com.darelbitsy.dbweather.utils.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.utils.broadcastreceivers.ServiceRestart;
import com.darelbitsy.dbweather.utils.broadcastreceivers.SyncDataReceiver;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * Created by Darel Bitsy on 06/02/17.
 */

public class KillCheckerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        final Intent serviceReschedule = new Intent(this, ServiceRestart.class);
        serviceReschedule.setFlags(START_FLAG_REDELIVERY);
        if (PendingIntent.getBroadcast(this,
                7130,
                serviceReschedule,
                PendingIntent.FLAG_NO_CREATE) == null) {
            rescheduleService();
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final Intent serviceReschedule = new Intent(this, ServiceRestart.class);
        serviceReschedule.setFlags(START_FLAG_REDELIVERY)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        if (PendingIntent.getBroadcast(this,
                7130,
                serviceReschedule,
                PendingIntent.FLAG_NO_CREATE) == null) {
            rescheduleService();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        AndroidThreeTen.init(this);
        new AlarmConfigHelper(this).setClothingNotificationAlarm();

        final Intent syncTaskIntent = new Intent(this, SyncDataReceiver.class);
        syncTaskIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        if (PendingIntent.getBroadcast(this,
                7127,
                syncTaskIntent,
                PendingIntent.FLAG_NO_CREATE) == null) {
            FeedDataInForeground.setNextSync(this);
        }
        Log.i("Feed", "KILL SERVICE DONE");
        final Intent serviceReschedule = new Intent(this, ServiceRestart.class);
        serviceReschedule.setFlags(START_FLAG_REDELIVERY)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        if (PendingIntent.getBroadcast(this,
                7130,
                serviceReschedule,
                PendingIntent.FLAG_NO_CREATE) == null) {
            rescheduleService();
        }
        super.onTaskRemoved(rootIntent);
    }

    private void rescheduleService() {
        final Intent serviceReschedule = new Intent(this, ServiceRestart.class);
        serviceReschedule.setFlags(START_FLAG_REDELIVERY);
        final PendingIntent servicePendingIntent = PendingIntent.getBroadcast(this,
                7130,
                serviceReschedule,
                PendingIntent.FLAG_UPDATE_CURRENT);


        final AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
                    System.currentTimeMillis()+600000,
                    servicePendingIntent);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+300000,
                    servicePendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, servicePendingIntent);

        } else  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC,
                    System.currentTimeMillis()+600000,
                    servicePendingIntent);

        } else {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis()+600000,
                    AlarmManager.INTERVAL_HOUR,
                    servicePendingIntent);
        }
        Log.i(ConstantHolder.TAG, "Kill Checker Service rescheduled");
    }
}
