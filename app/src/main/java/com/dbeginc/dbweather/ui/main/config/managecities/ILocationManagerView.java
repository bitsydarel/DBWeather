package com.dbeginc.dbweather.ui.main.config.managecities;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

/**
 * Created by darel on 10.06.17.
 */

interface ILocationManagerView {
    void removeLocation(@NonNull final GeoName location);
}
