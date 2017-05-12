package com.darelbitsy.dbweather.utils.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.utils.helper.FeedDataInForeground;
import com.darelbitsy.dbweather.utils.helper.AlarmConfigHelper;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmConfigOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {
            new AlarmConfigHelper(context).setClothingNotificationAlarm();
            Log.i("Feed_Data", "Resetting data on reboot");
            FeedDataInForeground.setNextSync(context);
            Log.i("Feed_Data", "Setted the hourly sync from Boot Up");
        }
    }
}
