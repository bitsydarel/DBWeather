package com.darelbitsy.dbweather.helper.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.darelbitsy.dbweather.controller.api.adapters.helper.GeoNamesHelper;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.model.geonames.GeoName;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Darel Bitsy on 04/04/17.
 * Location Suggestion provider
 */

public class LocationSuggestionProvider extends ContentProvider {
    private GeoNamesHelper mGeoNamesHelper;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    public static final List<GeoName> mListOfLocation = new ArrayList<>();

    @Override
    public boolean onCreate() {
        mGeoNamesHelper = GeoNamesHelper.newInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final String userQuery = uri.getLastPathSegment();

        final MatrixCursor matrixCursor = new MatrixCursor(new String[] {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2
        }, mListOfLocation.size());

        if (userQuery != null && !userQuery.isEmpty()) {
            mCompositeDisposable.add(mGeoNamesHelper.getLocationFromApi(userQuery)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetSuggestions()));

            for (int index = 0; index < mListOfLocation.size(); index ++) {
                GeoName location = mListOfLocation.get(index);

                matrixCursor.addRow(new Object[] {index, location.getName(), location.getCountryName()});
            }
        }

        return matrixCursor;
    }

    private class GetSuggestions extends DisposableSingleObserver<List<GeoName>> {
        @Override
        public void onSuccess(List<GeoName> geoNames) {
            mListOfLocation.clear();
            mListOfLocation.addAll(geoNames);
        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in location provider: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
