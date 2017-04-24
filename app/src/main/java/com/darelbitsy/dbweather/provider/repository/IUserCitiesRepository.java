package com.darelbitsy.dbweather.models.provider.repository;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

/**
 * Created by Darel Bitsy on 23/04/17.
 */

public interface IUserCitiesRepository {

    List<GeoName> getUserCities();
}
