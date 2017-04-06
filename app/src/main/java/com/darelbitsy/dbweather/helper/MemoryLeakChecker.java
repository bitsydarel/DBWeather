package com.darelbitsy.dbweather.helper;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Darel Bitsy on 06/04/17.
 * Here to check memory leak in the application
 */

public class MemoryLeakChecker extends android.support.multidex.MultiDexApplication {
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MemoryLeakChecker application = (MemoryLeakChecker) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
    }
}
