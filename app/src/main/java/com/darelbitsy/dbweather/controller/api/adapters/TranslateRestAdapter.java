package com.darelbitsy.dbweather.controller.api.adapters;

import android.util.Log;

import com.darelbitsy.dbweather.controller.api.services.TranslateService;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.model.ResponseData;
import com.darelbitsy.dbweather.model.news.MyMemoryJson;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class TranslateRestAdapter {
    private final OkHttpClient mHttpClient = new OkHttpClient();
    protected final Retrofit mRestAdapter;
    protected TranslateService mTranslateService;
    public TranslateRestAdapter() {
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(ConstantHolder.MYMEMORY_APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mHttpClient)
                .build();

        mTranslateService = mRestAdapter.create(TranslateService.class);
    }

    public String translateText(String textToTranslate, String email) {
        ResponseData responseData;

        String languagePair = String.format(Locale.ENGLISH,
                "en|%s",
                ConstantHolder.USER_LANGUAGE);

        Call<MyMemoryJson> response;

        if (!email.equals("")) {
             response = mTranslateService.getTranslatedText(textToTranslate,
                    languagePair, email);

        } else {
            response = mTranslateService.getTranslatedText(textToTranslate,
                    languagePair);
        }

        try {
            responseData = response.execute().body().getResponseData();

        } catch (IOException e) {
            Log.i(ConstantHolder.TAG, "Error : " + e.getMessage());
            return textToTranslate;
        }
        return responseData.getTranslatedText();
    }
}
