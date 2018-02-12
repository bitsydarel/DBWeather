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

package com.dbeginc.dbweather.di.modules

import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.config.managelocations.view.ManageLocationsActivity
import com.dbeginc.dbweather.config.managesources.ManageSourcesActivity
import com.dbeginc.dbweather.config.managesources.sourcedetail.SourceDetailActivity
import com.dbeginc.dbweather.di.modules.main.config.ConfigurationsModule
import com.dbeginc.dbweather.di.modules.main.news.NewsModule
import com.dbeginc.dbweather.di.modules.main.weather.WeatherModule
import com.dbeginc.dbweather.intro.IntroActivity
import com.dbeginc.dbweather.news.lives.livedetail.LiveDetailActivity
import com.dbeginc.dbweather.news.newspaper.articledetail.ArticleDetailActivity
import com.dbeginc.dbweather.splash.view.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by darel on 02.02.18.
 *
 * Activity Dagger Module
 */
@Module
abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = [NewsModule::class, WeatherModule::class, ConfigurationsModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector()
    abstract fun contributeIntroActivity(): IntroActivity

    @ContributesAndroidInjector()
    abstract fun contributeBaseActivity(): BaseActivity

    @ContributesAndroidInjector(modules = [NewsModule::class])
    abstract fun contributeArticleDetailActivity(): ArticleDetailActivity

    @ContributesAndroidInjector(modules = [NewsModule::class])
    abstract fun contributeLiveDetailActivity(): LiveDetailActivity

    @ContributesAndroidInjector(modules = [NewsModule::class])
    abstract fun contributeSourceDetailActivity(): SourceDetailActivity

    @ContributesAndroidInjector(modules = [NewsModule::class])
    abstract fun contributeManageSourcesActivity(): ManageSourcesActivity

    @ContributesAndroidInjector(modules = [WeatherModule::class])
    abstract fun contributeManageLocationsActivity(): ManageLocationsActivity
}