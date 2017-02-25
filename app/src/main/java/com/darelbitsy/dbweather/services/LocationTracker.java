package com.darelbitsy.dbweather.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.darelbitsy.dbweather.helper.ConstantHolder;

/**
 * Created by Darel Bitsy on 23/02/17.
 */

public class LocationTracker extends Service {
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(ConstantHolder.TAG, "In the service location , the location just changed");
                Intent coordonateIntent = new Intent("dbweather_location_update");
                coordonateIntent.putExtra("latitude", location.getLatitude());
                coordonateIntent.putExtra("longitude", location.getLongitude());
                sendBroadcast(coordonateIntent);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent openGpsSetting = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                openGpsSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openGpsSetting);
            }
        };

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener);

        }
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener);

        }
        Log.i(ConstantHolder.TAG, "Registered request Location update ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
