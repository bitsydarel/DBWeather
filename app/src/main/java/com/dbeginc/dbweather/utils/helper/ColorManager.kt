package com.dbeginc.dbweather.utils.helper

import android.util.SparseIntArray

import com.dbeginc.dbweather.R

/**
 * Created by Darel Bitsy on 05/01/17.
 * Color manager for dbweather app
 */

object ColorManager {
    private val mColors = SparseIntArray()

    init {
        mColors.put(R.drawable.clear_day, R.color.clear_day_background)
        mColors.put(R.drawable.clear_night, R.drawable.clear_night_background)
        mColors.put(R.drawable.partly_cloudy, R.drawable.partly_cloudy_background)
        mColors.put(R.drawable.cloudy_night, R.drawable.cloudy_night_background)
        mColors.put(R.drawable.cloudy, R.drawable.cloudy_background)
        mColors.put(R.drawable.fog, R.color.fog_background)
        mColors.put(R.drawable.sleet, R.color.clear_day_background)
        mColors.put(R.drawable.snow, R.color.snow_background)
        mColors.put(R.drawable.wind, R.color.wind_background)
        mColors.put(R.drawable.rain, R.color.rain_background)
    }

    fun getBackgroundColor(icon: Int): Int {
        return mColors.get(icon)
    }

    fun getSourceBackground(category: String): Int {
        return when(category){
            "business" -> R.color.category_business
            "entertainment" -> R.color.category_entertainment
            "gaming" -> R.color.category_gaming
            "general" -> R.color.category_general
            else -> R.color.category_entertainment
        }
    }
}
