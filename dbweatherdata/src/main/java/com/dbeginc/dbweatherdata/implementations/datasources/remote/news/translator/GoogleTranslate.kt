/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.BuildConfig
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.translate.Translate
import com.google.api.services.translate.TranslateRequestInitializer
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Created by darel on 05.10.17.
 *
 * Google Translate
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class GoogleTranslate : Translator {
    private val service: Translate = Translate
            .Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(),null)
            .setTranslateRequestInitializer(TranslateRequestInitializer(BuildConfig.TRANSLATE_API_KEY))
            .build()

    override fun translate(text: String, language: String): String {
        return StringEscapeUtils.unescapeHtml4(
                service.translations()
                        .list(listOf(text), language)
                        .execute()
                        .translations
                        .first()
                        .translatedText
        )
    }
}