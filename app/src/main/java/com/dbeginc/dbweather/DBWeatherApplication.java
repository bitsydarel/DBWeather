package com.dbeginc.dbweather;

import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.di.components.DBWeatherApplicationComponent;
import com.dbeginc.dbweather.di.components.DaggerDBWeatherApplicationComponent;
import com.dbeginc.dbweather.di.modules.DBWeatherApplicationModule;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.jakewharton.threetenabp.AndroidThreeTen;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Class representing the Application class
 */

public class DBWeatherApplication extends MultiDexApplication  {

    private static DBWeatherApplicationComponent mComponent;
    private boolean isFirebaseAvailable;
    private FirebaseApp firebaseApp;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        AndroidThreeTen.init(this);
        firebaseApp = FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, "ca-app-pub-3786486250382359~1426079826");
        Stetho.initializeWithDefaults(this);

        mComponent = DaggerDBWeatherApplicationComponent.builder()
                .dBWeatherApplicationModule(new DBWeatherApplicationModule(this))
                .build();

        final int googlePlayServicesAvailable = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(this);

        isFirebaseAvailable = googlePlayServicesAvailable == ConnectionResult.SUCCESS;
    }

    public static DBWeatherApplicationComponent getComponent() {
        return mComponent;
    }

    public boolean isFirebaseAvailable() { return isFirebaseAvailable; }

    public FirebaseApp getFirebaseApp() { return firebaseApp; }
}
