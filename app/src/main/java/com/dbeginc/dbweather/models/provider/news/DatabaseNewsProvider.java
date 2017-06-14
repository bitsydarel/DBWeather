package com.dbeginc.dbweather.models.provider.news;

import android.content.Context;

import com.dbeginc.dbweather.models.datatypes.news.Sources;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.models.datatypes.news.Article;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * This class represent an Database News Provider
 */

@Singleton
public class DatabaseNewsProvider implements INewsProvider {

    private final DatabaseOperation mDatabaseOperation;

    @Inject
    public DatabaseNewsProvider(final Context context) {
        mDatabaseOperation = DatabaseOperation.getInstance(context.getApplicationContext());
    }

    @Override
    public Single<Sources> getSourcesList() {
        return null;
    }

    @Override
    public Single<List<Article>> getNews() {
        return Single.fromCallable(mDatabaseOperation::getNewFromDatabase);
    }
}
