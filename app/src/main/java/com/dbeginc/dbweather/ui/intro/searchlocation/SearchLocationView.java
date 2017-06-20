package com.dbeginc.dbweather.ui.intro.searchlocation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

/**
 * Created by darel on 15.06.17.
 */

interface SearchLocationView {

    void loadLocation(@NonNull final List<GeoName> locations);

    void setupLocationLookupFeature();

    Context getContext();

    void onLocationSelected();

    void setQuery(@NonNull final String query);
}
