package com.darelbitsy.dbweather.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.listAdapter.LocationListAdapter;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.LocationFinderUtility;
import com.darelbitsy.dbweather.model.weather.Daily;
import com.darelbitsy.dbweather.model.weather.Hourly;
import com.darelbitsy.dbweather.model.weather.Weather;

import org.geonames.Toponym;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddLocationActivity extends AppCompatActivity {

    private RecyclerView cityAndCountryLocation;
    private LocationListAdapter mLocationListAdapter;
    private ProgressBar mLocationProgressBar;
    private EditText mSearchEditQuery;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ImageButton mLocationSearchIcon;
    private ImageButton mBackToMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        cityAndCountryLocation = (RecyclerView) findViewById(R.id.locationRecyclerView);
        cityAndCountryLocation.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));
        mSearchEditQuery = (EditText) findViewById(R.id.search_edit_query);
        mLocationSearchIcon = (ImageButton) findViewById(R.id.location_search_icon);
        mBackToMainActivity = (ImageButton) findViewById(R.id.backToMainActivity);

        mLocationProgressBar = (ProgressBar) findViewById(R.id.locationProgressBar);
        mLocationProgressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mCompositeDisposable.add(LocationFinderUtility.getLocationInfoFromName(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetLocationHelper()));

        } else {
            mCompositeDisposable.add(LocationFinderUtility.getLocationInfoFromName("Alajuela")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new GetLocationHelper()));
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mBackToMainActivity.setOnClickListener(v -> {
            DatabaseOperation database = new DatabaseOperation(this);
            Weather weather = database.getWeatherData();
            weather.setCurrently(database.getCurrentWeatherFromDatabase());

            weather.setDaily(new Daily());
            weather.getDaily().setData(database.getDailyWeatherFromDatabase());

            weather.setHourly(new Hourly());
            weather.getHourly().setData(database.getHourlyWeatherFromDatabase());

            weather.setAlerts(database.getAlerts());
            Intent intent = new Intent(AddLocationActivity.this, MainActivity.class);
            intent.putExtra(ConstantHolder.WEATHER_DATA_KEY, weather);
            intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, database.getNewFromDatabase());
            startActivity(intent);
            finish();
        });

        mSearchEditQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mLocationSearchIcon.setBackground(VectorDrawableCompat.create(getResources(), R.drawable.close_button_icon, getTheme()));
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCompositeDisposable.add(LocationFinderUtility.getLocationInfoFromName(s.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new GetLocationHelper()));
            }
            @Override
            public void afterTextChanged(Editable s) {
                mLocationSearchIcon.setBackground(VectorDrawableCompat.create(getResources(), R.drawable.close_button_icon, getTheme()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    private class GetLocationHelper extends DisposableSingleObserver<List<Toponym>> {
        /**
         * Notifies the SingleObserver with a single item and that the Single has finished sending
         * push-based notifications.
         * <p>
         * The  Single will not call this method if it calls {@link #onError}.
         *
         * @param listOfLocations the item emitted by the Single, an list of locations
         */
        @Override
        public void onSuccess(List<Toponym> listOfLocations) {
            if (mLocationListAdapter != null) {
                mLocationListAdapter.updateLocationList(listOfLocations);

            } else {
                mLocationListAdapter = new LocationListAdapter(listOfLocations);
                cityAndCountryLocation.setAdapter(mLocationListAdapter);
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
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in AddLocationActivity: " + e.getMessage());
        }
    }
}
