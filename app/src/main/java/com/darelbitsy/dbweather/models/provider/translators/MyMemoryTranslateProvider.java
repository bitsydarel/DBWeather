package com.darelbitsy.dbweather.models.provider.translators;

import android.support.annotation.NonNull;
import android.util.Log;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.models.api.adapters.MyMemoryTranslateRestAdapter;
import com.darelbitsy.dbweather.models.datatypes.news.MyMemoryJson;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

/**
 * Created by Darel Bitsy on 22/04/17.
 * MyMemoryTranslate api Provider
 */

@Singleton
public class MyMemoryTranslateProvider implements ITranslateProvider {

    @Inject MyMemoryTranslateRestAdapter mMyMemoryTranslateRestAdapter;

    private final String languagePair = String.format(Locale.ENGLISH,
            "en|%s",
            ConstantHolder.USER_LANGUAGE);

    @Inject
    public MyMemoryTranslateProvider() {
        DBWeatherApplication.getComponent().inject(this);
    }

    @Override
    public String translateText(@NonNull final String text) {
        final Response<MyMemoryJson> response;
        try {
            response = mMyMemoryTranslateRestAdapter.translateText(text,
                    languagePair)
                    .execute();

            if (response.isSuccessful()) {
                return response.body().getResponseData().getTranslatedText();
            }

        } catch (final IOException e) {
            Log.i(ConstantHolder.TAG, "Error : " + e.getMessage());
        }

        return text;
    }

    public String translateText(@NonNull final String text, final String email) {

        final Response<MyMemoryJson> response;

        try {
            response = mMyMemoryTranslateRestAdapter.translateText(text,
                    languagePair, email)
                    .execute();

            if (response.isSuccessful()) {
                return response.body().getResponseData().getTranslatedText();
            }

        } catch (final IOException e) {
            Log.i(ConstantHolder.TAG, "Error : " + e.getMessage());
        }
        return text;
    }
}
