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

package com.dbeginc.dbweather.utils.utility

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.view.View
import com.dbeginc.dbweather.di.WithChildDependencies
import com.dbeginc.dbweather.di.WithDependencies
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerApplication


/**
 * Created by darel on 27.09.17.
 *
 * Dependency Injector
 */
object Injector {

    fun init(application: DaggerApplication) {

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (activity is WithDependencies) {
                    AndroidInjection.inject(activity)
                }

                if (activity is WithChildDependencies) {
                    injectFragmentsDependencies(activity)
                }
            }

            override fun onActivityPaused(p0: Activity?) {}
            override fun onActivityResumed(p0: Activity?) {}
            override fun onActivityStarted(p0: Activity?) {}
            override fun onActivityDestroyed(p0: Activity?) {}
            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
            override fun onActivityStopped(p0: Activity?) {}
        })
    }

    private fun injectFragmentsDependencies(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentCreated(fm: FragmentManager?, fragment: Fragment?, savedInstanceState: Bundle?) {
                            if (fragment is WithDependencies) AndroidSupportInjection.inject(fragment)

                            if (fragment is WithChildDependencies) injectFragmentsChildDependencies(fragment)
                        }
                    }, true)
        }
    }

    private fun injectFragmentsChildDependencies(fragment: Fragment) {
        fragment.childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
                if (f is WithDependencies) AndroidSupportInjection.inject(f)

                if (f is WithChildDependencies) injectFragmentsChildDependencies(f)
            }
        }, true)
    }
}