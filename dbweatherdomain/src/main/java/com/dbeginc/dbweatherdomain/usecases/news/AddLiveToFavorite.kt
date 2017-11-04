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

import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweatherdomain.usecases.UseCaseCompletable
import io.reactivex.Completable

/**
 * Created by darel on 19.10.17.
 *
 * Bookmark Live use case
 */
class AddLiveToFavorite(private val repo: NewsRepository) : UseCaseCompletable<LiveRequest<Unit>>(){

    override fun buildUseCase(params: LiveRequest<Unit>): Completable = repo.addLiveToFavorite(params)

    override fun clean() = repo.clean()

}