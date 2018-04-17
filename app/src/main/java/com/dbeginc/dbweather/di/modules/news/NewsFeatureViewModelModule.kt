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

import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweather.di.ViewModelKey
import com.dbeginc.dbweathernews.articledetail.ArticleDetailViewModel
import com.dbeginc.dbweathernews.articles.ArticlesViewModel
import com.dbeginc.dbweathernews.managenewspapers.ManageNewsPapersViewModel
import com.dbeginc.dbweathernews.newspaperdetail.NewsPaperDetailViewModel
import com.dbeginc.dbweathernews.newspapers.NewsPapersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class NewsFeatureViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(NewsPapersViewModel::class)
    abstract fun bindNewsPapersViewModel(newsPapersViewModel: NewsPapersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsPaperDetailViewModel::class)
    abstract fun bindNewsPaperDetailViewModel(newsPaperDetailViewModel: NewsPaperDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticlesViewModel::class)
    abstract fun bindArticlesViewModel(articlesViewModel: ArticlesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticleDetailViewModel::class)
    abstract fun bindArticleDetailViewModel(articleDetailViewModel: ArticleDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManageNewsPapersViewModel::class)
    abstract fun bindManageNewsPapersViewModel(manageNewsPapersViewModel: ManageNewsPapersViewModel): ViewModel
}