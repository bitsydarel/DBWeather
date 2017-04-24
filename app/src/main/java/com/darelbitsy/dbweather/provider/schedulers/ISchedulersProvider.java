package com.darelbitsy.dbweather.provider.schedulers;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface ISchedulersProvider<SCHEDULER_TYPE> {

    SCHEDULER_TYPE getWeatherScheduler();

    SCHEDULER_TYPE getNewsScheduler();

    SCHEDULER_TYPE getDatabaseWorkScheduler();
}
