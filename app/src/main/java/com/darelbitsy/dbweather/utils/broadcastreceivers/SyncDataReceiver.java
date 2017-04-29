package com.darelbitsy.dbweather.utils.broadcastreceivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.darelbitsy.dbweather.utils.helper.FeedDataInForeground;
import com.darelbitsy.dbweather.utils.services.SyncDatabaseService;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * Created by Darel Bitsy on 04/02/17.
 * Background Sync Adapter
 */
public class SyncDataReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Intent service = new Intent(context, SyncDatabaseService.class);
        AndroidThreeTen.init(context);
        FeedDataInForeground.setNextSync(context);
        startWakefulService(context, service);
    }
}
