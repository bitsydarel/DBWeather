package com.dbeginc.dbweather.utils.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dbeginc.dbweather.utils.helper.AlarmConfigHelper;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_SYNC_JOB_ID;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_SYNC_SERVICES_SCHEDULED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_SYNC_JOB_ID;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_SYNC_SERVICE_SCHEDULED;

/**
 * Created by Darel Bitsy on 06/02/17.
 * Check if App has been killed by the system
 */

public class KillCheckerService extends IntentService {

    public KillCheckerService() {
        super(KillCheckerService.class.getSimpleName());
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        rescheduleService();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopSelf();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        new AlarmConfigHelper(this).setClothingNotificationAlarm();
        super.onTaskRemoved(rootIntent);
    }

    private void rescheduleService() {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            final JobScheduler scheduler = (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);

            if (sharedPreferences.getInt(NEWS_SYNC_SERVICES_SCHEDULED, 0) == JobScheduler.RESULT_FAILURE) {
                final JobInfo newsJobInfo = new JobInfo.Builder(NEWS_SYNC_JOB_ID, new ComponentName(getApplicationContext(), NewsSyncJobScheduler.class))
                        .setPersisted(true)
                        .setPeriodic(3600000)
                        .setRequiredNetworkType(NETWORK_TYPE_ANY)
                        .setRequiresCharging(true)
                        .setRequiresDeviceIdle(true)
                        .build();

                sharedPreferences
                        .edit()
                        .putInt(ConstantHolder.NEWS_SYNC_SERVICES_SCHEDULED, scheduler.schedule(newsJobInfo))
                        .apply();
            }

            if (sharedPreferences.getInt(WEATHER_SYNC_SERVICE_SCHEDULED, 0) == JobScheduler.RESULT_FAILURE) {
                final JobInfo weatherJobInfo = new JobInfo.Builder(WEATHER_SYNC_JOB_ID, new ComponentName(getApplicationContext(), WeatherSyncJobScheduler.class))
                        .setPersisted(true)
                        .setPeriodic(3600000)
                        .setRequiredNetworkType(NETWORK_TYPE_ANY)
                        .setRequiresCharging(false)
                        .build();

                sharedPreferences
                        .edit()
                        .putInt(ConstantHolder.WEATHER_SYNC_SERVICE_SCHEDULED, scheduler.schedule(weatherJobInfo))
                        .apply();
            }

        } else {
            final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (sharedPreferences.getInt(NEWS_SYNC_SERVICES_SCHEDULED, 0) == JobScheduler.RESULT_FAILURE) {
                final Intent newsSyncService = new Intent(getApplicationContext(), NewsSyncService.class);
                newsSyncService.setFlags(START_FLAG_REDELIVERY);
                final PendingIntent newsServicePendingIntent =
                        PendingIntent.getBroadcast(getApplicationContext(), NEWS_SYNC_JOB_ID, newsSyncService,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()+3600000,
                        AlarmManager.INTERVAL_HOUR,
                        newsServicePendingIntent);
            }
            if (sharedPreferences.getInt(WEATHER_SYNC_SERVICE_SCHEDULED, 0) == JobScheduler.RESULT_FAILURE) {
                final Intent weatherSyncService = new Intent(getApplicationContext(), WeatherSyncService.class);
                weatherSyncService.setFlags(START_FLAG_REDELIVERY);
                final PendingIntent weatherServicePendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        WEATHER_SYNC_JOB_ID,
                        weatherSyncService,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setRepeating(AlarmManager.RTC,
                        System.currentTimeMillis()+3600000,
                        AlarmManager.INTERVAL_HOUR,
                        weatherServicePendingIntent);
            }
        }
    }
}
