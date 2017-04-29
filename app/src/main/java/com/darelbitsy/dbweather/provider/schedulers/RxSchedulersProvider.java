package com.darelbitsy.dbweather.provider.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Scheduler Provider
 */

public class RxSchedulersProvider implements ISchedulersProvider {
    private final Scheduler mNewsScheduler;
    private final Scheduler mWeatherScheduler;
    private final Scheduler mDatabaseTaskScheduler;
    private static RxSchedulersProvider singletonProvider;

    public static RxSchedulersProvider newInstance() {
        if (singletonProvider == null) {
            singletonProvider = new RxSchedulersProvider();
        }
        return singletonProvider;
    }

    private RxSchedulersProvider() {
        mNewsScheduler = Schedulers.io();
        mWeatherScheduler = Schedulers.io();
        mDatabaseTaskScheduler = Schedulers.io();
    }

    @Override
    public Scheduler getWeatherScheduler() {
        return mWeatherScheduler;
    }

    @Override
    public Scheduler getNewsScheduler() {
        return mNewsScheduler;
    }

    @Override
    public Scheduler getDatabaseWorkScheduler() { return mDatabaseTaskScheduler; }


}
