package com.darelbitsy.dbweather;

import android.support.multidex.MultiDexApplication;

import com.darelbitsy.dbweather.dagger.components.DBWeatherApplicationComponent;
import com.darelbitsy.dbweather.dagger.components.DaggerDBWeatherApplicationComponent;
import com.darelbitsy.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Class representing the Application class
 */

public class DBWeatherApplication extends MultiDexApplication {

    private static DBWeatherApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        mComponent = DaggerDBWeatherApplicationComponent.builder()
                .dBWeatherApplicationModule(new DBWeatherApplicationModule(this))
                .build();

    }

    public static DBWeatherApplicationComponent getComponent() {
        return mComponent;
    }
}
