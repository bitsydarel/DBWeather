package com.darelbitsy.dbweather.controller.api.adapters.network;

import android.util.Log;

import com.darelbitsy.dbweather.controller.api.services.TranslateService;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.MyMemoryJson;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 19/02/17.
 * Translate api adapter
 */

public class TranslateRestAdapter {
    private final TranslateService mTranslateService;

    public TranslateRestAdapter() {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(ConstantHolder.MYMEMORY_APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(AppUtil.translateOkHttpClient)
                .build();

        mTranslateService = restAdapter.create(TranslateService.class);
    }

    public String translateText(final String textToTranslate,
                                final String email) {

        final String languagePair = String.format(Locale.ENGLISH,
                "en|%s",
                ConstantHolder.USER_LANGUAGE);

        final Response<MyMemoryJson> response;

        try {
            if (!"".equals(email)) {
                response = mTranslateService.getTranslatedText(textToTranslate,
                        languagePair, email).execute();

            } else {
                response = mTranslateService.getTranslatedText(textToTranslate,
                        languagePair).execute();
            }

            if (response.isSuccessful()) {
                return response.body().getResponseData().getTranslatedText();
            }

        } catch (final IOException e) {
            Log.i(ConstantHolder.TAG, "Error : " + e.getMessage());
            return textToTranslate;
        }
        return textToTranslate;
    }
}
