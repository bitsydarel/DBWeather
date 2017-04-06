package com.darelbitsy.dbweather.controller.api.adapters.helper;


import android.content.Context;

import com.darelbitsy.dbweather.R;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 16/02/17.
 */

class TranslateHelper {
    private final String translateApiKey = "AIzaSyAcFFnbD94RuNav543XpwfrPh0kOznIR3c";
    private final String mUserLanguage = Locale.getDefault().getLanguage();

    String translateText(String sourceText, Context context) throws GeneralSecurityException, IOException {
        final ImmutableList<String> textToTranslate = ImmutableList
                .<String>builder()
                .add(sourceText)
                .build();

        return createTranslateService(context)
                .translations()
                .list(textToTranslate, mUserLanguage)
                .execute()
                .getTranslations()
                .get(0)
                .getTranslatedText();
    }

    private Translate createTranslateService(Context context) throws GeneralSecurityException, IOException {
        return new Translate.Builder(com.google.api.client.extensions.android.http.AndroidHttp.newCompatibleTransport(),
                GsonFactory.getDefaultInstance(), null)
                .setTranslateRequestInitializer(new TranslateRequestInitializer(translateApiKey))
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }
}
