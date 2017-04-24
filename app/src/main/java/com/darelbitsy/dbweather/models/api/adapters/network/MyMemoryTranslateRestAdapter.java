package com.darelbitsy.dbweather.models.api.adapters.network;

import com.darelbitsy.dbweather.models.api.services.TranslateService;
import com.darelbitsy.dbweather.models.datatypes.news.MyMemoryJson;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 19/02/17.
 * Translate api adapter
 */

public class MyMemoryTranslateRestAdapter {
    private final TranslateService mTranslateService;
    private static MyMemoryTranslateRestAdapter singletonMyMemoryTranslator;

    public static MyMemoryTranslateRestAdapter newInstance() {
        if (singletonMyMemoryTranslator == null) {
            singletonMyMemoryTranslator = new MyMemoryTranslateRestAdapter();
        }

        return singletonMyMemoryTranslator;
    }


    private MyMemoryTranslateRestAdapter() {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(ConstantHolder.MYMEMORY_APIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(AppUtil.translateOkHttpClient)
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
