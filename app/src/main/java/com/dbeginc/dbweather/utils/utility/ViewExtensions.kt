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

package com.dbeginc.dbweather.utils.utility

import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.Color
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.utils.animations.widgets.RainFallView
import com.dbeginc.dbweather.utils.animations.widgets.SnowFallView
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel
import com.dbeginc.dbweatherweather.viewmodels.toFormattedTime
import com.github.clans.fab.FloatingActionButton
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*

/**
 * Created by Darel Bitsy on 26/04/17.
 * Custom Binder for my layout
 */

fun View.show() {
    visibility = View.VISIBLE
}

fun View.remove() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.toast(message: String = "", resId: Int = 0, duration: Int = Toast.LENGTH_SHORT) {
    if (resId == 0) Toast.makeText(context, message, duration).show()
    else Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
}

fun ViewGroup.snack(message: String = "", resId: Int = 0, duration: Int = Snackbar.LENGTH_LONG) {
    val snackbar = if (resId == 0) Snackbar.make(this, message, duration)
    else Snackbar.make(this, resId, duration)

    snackbar.show()
}

@BindingAdapter("articleTime")
fun setArticleTime(textView: TextView, time: Long) {
    val zoneId = ZoneId.of(TimeZone.getDefault().id)

    val currentTime = Instant.now().atZone(zoneId)

    val zonedLastChange = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(time),
            zoneId
    )

    val difference = Duration.between(zonedLastChange, currentTime)

    val publishDate: String = when {
        difference.toDays() > 0 -> textView.context.getString(R.string.published_at_days_ago, difference.toDays())
        difference.toHours() > 0 -> textView.context.getString(R.string.published_at_hours_ago, difference.toHours())
        difference.toMinutes() > 0 -> textView.context.getString(R.string.published_at_minutes_ago, difference.toMinutes())
        else -> textView.context.getString(R.string.published_at_seconds_ago, difference.seconds)
    }

    textView.text = publishDate
}

@BindingAdapter("setUpdateTime")
fun setWeatherUpdateTime(textView: TextView, time: Long) {
    textView.text = textView.context.getString(R.string.time_label).format(Locale.getDefault(), time.toFormattedTime(null))
}

@BindingAdapter("setImage")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        imageView.setImageDrawable(VectorDrawableCompat.create(imageView.resources, resource, imageView.context.theme))

    } else imageView.setImageResource(resource)
}

fun ConstraintLayout.showRainingAnimation() {
    val snowAnimation = findViewById<SnowFallView>(SnowFallView.VIEW_ID)

    if (snowAnimation != null) removeView(snowAnimation)

    if (findViewById<RainFallView>(RainFallView.VIEW_ID) == null) addView(RainFallView(context), getLayoutParameter())
}

fun ConstraintLayout.showSnowFallAnimation() {
    val rainAnimation = findViewById<RainFallView>(RainFallView.VIEW_ID)

    if (rainAnimation != null) removeView(rainAnimation)

    if (findViewById<SnowFallView>(SnowFallView.VIEW_ID) == null) addView(SnowFallView(context), getLayoutParameter())
}

inline fun WeatherLocationModel.asFloatingActionButton(positions: ViewGroup.LayoutParams, context: Context, crossinline onClick: (WeatherLocationModel) -> Unit): FloatingActionButton {
    return FloatingActionButton(ContextThemeWrapper(context, R.style.AppTheme)).apply {
        labelText = fullName()
        colorNormal = Color.WHITE
        layoutParams = positions
        setColorPressedResId(R.color.colorSecondaryLight)
        setColorRippleResId(R.color.colorSecondaryLight)
        setImageResource(R.drawable.ic_city_location)
        setOnClickListener { onClick(this@asFloatingActionButton) }
    }
}

fun ViewGroup.removeWeatherAnimation() {
    removeView(findViewById<SnowFallView>(SnowFallView.VIEW_ID))
    removeView(findViewById<RainFallView>(RainFallView.VIEW_ID))
}

private fun getLayoutParameter(): ConstraintLayout.LayoutParams {
    val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)

    params.apply {
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    }

    return params
}