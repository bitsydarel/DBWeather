package com.darelbitsy.dbweather.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.services.SyncDatabaseService;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * Created by Darel Bitsy on 04/02/17.
 */
public class SyncDataReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SyncDatabaseService.class);
        AndroidThreeTen.init(context);
        FeedDataInForeground.setNextSync(context);
        startWakefulService(context, service);
    }
}
