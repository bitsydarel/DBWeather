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

package com.dbeginc.dbweather.news.lives.page.alllives

import com.dbeginc.dbweather.base.IPresenter
import com.dbeginc.dbweather.base.IView
import com.dbeginc.dbweather.viewmodels.news.LiveModel

/**
 * Created by darel on 20.10.17.
 *
 * Lives Tab Contract
 */
interface AllLivesTabContract {

    interface AllLivesTabView : IView {
        fun displayAllLives(lives: List<LiveModel>)

        fun showUpdateStatus()

        fun hideUpdateStatus()

        fun showError(message: String)
    }

    interface AllLivesTabPresenter : IPresenter<AllLivesTabView> {
        fun loadAllLives()
    }
}