package com.darelbitsy.dbweather.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.jakewharton.threetenabp.AndroidThreeTen;

import static android.app.Service.START_FLAG_REDELIVERY;
import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * Created by Darel Bitsy on 08/02/17.
 */
public class ServiceRestart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(FLAG_KEEP_SCREEN_ON | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE,
                "notification_lock");
        wakeLock.acquire(900000);
        Intent serviceReschedule = new Intent(context, ServiceRestart.class);
        serviceReschedule.setFlags(START_FLAG_REDELIVERY);
        AndroidThreeTen.init(context);
        PendingIntent servicePendingIntent = PendingIntent.getBroadcast(context,
                7130,
                serviceReschedule,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis()+600000,
                    servicePendingIntent);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+300000,
                    servicePendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, servicePendingIntent);

        } else  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis()+600000,
                    servicePendingIntent);

        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis()+600000,
                    AlarmManager.INTERVAL_HOUR,
                    servicePendingIntent);
        }
        Log.i(ConstantHolder.TAG, "Kill Checker Service rescheduled");
        wakeLock.release();
    }
}
