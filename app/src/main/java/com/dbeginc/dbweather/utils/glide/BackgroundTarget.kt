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

package com.dbeginc.dbweather.utils.glide

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition

/**
 * Created by darel on 21.10.17.
 *
 * Blur background view target
 */
class BackgroundTarget(container: ViewGroup) : ViewTarget<ViewGroup, Drawable>(container) {
    override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
        this.view.background = resource
    }
}