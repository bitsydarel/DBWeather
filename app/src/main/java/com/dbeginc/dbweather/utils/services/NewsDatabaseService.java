package com.dbeginc.dbweather.utils.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import java.util.ArrayList;

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
        final ArrayList<Article> newses = intent.getParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY);
        if (newses != null) { DatabaseOperation.getInstance(this).saveNewses(newses); }
    }
}
