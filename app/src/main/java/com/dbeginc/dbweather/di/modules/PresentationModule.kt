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

package com.dbeginc.dbweather.di.modules

import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.launch.LaunchActivity
import com.dbeginc.dbweather.launch.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by darel on 08.02.18.
 */
@Module
abstract class PresentationModule {

    @ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeIntroActivity(): LaunchActivity

    @ContributesAndroidInjector()
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector()
    abstract fun contributeBaseActivity(): BaseActivity

    @ContributesAndroidInjector()
    abstract fun contributeBaseFragment(): BaseFragment
}