package com.darelbitsy.dbweather.ui;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.adapters.CustomFragmentAdapter;
import com.darelbitsy.dbweather.helper.GetWeatherData;
import com.darelbitsy.dbweather.news.News;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Darel Bitsy on 11/02/17.
 */

public class MainActivity1 extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static String jsonData;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;

    public static final String JSON_DATA = "json_data";
    public static final String CURRENT_WEATHER_KEY = "current_weather_key";
    public static final String HOURS_WEATHER_KEY = "hours_weather_key";
    public static final String DAYS_WEATHER_KEY = "days_weather_key";
    public static final String WEATHER_DATA_KEY = "weather_data_key";

    private VerticalViewPager mViewPager;
    private WeatherApi mWeatherApi;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mIsGpsPermissionOn;

    private GetWeatherData mWeatherData;
    private News[] mNewses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        mWeatherData = new GetWeatherData(this);

        mLocationRequest = createLocationRequest();

        //Configuring the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mIsGpsPermissionOn = false;

        mWeatherApi = getIntent().getParcelableExtra(WelcomeActivity.WEATHER_DATA_KEY);
        mNewses = (News[]) getIntent().getParcelableArrayExtra(WelcomeActivity.NEWS_DATA_KEY);

        mViewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new CustomFragmentAdapter(getFragmentManager(), mWeatherApi, mNewses));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mIsGpsPermissionOn) {
            if(LocationServices.FusedLocationApi
                    .getLocationAvailability(mGoogleApiClient)
                    .isLocationAvailable()) { getLocation(); }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /* trying to send a Intent to start a google play services activity that can resolve the error
        * if occure
        */
        if (connectionResult.hasResolution()) {
            try {
                // starting a activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(MainActivity.TAG, " Following error occure: ", e);
            }
        } else {
            // if no resolution found , display a dialog to the user with error.
            Log.i(MainActivity.TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mWeatherData.setLatitude(location.getLatitude());
        mWeatherData.setLongitude(location.getLongitude());
        new Handler().post(mWeatherData::execute);
        mWeatherApi.getCurrent()
                .setCityName(mWeatherData.getLocationName(location.getLatitude(),
                        location.getLongitude()));
        Log.i(MainActivity.TAG, "City Name: "+mWeatherApi.getCurrent().getCityName());
        mWeatherApi = mWeatherData.getWeatherApi();
    }



    /**
     * get last know location
     * if not available request location update
     */
    private void getLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            mWeatherData.setLatitude(location.getLatitude());
            mWeatherData.setLongitude(location.getLongitude());
            runOnUiThread(new GetWeatherData(this)::execute);
            mWeatherApi.getCurrent()
                    .setCityName(mWeatherData.getLocationName(location.getLatitude(),
                    location.getLongitude()));
            Log.i(MainActivity.TAG, "City Name: "+mWeatherApi.getCurrent().getCityName());
        }
    }


    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((long)(1200) * 1000) // Seconds, in milliseconds
                .setFastestInterval(1000); // 1 Seconds, in milliseconds
    }
}