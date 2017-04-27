package com.darelbitsy.dbweather.provider.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.provider.schedulers.RxSchedulersProvider;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 23/04/17.
 * Local Database implementation
 * of the user cities repository
 */

public class DatabaseUserCitiesRepository implements IUserCitiesRepository {

    private final DatabaseOperation mDatabaseOperation;

    public DatabaseUserCitiesRepository(@NonNull final Context context) {
        mDatabaseOperation = DatabaseOperation.newInstance(context);
    }

    @Override
    public Single<List<GeoName>> getUserCities() {
        return Single.fromCallable(mDatabaseOperation::getUserCitiesFromDatabase);
    }

    @Override
    public void removeCity(@NonNull final GeoName location) {
        mDatabaseOperation.removeLocationFromDatabase(location);
    }
}