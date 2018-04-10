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

package com.dbeginc.dbweather.managenewspapers

import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

/**
 * Created by darel on 23.03.18.
 *
 * News Papers Manager Bridge
 */
interface NewsPapersManagerBridge {
    fun goToNewsPaperDetail(newsPaper: NewsPaperModel)

    fun subscribe(newsPaper: NewsPaperModel, position: Int)

    fun unSubscribe(newsPaper: NewsPaperModel, position: Int)
}