package com.darelbitsy.dbweather.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.receiver.SyncDataReceiver;

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
    protected void onHandleIntent(Intent intent) {
        new FeedDataInForeground(this).performSync();
        Log.i("DBWEATHER", "Feed data from server ");
        Toast.makeText(this, "DBweather updated weather data from server", Toast.LENGTH_LONG).show();
        SyncDataReceiver.completeWakefulIntent(intent);
    }
}
