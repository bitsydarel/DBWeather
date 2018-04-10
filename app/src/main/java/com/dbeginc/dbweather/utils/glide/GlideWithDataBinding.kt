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

import android.databinding.BindingAdapter
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.utils.utility.YOUTUBE_THUMBNAIL_URL
import java.util.*

private val IPTV_GENERIC_LOGOS: IntArray = intArrayOf(
        android.R.color.holo_green_light,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_purple
)

private val RANDOM_VALUE_PICKER: Random = Random()

@BindingAdapter("setImageUrl")
fun setImage(imageView: ImageView, url: String?) {
    if (url != null && url.isNotEmpty()) {
        Glide.with(imageView)
                .load(url)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
    }
}

@BindingAdapter("setYoutubeImage")
fun setYoutubeImage(imageView: ImageView, url: String?) {
    if (url != null && url.isNotEmpty()) {
        Glide.with(imageView)
                .load(YOUTUBE_THUMBNAIL_URL.format(url))
                .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
    }
}

@BindingAdapter("setBlurYoutubeBackgroundImage")
fun setBlurBackground(viewGroup: ViewGroup, url: String?) {
    if (url != null && url.isNotEmpty()) {
        Glide.with(viewGroup.context)
                .load(YOUTUBE_THUMBNAIL_URL.format(url))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.bitmapTransform(BlurTransformation(viewGroup.context)))
                .into(BackgroundTarget(viewGroup))
    }
}

@BindingAdapter(value = ["iptvLogo", "iptvName"], requireAll = true)
fun setIpTvLogo(imageView: ImageView, iptvLogo: String, iptvName: String) {
    if (iptvLogo.isNotBlank()) {
        Glide.with(imageView)
                .load(iptvLogo)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)

    } else imageView.setImageResource(IPTV_GENERIC_LOGOS[RANDOM_VALUE_PICKER.nextInt(IPTV_GENERIC_LOGOS.size)])

}