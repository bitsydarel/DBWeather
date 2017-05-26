package com.dbeginc.dbweather.models.provider.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 23/04/17.
 * Local Database implementation
 * of the user cities repository
 */

public class DatabaseUserCitiesRepository implements IUserCitiesRepository {

    private final DatabaseOperation mDatabaseOperation;

    public DatabaseUserCitiesRepository(@NonNull final Context context) {
        mDatabaseOperation = DatabaseOperation.getInstance(context);
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
