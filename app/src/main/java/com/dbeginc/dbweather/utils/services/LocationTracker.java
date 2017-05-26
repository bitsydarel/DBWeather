package com.dbeginc.dbweather.utils.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.weather.Hourly;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.TimeZone;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_COORDINATE_INSERTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_GPS_PERMISSION_GRANTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 23/02/17.
 * Service that get the user location
 * request user location
 */

@SuppressWarnings("MissingPermission")
public class LocationTracker extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        LocationListener {

    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private Disposable disposable;
    private SharedPreferences sharedPreferences;
    private DatabaseOperation database;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        //Configuring the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        mGoogleApiClient.connect();
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (sharedPreferences == null) {
            sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            database = DatabaseOperation.getInstance(getApplicationContext());
        }

        final boolean isGpsPermissionOn = sharedPreferences
                .getBoolean(IS_GPS_PERMISSION_GRANTED, false);

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isGpsPermissionOn) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    300000,
                    0,
                    this);

            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        300000,
                        0,
                        this);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if (mLocationManager != null) { mLocationManager.removeUpdates(this); }
        if (disposable != null) { disposable.dispose(); }
    }

    //Should not be call but was called in API 4.3
    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if (mLocationManager != null) { mLocationManager.removeUpdates(this); }
        if (disposable != null) { disposable.dispose(); }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if (mLocationManager != null) { mLocationManager.removeUpdates(this); }
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) { getLocation(); }

    @Override
    public void onConnectionSuspended(final int i) {
        //Empty for now, will be implemented later if needed
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        //Leaving this empty because if not available we will use the gps card
    }

    @Override
    public void onLocationChanged(final Location location) { sendLocationToActivity(location); }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        //Not Using it for now
    }

    @Override
    public void onProviderEnabled(final String provider) {
        //Not using it for now
        if (mLocationManager != null) {
            mLocationManager.requestLocationUpdates(provider,
                    300000,
                    0,
                    this);
        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
        final Intent openGpsSetting = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        openGpsSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(openGpsSetting);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    /**
     * get last know location
     * if not available request location update
     */
    private void getLocation() {
        final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(),  this);

        } else {
            Log.i(ConstantHolder.TAG, "Inside the getLocation method, i find location");
            sendLocationToActivity(location);
        }
    }


    /**
     * Method that create an LocationRequest
     * with specific parameter
     * @return LocationRequest
     */
    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(300000) // Seconds, in milliseconds
                .setFastestInterval(1000); // 1 Seconds, in milliseconds
    }

    /**
     * Send location details to activity
     * by an broadcast request
     * @param location represent the user location
     */
    private void sendLocationToActivity(final Location location) {
        final Intent coordinateIntent = new Intent(LOCATION_UPDATE);

        if (!sharedPreferences.getBoolean(IS_COORDINATE_INSERTED, false)) {
            final Weather weather = new Weather();
            weather.setCityName("UNKNOWN");
            weather.setTimezone(TimeZone.getDefault().getID());
            weather.setLatitude(34.0549);
            weather.setLongitude(-118.2445);
            weather.setHourly(new Hourly());
            weather.getHourly().setSummary("UNKNOWN");

            Completable.create(completableEmitter -> {
                database.saveWeatherData(weather);
                completableEmitter.onComplete();

            }).subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .doOnComplete(() -> database.saveCoordinatesAsync(location.getLatitude(), location.getLongitude())
                            .subscribeWith(new DisposableCompletableObserver() {
                                @Override
                                public void onComplete() {
                                    sendBroadcast(coordinateIntent);
                                    sharedPreferences.edit().putBoolean(IS_COORDINATE_INSERTED, true).apply();
                                }
                                @Override
                                public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                            }))
                    .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .observeOn(schedulersProvider.getUIScheduler())
                    .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .subscribe();

        } else {
            disposable = database
                    .saveCoordinatesAsync(location.getLatitude(), location.getLongitude())
                    .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .observeOn(schedulersProvider.getUIScheduler())
                    .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() { sendBroadcast(coordinateIntent); }
                        @Override
                        public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                    });
        }
    }

}
