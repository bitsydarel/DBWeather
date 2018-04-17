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

package com.dbeginc.dbweather.di.modules.news

import com.dbeginc.dbweather.articledetail.ArticleDetailActivity
import com.dbeginc.dbweather.articles.ArticlesFragment
import com.dbeginc.dbweather.managenewspapers.ManageNewsPapersFragment
import com.dbeginc.dbweather.newspaper.NewsPapersFragment
import com.dbeginc.dbweather.newspaperdetail.NewsPaperDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NewsFeatureModule {

    @ContributesAndroidInjector
    abstract fun contributeNewsPaperFragment(): NewsPapersFragment

    @ContributesAndroidInjector
    abstract fun contributeArticlesFragment(): ArticlesFragment

    @ContributesAndroidInjector
    abstract fun contributeArticleDetailActivity(): ArticleDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeManageNewsPapersFragment(): ManageNewsPapersFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsPaperDetailActivity(): NewsPaperDetailActivity
}