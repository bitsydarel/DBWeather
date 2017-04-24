package com.darelbitsy.dbweather.models.provider.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

/**
 * Created by Darel Bitsy on 23/04/17.
 */

public class DatabaseUserCitiesRepository implements IUserCitiesRepository {

    private final DatabaseOperation mDatabaseOperation;

    public DatabaseUserCitiesRepository(@NonNull final Context context) {
        mDatabaseOperation = DatabaseOperation.newInstance(context);
    }

    @Override
    public List<GeoName> getUserCities() {
        return mDatabaseOperation.getUserCitiesFromDatabase();
    }
}
