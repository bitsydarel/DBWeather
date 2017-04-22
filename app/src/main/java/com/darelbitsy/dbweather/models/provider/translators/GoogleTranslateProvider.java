package com.darelbitsy.dbweather.models.provider.translators;

import android.content.Context;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.R;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Google Translate Provider
 */

public class GoogleTranslateProvider implements TranslateProvider<String> {
    private static final String translateApiKey = "AIzaSyAcFFnbD94RuNav543XpwfrPh0kOznIR3c";
    private final String mUserLanguage = Locale.getDefault().getLanguage();
    private final Context mApplicationContext;
    private static GoogleTranslateProvider singletonGoogleTranslator;

    public static GoogleTranslateProvider newInstance(final Context context) {
        if (singletonGoogleTranslator == null) {
            singletonGoogleTranslator = new GoogleTranslateProvider(context.getApplicationContext());
        }

        return singletonGoogleTranslator;
    }

    private GoogleTranslateProvider(final Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public String translateText(@NonNull final String text) throws GeneralSecurityException, IOException {

        final ImmutableList<String> textToTranslate = ImmutableList
                .<String>builder()
                .add(text)
                .build();

        return createTranslateService()
                .translations()
                .list(textToTranslate, mUserLanguage)
                .execute()
                .getTranslations()
                .get(0)
                .getTranslatedText();
    }

    private Translate createTranslateService() throws GeneralSecurityException, IOException {
        return new Translate.Builder(com.google.api.client.extensions.android.http.AndroidHttp.newCompatibleTransport(),
                GsonFactory.getDefaultInstance(), null)
                .setTranslateRequestInitializer(new TranslateRequestInitializer(translateApiKey))
                .setApplicationName(mApplicationContext.getString(R.string.app_name))
                .build();
    }
}
