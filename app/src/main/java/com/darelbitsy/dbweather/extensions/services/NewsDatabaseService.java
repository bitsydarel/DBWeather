package com.darelbitsy.dbweather.models.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.news.Article;

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
        ArrayList<Article> newses = intent.getParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY);
        DatabaseOperation.newInstance(this).saveNewses(newses);
        stopSelf();
        return START_NOT_STICKY;
    }
}
