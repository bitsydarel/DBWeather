package com.darelbitsy.dbweather.models.provider.translators;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface TranslateProvider<TYPE> {

    TYPE translateText(@NonNull final String text) throws GeneralSecurityException, IOException;

}
