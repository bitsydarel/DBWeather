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

/**
 * Created by darel on 22.03.18.
 *
 * Constant Holder for DBWeather
 */
/****************************** WEATHER  *****************************/
const val SEARCH_QUERY_URI = "content://com.dbeginc.dbweather.utils.contentprovider.LocationSuggestionProvider/search_suggest_query/%s?limit=50"
const val WEATHER_ALERT_CHANNEL_ID = "forecast_alert_id"
const val WEATHER_ALERT_CHANNEL_NAME = "DBWeather forecast alert"
const val IS_GPS_PERMISSION_GRANTED = "is_gps_permission_granted"
const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125
const val IS_GPS_LOCATION = "is_current_location"
const val NOTIFICATION_KEY = "notification_key"
const val WEATHER_ALERT_ID = 7125205
/****************************** WEATHER KEYS  *****************************/
const val CURRENT_CITY = "current_city"
const val CURRENT_LATITUDE = "current_latitude"
const val CURRENT_LONGITUDE = "current_longitude"
const val CURRENT_COUNTRY_CODE = "current_country_code"
const val CUSTOM_CITY = "custom_city"
const val CUSTOM_LATITUDE = "custom_latitude"
const val CUSTOM_LONGITUDE = "custom_longitude"
const val CUSTOM_COUNTRY_CODE = "custom_country_code"
/****************************** News  *****************************/
const val SOURCE_SORTING_PREFERENCES = "source_sorting_preferences"
const val NEWSPAPER_KEY = "source_key"
const val ARTICLE_KEY = "article_key"
/********************** Youtube Constant  *****************************/
const val YOUTUBE_RECOVERY_DIALOG_REQUEST = 7125
const val YOUTUBE_LIVE_KEY = "youtube_live_key"
const val YOUTUBE_LIVES_SORTING_PREFERENCES = "youtube_live_sorting_preferences"
const val YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/%s/hqdefault.jpg"
/********************** Application  *****************************/
const val IPTV_PLAYLIST_KEY = "iptv_playlist_key"
const val IPTV_LIVE_DATA = "iptv_live_data"
const val PREFS_NAME = "db_weather_prefs"
const val FIRST_RUN = "is_first_run"
const val LOADING_PERIOD: Long = 500
const val CUSTOM_TAB_CLIENT_WAIT_TIME: Long = 1L
