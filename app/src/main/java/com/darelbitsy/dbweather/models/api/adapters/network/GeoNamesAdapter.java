package com.darelbitsy.dbweather.models.api.adapters.network;

import android.content.Context;

import com.darelbitsy.dbweather.models.api.services.GeoNamesService;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoNamesResult;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.USER_LANGUAGE;

/**
 * Created by Darel Bitsy on 03/04/17.
 */

public class GeoNamesAdapter {
    private static final String RESULT_STYLE = "MEDIUM";
    private final List<String> listOfSupportedLanguage = Arrays.asList(
            "cs", "en", "eo", "fi", "he", "iata", "it", "nl", "no",
            "pl", "ru", "uk", "unlc");

    private static final String GEO_NAMES_API_URL= "http://api.geonames.org/";
    private static final String USER_NAME = "bitsydarel";
    private static final boolean IS_NAME_REQUIERED = true;
    private static final int MAX_ROWS = 3;
    private GeoNamesService mGeoNamesService;
    private static GeoNamesAdapter singletonGeoNamesAdapter;

    public static GeoNamesAdapter newInstance(final Context context) {
        if (singletonGeoNamesAdapter == null) {
            singletonGeoNamesAdapter = new GeoNamesAdapter(context.getApplicationContext());
        }
        return singletonGeoNamesAdapter;
    }

    private GeoNamesAdapter(final Context context) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEO_NAMES_API_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(AppUtil
                        .geoNameOkHttpClient
                        .cache(AppUtil.getCacheDirectory(context))
                        .build())
                .build();
        if (mGeoNamesService == null) {
            mGeoNamesService = retrofit.create(GeoNamesService.class);
        }
    }

    public Call<GeoNamesResult> getLocations(final String query) {
        return mGeoNamesService.getLocation(query, USER_NAME, RESULT_STYLE,
                MAX_ROWS, IS_NAME_REQUIERED,
                listOfSupportedLanguage.contains(USER_LANGUAGE) ? USER_LANGUAGE : "en");
    }
}
