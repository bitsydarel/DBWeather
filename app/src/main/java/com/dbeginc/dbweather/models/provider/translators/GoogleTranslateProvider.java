package com.dbeginc.dbweather.models.provider.translators;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Google Translate Provider
 */

@Singleton
public class GoogleTranslateProvider implements ITranslateProvider {
    private static final String translateApiKey = "AIzaSyAcFFnbD94RuNav543XpwfrPh0kOznIR3c";
    private final String mUserLanguage = Locale.getDefault().getLanguage();
    @Inject Context mApplicationContext;

    @Inject
    public GoogleTranslateProvider() {
        DBWeatherApplication.getComponent()
                .inject(this);
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
