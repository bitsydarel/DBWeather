package com.darelbitsy.dbweather.models.provider.schedulers;

import io.reactivex.Scheduler;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface ISchedulersProvider {

    Scheduler getWeatherScheduler();

    Scheduler getNewsScheduler();

    Scheduler getDatabaseWorkScheduler();

    Scheduler getUIScheduler();
}
