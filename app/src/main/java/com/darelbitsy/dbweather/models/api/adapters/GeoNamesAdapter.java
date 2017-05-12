package com.darelbitsy.dbweather.models.api.adapters;

import com.darelbitsy.dbweather.models.api.services.GeoNamesService;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoNamesResult;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.USER_LANGUAGE;

/**
 * Created by Darel Bitsy on 03/04/17.
 * GeoName Retrofit Adapter that provide
 * Access to the GeoName Api
 */

@Singleton
public class GeoNamesAdapter {
    private static final String RESULT_STYLE = "MEDIUM";
    private static final String GEO_NAMES_API_URL= "http://api.geonames.org/";
    private static final String USER_NAME = "bitsydarel";
    private static final boolean IS_NAME_REQUIRED = true;
    private static final int MAX_ROWS = 3;

    private final List<String> listOfSupportedLanguage = Arrays.asList(
            "cs", "en", "eo", "fi", "he", "iata", "it", "nl", "no",
            "pl", "ru", "uk", "unlc");
    private final GeoNamesService mGeoNamesService;

    @Inject
    public GeoNamesAdapter(final OkHttpClient okHttpClient) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEO_NAMES_API_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(okHttpClient)
                .build();

        mGeoNamesService = retrofit.create(GeoNamesService.class);
    }

    public Call<GeoNamesResult> getLocations(final String query) {
        return mGeoNamesService.getLocation(query, USER_NAME, RESULT_STYLE,
                MAX_ROWS, IS_NAME_REQUIRED,
                listOfSupportedLanguage.contains(USER_LANGUAGE) ? USER_LANGUAGE : "en");
    }
}
