package com.dbeginc.dbweather.models.provider.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Scheduler Provider
 */

public class RxSchedulersProvider implements ISchedulersProvider {
    private final Scheduler mNewsScheduler;
    private final Scheduler mWeatherScheduler;
    private final Scheduler mDatabaseTaskScheduler;
    private final Scheduler mUiScheduler;
    private final Scheduler computationThread;
    private static RxSchedulersProvider singletonProvider;

    public static synchronized RxSchedulersProvider getInstance() {
        if (singletonProvider == null) {
            singletonProvider = new RxSchedulersProvider();
        }
        return singletonProvider;
    }

    private RxSchedulersProvider() {
        mNewsScheduler = Schedulers.io();
        mWeatherScheduler = Schedulers.io();
        mDatabaseTaskScheduler = Schedulers.io();
        computationThread = Schedulers.computation();
        mUiScheduler = AndroidSchedulers.mainThread();
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

    @Override
    public Scheduler getUIScheduler() {
        return mUiScheduler;
    }

    @Override
    public Scheduler getComputationThread() {
        return computationThread;
    }
}
