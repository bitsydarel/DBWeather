/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import com.dbeginc.dbweatherdata.proxies.remote.yandextranslator.YandexTranslation
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single
import java.io.IOException
import java.util.*

/**
 * Yandex Text Translator
 *
 * Implementation [Translator] and Encapsulation of yandex Api
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class YandexTranslator : Translator {

    override fun translate(text: String, language: String): Single<String> {
        return Rx2AndroidNetworking
                .get("https://translate.yandex.net/api/v1.5/tr.json/translate")
                .addQueryParameter("key", BuildConfig.YANDEX_TRANSLATE_API_KEY)
                .addQueryParameter("text", text)
                .addQueryParameter("lang", language)
                .addQueryParameter("format", "plain")
                .build()
                .getObjectSingle(YandexTranslation::class.java)
                .onErrorResumeNext {
                    if (it is IOException || it.cause is IOException) Single.just(YandexTranslation(code = 200, text = Collections.singletonList(text)))
                    else Single.error(it)
                }
                .map { it.text?.firstOrNull() ?: text }
    }
}