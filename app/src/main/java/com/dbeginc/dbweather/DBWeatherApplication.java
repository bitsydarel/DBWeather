package com.dbeginc.dbweather;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.dagger.components.DBWeatherApplicationComponent;
import com.dbeginc.dbweather.dagger.components.DaggerDBWeatherApplicationComponent;
import com.dbeginc.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.github.moduth.blockcanary.BlockCanary;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.leakcanary.LeakCanary;

import javax.annotation.Nonnull;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Class representing the Application class
 */

public class DBWeatherApplication extends MultiDexApplication  implements ConnectionClassManager.ConnectionClassStateChangeListener {

    private static DBWeatherApplicationComponent mComponent;
    private ConnectionQuality mConnectionQuality = ConnectionQuality.UNKNOWN;

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

        mComponent = DaggerDBWeatherApplicationComponent.builder()
                .dBWeatherApplicationModule(new DBWeatherApplicationModule(this))
                .build();

        ConnectionClassManager.getInstance().register(this);
        DeviceBandwidthSampler.getInstance().startSampling();
    }

    public static DBWeatherApplicationComponent getComponent() {
        return mComponent;
    }

    @Override
    public void onBandwidthStateChange(@Nonnull final ConnectionQuality bandwidthState) {
        mConnectionQuality = bandwidthState;
    }

    public ConnectionQuality getConnectionQuality() {
        return mConnectionQuality;
    }
}
