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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddLocationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LocationListAdapter mLocationListAdapter;
    private ProgressBar mLocationProgressBar;
    private EditText mSearchEditQuery;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ImageButton mLocationSearchIcon;
    private ImageButton mBackToMainActivity;
    private final View.OnClickListener searchClickListener = view ->
            getUserQuery(mSearchEditQuery.getText().toString());

    private final View.OnClickListener clearTextListener =
            view -> mSearchEditQuery.setText("");

    private void getUserQuery(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            getUserQuery(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    private void getUserQuery(final String query) {
        String userQuery = query;
        try {
            userQuery = URLEncoder.encode(query, "UTF8");
        } catch (UnsupportedEncodingException e) {
            Log.i(ConstantHolder.TAG, "Error while encoding: "
                    + e.getMessage());
        }

        mCompositeDisposable.add(LocationFinderUtility.mockGetLocationInfoFromName(userQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new GetLocationHelper()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mRecyclerView = (RecyclerView) findViewById(R.id.locationRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));

        mSearchEditQuery = (EditText) findViewById(R.id.search_edit_query);
        mLocationSearchIcon = (ImageButton) findViewById(R.id.location_search_icon);
        mBackToMainActivity = (ImageButton) findViewById(R.id.backToMainActivity);

        mLocationProgressBar = (ProgressBar) findViewById(R.id.locationProgressBar);
        mLocationProgressBar.setVisibility(View.GONE);

        getUserQuery("Alajuela");
        getUserQuery(getIntent());
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
                mLocationSearchIcon.setImageResource(R.drawable.close_button_icon);
                mLocationSearchIcon.setOnClickListener(clearTextListener);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getUserQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                mLocationSearchIcon.setImageResource(R.drawable.close_button_icon);
                mLocationSearchIcon.setOnClickListener(searchClickListener);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
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
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in AddLocationActivity: " + e.getMessage());
        }
    }
}
