package com.darelbitsy.dbweather.utils.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;

import java.util.ArrayList;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.TAG;

/**
 * Created by Darel Bitsy on 27/02/17.
 * Service that handle saving newses
 * in the database
 */

public class NewsDatabaseService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NewsDatabaseService() {
        super(NewsDatabaseService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.i(TAG, "In NewsDatabase Service");
        final ArrayList<Article> newses = intent.getParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY);
        if (newses != null) { DatabaseOperation.getInstance(this).saveNewses(newses); }
        Log.i(TAG, "Saved Weather Info");
    }
}
