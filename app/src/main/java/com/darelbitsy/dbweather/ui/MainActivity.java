package com.darelbitsy.dbweather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.darelbitsy.dbweather.adapters.CustomFragmentAdapter;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.api.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.news.News;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static com.darelbitsy.dbweather.helper.ConstantHolder.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.darelbitsy.dbweather.helper.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.darelbitsy.dbweather.helper.ConstantHolder.MY_PERMiSSIONS_REQUEST_GET_ACCOUNT;

/**
 * Created by Darel Bitsy on 11/02/17.
 */

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private VerticalViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private ArrayList<News> mNewses;
    private Weather mWeather;
    private DatabaseOperation mDatabase;
    private CustomFragmentAdapter mFragmentAdapter;
    private BroadcastReceiver mLocationBroadcast;

    private class GetWeather extends GetWeatherHelper {

        GetWeather(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Weather weather) {
            mWeather = weather;
            if (mFragmentAdapter != null) {
                mFragmentAdapter.updateWeatherOnFragment(weather.getCurrently(),
                        weather.getDaily(),
                        weather.getCityName());
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = new DatabaseOperation(this);

        //Configuring the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = createLocationRequest();

        AppUtil.askLocationPermIfNeeded(this);
        AppUtil.askAccountInfoPermIfNeeded(this);

        Bundle extras = getIntent().getExtras();
        mWeather = extras.getParcelable(ConstantHolder.WEATHER_DATA_KEY);
        mNewses = extras.getParcelableArrayList(ConstantHolder.NEWS_DATA_KEY);
        mViewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        mFragmentAdapter = new CustomFragmentAdapter(getFragmentManager(), mWeather, mNewses);
        mViewPager.setAdapter(mFragmentAdapter);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationBroadcast == null) {
            mLocationBroadcast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    WeatherUtil.saveCoordinates(intent.getExtras().getDouble("latitude"),
                            intent.getExtras().getDouble("longitude"));
                    new GetWeather(MainActivity.this).execute();
                }
            };

        }
        registerReceiver(mLocationBroadcast, new IntentFilter("dbweather_location_update"));
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationBroadcast != null) { unregisterReceiver(mLocationBroadcast); }

        mDatabase.saveWeatherData(mWeather);

        if (mWeather.getAlerts() != null) { mDatabase.saveAlerts(mWeather.getAlerts()); }
        if (mWeather.getMinutely() != null) {
            mDatabase.saveMinutelyWeather(mWeather
                .getMinutely()
                .getData()) ;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ConstantHolder.isGpsPermissionOn)
                &&
                LocationServices.FusedLocationApi
                        .getLocationAvailability(mGoogleApiClient)
                        .isLocationAvailable()) {

            getLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Empty for now, will be implemented later
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
                Log.e(ConstantHolder.TAG, " Following error occure: ", e);
            }
        } else {
            // if no resolution found , display a dialog to the user with error.
            Log.i(ConstantHolder.TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        WeatherUtil.saveCoordinates(location.getLatitude(), location.getLongitude());

        new Handler().post(new GetWeather(this)::execute);
        Log.i(ConstantHolder.TAG, "City Name: "+ mWeather.getCityName());
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Checking if the user cancelled, the permission
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            ConstantHolder.isGpsPermissionOn = true;
            mLocationRequest = createLocationRequest();
            getLocation();

        }

        if (requestCode == MY_PERMiSSIONS_REQUEST_GET_ACCOUNT
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            ConstantHolder.isAccountPermissionOn = true;

        }


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
            WeatherUtil.saveCoordinates(location.getLatitude(), location.getLongitude());
            runOnUiThread(new GetWeather(this)::execute);
            Log.i(ConstantHolder.TAG, "City Name: "+mWeather.getCityName());
        }
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((long)(1200) * 1000) // Seconds, in milliseconds
                .setFastestInterval(1000); // 1 Seconds, in milliseconds
    }
}