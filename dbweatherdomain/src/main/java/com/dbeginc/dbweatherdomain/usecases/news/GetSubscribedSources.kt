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

package com.dbeginc.dbweatherdomain.usecases.news

import com.dbeginc.dbweatherdomain.entities.news.Source
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweatherdomain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 06.10.17.
 *
 * Get Subscribed Sources Use Case
 */
class GetSubscribedSources(private val newsRepo: NewsRepository) : UseCase<List<Source>, Unit>() {

    override fun buildUseCase(params: Unit): Flowable<List<Source>> = newsRepo.getSubscribedSources()

    override fun clean() = newsRepo.clean()
}