package com.darelbitsy.dbweather.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.model.news.News;

import java.util.ArrayList;

/**
 * Created by Darel Bitsy on 27/02/17.
 * Service that handle saving newses
 * in the database
 */

public class NewsDatabaseService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<News> newses = intent.getParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY);
        new DatabaseOperation(this).saveNewses(newses);
        return START_NOT_STICKY;
    }
}
