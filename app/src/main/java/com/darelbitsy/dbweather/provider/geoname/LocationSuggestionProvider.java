package com.darelbitsy.dbweather.provider.geoname;

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

import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

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
    private GeoNameLocationInfoProvider mGeoNameLocationInfoProvider;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    public static final List<GeoName> mListOfLocation = new ArrayList<>();

    @Override
    public boolean onCreate() {
        mGeoNameLocationInfoProvider = new GeoNameLocationInfoProvider(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull final Uri uri,
                        @Nullable final String[] projection,
                        @Nullable final String selection,
                        @Nullable final String[] selectionArgs,
                        @Nullable final String sortOrder) {

        final String userQuery = uri.getLastPathSegment();

        final MatrixCursor matrixCursor = new MatrixCursor(new String[] {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2
        }, mListOfLocation.size());

        if (userQuery != null && !userQuery.isEmpty()) {
            mCompositeDisposable.add(mGeoNameLocationInfoProvider
                    .getLocation(userQuery)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetSuggestions()));

            final int mListOfLocation_size = mListOfLocation.size();

            for (int index = 0; index < mListOfLocation_size; index ++) {
                final GeoName location = mListOfLocation.get(index);

                matrixCursor.addRow(new Object[] {index, location.getName(), location.getCountryName()});
            }
        }

        return matrixCursor;
    }

    private class GetSuggestions extends DisposableSingleObserver<List<GeoName>> {
        @Override
        public void onSuccess(final List<GeoName> geoNames) {
            mListOfLocation.clear();
            mListOfLocation.addAll(geoNames);
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in location provider: " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull final Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull final Uri uri,
                      @Nullable final String selection,
                      @Nullable final String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull final Uri uri,
                      @Nullable final ContentValues values,
                      @Nullable final String selection,
                      @Nullable final String[] selectionArgs) {
        return 0;
    }
}
