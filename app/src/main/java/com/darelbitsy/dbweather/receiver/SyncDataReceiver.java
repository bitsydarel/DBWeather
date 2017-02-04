package com.darelbitsy.dbweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.darelbitsy.dbweather.adapters.FeedDataInForeground;

/**
 * Created by Darel Bitsy on 04/02/17.
 */
public class SyncDataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new FeedDataInForeground(context).performSync();
        Log.i("DBWEATHER", "Feed data from server ");
        Toast.makeText(context, "DBweather updated weather data from server", Toast.LENGTH_LONG).show();
        FeedDataInForeground.setNextSync(context);
    }
}
