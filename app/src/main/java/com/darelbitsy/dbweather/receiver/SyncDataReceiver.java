package com.darelbitsy.dbweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.services.SyncDatabaseService;
import com.jakewharton.threetenabp.AndroidThreeTen;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Darel Bitsy on 04/02/17.
 */
public class SyncDataReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SyncDatabaseService.class);
        AndroidThreeTen.init(context);
        startWakefulService(context, service);
    }
}
