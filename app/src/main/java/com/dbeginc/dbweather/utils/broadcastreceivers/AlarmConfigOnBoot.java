package com.dbeginc.dbweather.utils.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dbeginc.dbweather.utils.helper.AlarmConfigHelper;
import com.dbeginc.dbweather.utils.services.NewsSyncService;
import com.dbeginc.dbweather.utils.services.WeatherSyncService;

import static android.app.Service.START_FLAG_REDELIVERY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_SYNC_JOB_ID;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_SYNC_JOB_ID;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmConfigOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {
            new AlarmConfigHelper(context).setClothingNotificationAlarm();

            final Intent newsSyncService = new Intent(context, NewsSyncService.class);
            newsSyncService.setFlags(START_FLAG_REDELIVERY);

            final PendingIntent newsServicePendingIntent = PendingIntent.getBroadcast(context,
                    NEWS_SYNC_JOB_ID,
                    newsSyncService,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            final Intent weatherSyncService = new Intent(context, WeatherSyncService.class);
            weatherSyncService.setFlags(START_FLAG_REDELIVERY);

            final PendingIntent weatherServicePendingIntent = PendingIntent.getBroadcast(context,
                    WEATHER_SYNC_JOB_ID,
                    weatherSyncService,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis()+3600000,
                    AlarmManager.INTERVAL_HOUR,
                    newsServicePendingIntent);

            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis()+3600000,
                    AlarmManager.INTERVAL_HOUR,
                    weatherServicePendingIntent);
        }
    }
}
