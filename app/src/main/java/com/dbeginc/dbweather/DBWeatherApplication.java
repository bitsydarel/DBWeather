package com.dbeginc.dbweather;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.dagger.components.DBWeatherApplicationComponent;
import com.dbeginc.dbweather.dagger.components.DaggerDBWeatherApplicationComponent;
import com.dbeginc.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.github.moduth.blockcanary.BlockCanary;
import com.google.android.gms.ads.MobileAds;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import javax.annotation.Nonnull;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Class representing the Application class
 */

public class DBWeatherApplication extends MultiDexApplication  {

    private static DBWeatherApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        AndroidThreeTen.init(this);
        MobileAds.initialize(this, "ca-app-pub-3786486250382359~1426079826");

        mComponent = DaggerDBWeatherApplicationComponent.builder()
                .dBWeatherApplicationModule(new DBWeatherApplicationModule(this))
                .build();
    }

    public static DBWeatherApplicationComponent getComponent() {
        return mComponent;
    }

}
