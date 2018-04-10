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

package com.dbeginc.dbweather.di.modules.lives

import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweather.di.ViewModelKey
import com.dbeginc.dbweatherlives.favoriteyoutubelives.FavoriteYoutubeLivesViewModel
import com.dbeginc.dbweatherlives.iptvplaylistdetail.IpTvPlaylistDetailViewModel
import com.dbeginc.dbweatherlives.iptvplaylists.IpTvPlayListsViewModel
import com.dbeginc.dbweatherlives.manageyoutubelives.ManageYoutubeLivesViewModel
import com.dbeginc.dbweatherlives.youtubelivedetail.YoutubeLiveDetailViewModel
import com.dbeginc.dbweatherlives.youtubelives.YoutubeLivesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LivesViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(YoutubeLivesViewModel::class)
    abstract fun bindYoutubeLivesViewModel(youtubeLivesViewModel: YoutubeLivesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteYoutubeLivesViewModel::class)
    abstract fun bindFavoriteYoutubeLivesViewModel(favoriteYoutubeLivesViewModel: FavoriteYoutubeLivesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(YoutubeLiveDetailViewModel::class)
    abstract fun bindYoutubeLiveDetailViewModel(youtubeLiveDetailViewModel: YoutubeLiveDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManageYoutubeLivesViewModel::class)
    abstract fun bindManageYoutubeLivesViewModel(manageYoutubeLivesViewModel: ManageYoutubeLivesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IpTvPlayListsViewModel::class)
    abstract fun bindIpTvPlayListsViewModel(ipTvPlayListsViewModel: IpTvPlayListsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IpTvPlaylistDetailViewModel::class)
    abstract fun bindIpTvPlaylistDetailViewModel(ipTvPlaylistDetailViewModel: IpTvPlaylistDetailViewModel): ViewModel

}