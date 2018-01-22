package com.dbeginc.dbweather;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.jakewharton.threetenabp.AndroidThreeTen;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Class representing the Application class
 */

public class DBWeatherApplication extends MultiDexApplication  {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        AndroidThreeTen.init(this);

        FirebaseApp.initializeApp(this);

        MobileAds.initialize(this, "ca-app-pub-3786486250382359~1426079826");
    }
}
