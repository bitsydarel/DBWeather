package com.darelbitsy.dbweather.utils.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.utils.helper.FeedDataInForeground;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.utils.broadcastreceivers.SyncDataReceiver;

/**
 * Created by Darel Bitsy on 06/02/17.
 */

public class SyncDatabaseService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  Used to name the worker thread, important only for debugging.
     */
    public SyncDatabaseService() {
        super("Database Feed Service");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        new FeedDataInForeground(this).performSync();
        Log.i("DBWEATHER", "Feed data from server ");
        Log.i(ConstantHolder.TAG, "DBweather updated weather data from server");
        SyncDataReceiver.completeWakefulIntent(intent);
    }
}
