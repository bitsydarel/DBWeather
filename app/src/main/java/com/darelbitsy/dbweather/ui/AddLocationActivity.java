package com.darelbitsy.dbweather.ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.listAdapter.LocationListAdapter;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GeoNamesHelper;
import com.darelbitsy.dbweather.helper.MemoryLeakChecker;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.provider.LocationSuggestionProvider;
import com.darelbitsy.dbweather.model.geonames.GeoName;
import com.darelbitsy.dbweather.model.weather.Daily;
import com.darelbitsy.dbweather.model.weather.Hourly;
import com.darelbitsy.dbweather.model.weather.Weather;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddLocationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LocationListAdapter mLocationListAdapter;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ImageButton mBackToMainActivity;

    private DatabaseOperation mDatabaseOperation;

    private void getUserQuery(final Intent intent) {
        if (intent != null &&
                Intent.ACTION_SEARCH.equals(intent.getAction())) {

            getUserQuery(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    private void getUserQuery(final String query) {
        if (!query.isEmpty()) {
            mCompositeDisposable.add(GeoNamesHelper.newInstance(this)
                    .getLocationFromApi(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetLocationHelper()));
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.add_location_toolbar);
        setSupportActionBar(toolbar);
        mDatabaseOperation = DatabaseOperation.newInstance(this);

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) findViewById(R.id.searchLocationView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnSuggestionListener(new SuggestionListener());

        mRecyclerView = (RecyclerView) findViewById(R.id.locationRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));

        mBackToMainActivity = (ImageButton) findViewById(R.id.backToMainActivity);

        getUserQuery(getIntent());
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBackToMainActivity.setOnClickListener(v -> {
            final DatabaseOperation database = DatabaseOperation.newInstance(this);
            final Weather weather = database.getWeatherData();
            weather.setCurrently(database.getCurrentWeatherFromDatabase());

            weather.setDaily(new Daily());
            weather.getDaily().setData(database.getDailyWeatherFromDatabase());

            weather.setHourly(new Hourly());
            weather.getHourly().setData(database.getHourlyWeatherFromDatabase());

            weather.setAlerts(database.getAlerts());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(ConstantHolder.WEATHER_DATA_KEY, weather);
            intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, database.getNewFromDatabase());
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
        MemoryLeakChecker.getRefWatcher(this);
        super.onDestroy();
    }

    /**
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent received by the edit text
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getUserQuery(intent);
    }

    public class SuggestionListener implements SearchView.OnSuggestionListener {
        @Override
        public boolean onSuggestionSelect(final int position) {
            return true;
        }

        @Override
        public boolean onSuggestionClick(final int position) {
            final GeoName location = LocationSuggestionProvider.mListOfLocation.get(position);

            final DialogInterface.OnClickListener mCancelLocationClick =
                    (dialog, which) -> dialog.cancel();

            final DialogInterface.OnClickListener mAddLocationClick = (dialog, which) ->
                    mDatabaseOperation.addLocationToDatabase(location);

            new AlertDialog.Builder(AddLocationActivity.this)
                    .setMessage(String.format(Locale.getDefault(),
                            AddLocationActivity.this
                                    .getApplicationContext()
                                    .getString(R.string.alert_add_location_text),
                            location.getName()))
                    .setNegativeButton(android.R.string.cancel, mCancelLocationClick)
                    .setPositiveButton(android.R.string.yes, mAddLocationClick)
                    .create()
                    .show();
            return true;
        }
    }

    private class GetLocationHelper extends DisposableSingleObserver<List<GeoName>> {
        /**
         * Notifies the SingleObserver with a single item and that the Single has finished sending
         * push-based notifications.
         * <p>
         * The  Single will not call this method if it calls {@link #onError}.
         *
         * @param listOfLocations the item emitted by the Single, an list of locations
         */
        @Override
        public void onSuccess(final List<GeoName> listOfLocations) {
            if (mLocationListAdapter != null) {
                mLocationListAdapter.updateLocationList(listOfLocations);
            } else {
                mLocationListAdapter = new LocationListAdapter(listOfLocations);
                mRecyclerView.setAdapter(mLocationListAdapter);
            }
        }

        /**
         * Notifies the SingleObserver that the Single has experienced an error condition.
         * <p>
         * If the Single calls this method, it will not thereafter call #onSuccess.
         *
         * @param e the exception encountered by the Single
         */
        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in AddLocationActivity: " + e.getMessage());
        }
    }
}