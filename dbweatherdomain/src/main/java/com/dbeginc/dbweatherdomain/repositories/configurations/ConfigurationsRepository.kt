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

package com.dbeginc.dbweatherdomain.repositories.configurations

import com.dbeginc.dbweatherdomain.repositories.Cleanable
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by darel on 25.10.17.
 *
 * Configuration Repository
 *
 * Interface following repository pattern.
 *
 * Each method in this interface are threaded as Use Cases (Interactor in terms of Clean Architecture).
 */
interface ConfigurationsRepository : Cleanable{

    /**
     * Created by darel on 26.10.17.
     *
     * Get weather notification status
     *
     * @return [Single] reactive stream of [Boolean] that provide a [Boolean] of true or false if notification is enable or an error
     */
    fun getWeatherNotificationStatus() : Single<Boolean>


    /**
     * Created by darel on 26.10.17.
     *
     * Get NewsPaper Translation status
     *
     * @return [Single] reactive stream of [Boolean] that provide a [Boolean] of true or false if translation is enable or an error
     */
    fun getNewsPapersTranslationStatus(): Single<Boolean>


    /**
     * Created by darel on 25.10.17.
     *
     * Change weather notification status
     *
     * @param enable weather notifications
     *
     * @return [Completable] reactive stream that notify completion of the task
     */
    fun changeWeatherNotificationStatus(enable: Boolean): Completable


    /**
     * Created by darel on 25.10.17.
     *
     * Change NewsPaper Translation Status
     *
     * @param shouldTranslate newsPapers
     *
     * @return [Completable] reactive stream that notify completion of the task
     */
    fun changeNewsPapersTranslationStatus(shouldTranslate: Boolean): Completable
}