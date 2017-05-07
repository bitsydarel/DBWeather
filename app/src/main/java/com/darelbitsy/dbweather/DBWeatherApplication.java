package com.darelbitsy.dbweather;

import android.support.multidex.MultiDexApplication;

import com.darelbitsy.dbweather.dagger.components.DBWeatherApplicationComponent;
import com.darelbitsy.dbweather.dagger.components.DaggerDBWeatherApplicationComponent;
import com.darelbitsy.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.github.moduth.blockcanary.BlockCanary;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Class representing the Application class
 */

public class DBWeatherApplication extends MultiDexApplication {

    private static DBWeatherApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        AndroidThreeTen.init(this);

        mComponent = DaggerDBWeatherApplicationComponent.builder()
                .dBWeatherApplicationModule(new DBWeatherApplicationModule(this))
                .build();

    }

    public static DBWeatherApplicationComponent getComponent() {
        return mComponent;
    }
}
