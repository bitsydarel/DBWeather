package com.dbeginc.dbweather.ui.main.config.fragments;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

/**
 * Created by darel on 10.06.17.
 */

interface ILocationManagerView {
    void removeLocation(@NonNull final GeoName location);
}
