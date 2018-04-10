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

import com.dbeginc.dbweather.iptvlivedetail.IpTvLiveActivity
import com.dbeginc.dbweather.iptvplaylistdetail.IpTvPlaylistDetailFragment
import com.dbeginc.dbweather.iptvplaylists.IptvPlaylistsFragment
import com.dbeginc.dbweather.youtubefavoritelives.FavoriteYoutubeLivesFragment
import com.dbeginc.dbweather.youtubelives.YoutubeLivesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LivesFeatureModule {
    @ContributesAndroidInjector
    abstract fun contributeAllLivesTabPageFragment(): YoutubeLivesFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteLivesTabFragment(): FavoriteYoutubeLivesFragment

    @ContributesAndroidInjector
    abstract fun contributeIptvPlaylistsFragment(): IptvPlaylistsFragment

    @ContributesAndroidInjector
    abstract fun contributeIpTvPlaylistDetailFragment(): IpTvPlaylistDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeIpTvLiveActivity(): IpTvLiveActivity
}