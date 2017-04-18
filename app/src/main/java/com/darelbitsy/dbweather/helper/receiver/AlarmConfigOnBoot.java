package com.darelbitsy.dbweather.helper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.presenter.FeedDataInForeground;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;

/**
 * Created by Darel Bitsy on 30/01/17.
 */

public class AlarmConfigOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new AlarmConfigHelper(context).setClothingNotificationAlarm();
        Log.i("Feed_Data", "Resetting data on reboot");
        FeedDataInForeground.setNextSync(context);
        Log.i("Feed_Data", "Setted the hourly sync from Boot Up");
    }
}
