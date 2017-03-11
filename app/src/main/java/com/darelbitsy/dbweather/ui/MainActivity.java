package com.darelbitsy.dbweather.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.CustomFragmentAdapter;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.api.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.services.LocationTracker;
import com.darelbitsy.dbweather.services.WeatherDatabaseService;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.helper.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.darelbitsy.dbweather.helper.ConstantHolder.MY_PERMiSSIONS_REQUEST_GET_ACCOUNT;

/**
 * Created by Darel Bitsy on 11/02/17.
 * MainActivity of the application
 * Handle location update and set viewPager
 */

public class MainActivity extends FragmentActivity {

    private DatabaseOperation mDatabase;
    private CustomFragmentAdapter mFragmentAdapter;
    private BroadcastReceiver mLocationBroadcast;
    private Single<Weather> mWeatherObservable;
    public static final CompositeDisposable subscriptions = new CompositeDisposable();
    private final Handler mUpdateHandler = new Handler();

    /**
     * This class implement the behavior
     * i want when i receive the weather data
     */
    private final class MainActivityWeatherObserver extends DisposableSingleObserver<Weather> {
        @Override
        public void onSuccess(Weather weather) {
            Log.i(ConstantHolder.TAG, "Inside the weatherObserver MainActivity");

            if (mFragmentAdapter != null) {
                Log.i(ConstantHolder.TAG, "Inside: fragmentAdapter not null");
                mUpdateHandler.post(() -> mFragmentAdapter.updateWeatherOnFragment(weather.getCurrently(),
                        weather.getDaily(),
                        weather.getCityName()));

            } else {
                Log.i(ConstantHolder.TAG, "Inside: fragmentAdapter null");
            }

            startService(new Intent(MainActivity.this, WeatherDatabaseService.class)
                    .putExtra(ConstantHolder.WEATHER_DATA_KEY, weather));

            Log.i(ConstantHolder.TAG, "City Name: "+ weather.getCityName());

        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error: " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = new DatabaseOperation(this);

        mWeatherObservable = new GetWeatherHelper(this)
                .getObservableWeatherFromApi(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        AppUtil.askLocationPermIfNeeded(this);
        AppUtil.askAccountInfoPermIfNeeded(this);

        Bundle extras = getIntent().getExtras();

        VerticalViewPager viewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        mFragmentAdapter = new CustomFragmentAdapter(getFragmentManager(),
                extras.getParcelable(ConstantHolder.WEATHER_DATA_KEY),
                extras.getParcelableArrayList(ConstantHolder.NEWS_DATA_KEY));

        viewPager.setAdapter(mFragmentAdapter);

        if (mLocationBroadcast == null) {
            mLocationBroadcast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    WeatherUtil.saveCoordinates(intent.getExtras().getDouble("latitude"),
                            intent.getExtras().getDouble("longitude"),
                            mDatabase);

                    subscriptions.add(mWeatherObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new MainActivityWeatherObserver()));
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mLocationBroadcast,
                new IntentFilter("dbweather_location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.dispose();
        if (mLocationBroadcast != null) {
            unregisterReceiver(mLocationBroadcast);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Checking if the user cancelled, the permission
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            AppUtil.setGpsPermissionValue(this);
            startService(new Intent(this, LocationTracker.class));

        }

        if (requestCode == MY_PERMiSSIONS_REQUEST_GET_ACCOUNT
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            AppUtil.setAccountPermissionValue(this);

        }
    }

}