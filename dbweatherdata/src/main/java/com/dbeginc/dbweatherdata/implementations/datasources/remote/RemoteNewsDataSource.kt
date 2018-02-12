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

package com.dbeginc.dbweatherdata.implementations.datasources.remote

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source
import io.reactivex.Flowable

/**
 * Created by darel on 04.10.17.
 *
 * Remote News DataSource
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface RemoteNewsDataSource {
    fun getTranslatedArticles(sources: List<Source>) : Flowable<List<Article>>
    fun getArticles(sources: List<Source>): Flowable<List<Article>>
    fun getSources(): Flowable<List<Source>>
    fun getAllLives(): Flowable<List<Live>>
}