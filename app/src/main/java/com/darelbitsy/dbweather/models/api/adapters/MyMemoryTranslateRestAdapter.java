package com.darelbitsy.dbweather.models.api.adapters;

import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.api.services.TranslateService;
import com.darelbitsy.dbweather.models.datatypes.news.MyMemoryJson;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 19/02/17.
 * Translate api adapter
 */

@Singleton
public class MyMemoryTranslateRestAdapter {
    private final TranslateService mTranslateService;


    @Inject
    public MyMemoryTranslateRestAdapter(final OkHttpClient okHttpClient) {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(ConstantHolder.MYMEMORY_APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mTranslateService = restAdapter.create(TranslateService.class);
    }

    public Call<MyMemoryJson> translateText(final String textToTranslate,
                                            final String languagePair) {


            return mTranslateService.getTranslatedText(textToTranslate,
                        languagePair);
    }

    public Call<MyMemoryJson> translateText(final String textToTranslate,
                                            final String languagePair,
                                            final String email) {

        return mTranslateService.getTranslatedText(textToTranslate,
                languagePair, email);
    }
}
