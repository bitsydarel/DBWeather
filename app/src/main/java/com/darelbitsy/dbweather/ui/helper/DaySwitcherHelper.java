package com.darelbitsy.dbweather.ui.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.ui.WeatherFragment;
import com.darelbitsy.dbweather.ui.animation.AnimationUtility;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil.mColorPicker;

/**
 * Created by Darel Bitsy on 03/03/17.
 * class that manage day switch
 */

public class DaySwitcherHelper {
    @BindView(R.id.current_weather_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.locationLabel)
    TextView mLocationLabel;
    @BindView(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @BindView(R.id.iconImageView)
    ImageView mIconImageView;
    @BindView(R.id.apparentTemperature)
    TextView mApparentTemperature;
    @BindView(R.id.timeLabel)
    TextView mTimeLabel;
    @BindView(R.id.sunsetTime)
    TextView mSunsetTimeValue;
    @BindView(R.id.sunriseTime)
    TextView mSunriseTimeValue;
    @BindView(R.id.humidityLabel)
    TextView mHumidityLabel;
    @BindView(R.id.humidityValue)
    TextView mHumidityValue;
    @BindView(R.id.precipLabel)
    TextView mPrecipLabel;
    @BindView(R.id.precipValue)
    TextView mPrecipValue;
    @BindView(R.id.windSpeed)
    TextView mWindSpeedLabel;
    @BindView(R.id.windSpeedValue)
    TextView mWindSpeedValue;
    @BindView(R.id.cloudCoverLabel)
    TextView mCloudCoverLabel;
    @BindView(R.id.cloudCoverValue)
    TextView mCloudCoverValue;
    @BindView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @BindView(R.id.sunriseTimeLabel)
    TextView mSunriseTimeLabel;
    @BindView(R.id.sunsetTimeLabel)
    TextView mSunsetTimeLabel;

    private String mCityName;


    public DaySwitcherHelper(WeatherFragment currentFragment, View view, String cityName) {

        ButterKnife.bind(this, view);
        mCityName = cityName;
        setTypeFace(currentFragment.getContext());
    }

    private void setTypeFace(Context context) {
        Typeface appTypeFace = AppUtil
                .getAppGlobalTypeFace(context);

        if (appTypeFace != null) {
            mLocationLabel.setTypeface(appTypeFace);
            mApparentTemperature.setTypeface(appTypeFace);
            mSunriseTimeLabel.setTypeface(appTypeFace);
            mSunsetTimeLabel.setTypeface(appTypeFace);
            mTimeLabel.setTypeface(appTypeFace);
            mPrecipLabel.setTypeface(appTypeFace);
            mWindSpeedLabel.setTypeface(appTypeFace);
            mHumidityLabel.setTypeface(appTypeFace);
            mCloudCoverLabel.setTypeface(appTypeFace);
            mSummaryLabel.setTypeface(appTypeFace);
        }
    }

    public void setCurrentViews(Context context, Currently currently, String timeZone, long sunrise, long sunset) {

        AnimationUtility.rotateTextThanUpdate(mTemperatureLabel,
                String.format(Locale.ENGLISH,
                        "%d",
                        WeatherUtil.getTemperatureInInt(currently.getTemperature())));

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mApparentTemperature,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.apparentTemperatureValue),
                        WeatherUtil.getTemperatureInInt(currently.getApparentTemperature())));


        AnimationUtility.fadeTextOutUpdateThanFadeIn(mTimeLabel,
                String.format(Locale.getDefault(),
                        context.getString(R.string.time_label),
                        WeatherUtil.getFormattedTime(currently.getTime(), timeZone)));

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mHumidityValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.humidity_value),
                        WeatherUtil.getHumidityPourcentage(currently.getHumidity())));

        if (currently.getPrecipType() != null) {

            AnimationUtility.slideTextRightThanLeft(mPrecipLabel, String.format(Locale.getDefault(),
                    context.getString(R.string.precipeChanceTypeLabel),
                    currently.getPrecipType()));
        } else {
            AnimationUtility.slideTextRightThanLeft(mPrecipLabel, "Rain/Snow");
        }

        //Setting the location to the current location of the device because the api only provide the timezone as location
        AnimationUtility.fadeTextOutUpdateThanFadeIn(mLocationLabel, mCityName);
        Log.i(ConstantHolder.TAG, "the City Name: " + mCityName);


        AnimationUtility.slideTextRightThanLeft(mPrecipValue,
                String.format(Locale.getDefault(),
                        context.getString(R.string.precipChanceValue),
                        WeatherUtil.getPrecipPourcentage(currently.getPrecipProbability())));

        AnimationUtility.rotateTextThanUpdate(mSummaryLabel, currently.getSummary());

        AnimationUtility.slideTextLeftThanRight(mWindSpeedValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.windSpeedValue),
                        WeatherUtil.getWindSpeedMeterPerHour(currently.getWindSpeed())));


        AnimationUtility.fadeTextOutUpdateThanFadeIn(mCloudCoverValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.cloudCoverValue),
                        WeatherUtil.getCloudCoverPourcentage(currently.getCloudCover())));

        getSunriseAndSunset(timeZone, sunrise, sunset);

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(context,
                WeatherUtil.getIconId(currently.getIcon())));

        if (!"rain".equals(currently.getIcon()) || !"snow".equals(currently.getIcon())) {

            VideoView videoView = (VideoView)
                    mMainLayout.findViewById(R.id.backgroundVideo);
            if (videoView.getVisibility() == View.VISIBLE) {
                videoView.stopPlayback();
                videoView.setVisibility(View.INVISIBLE);
            }
            mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(currently.getIcon()));
        }
    }

    /**
     * Show the weather of the choosed day
     * @param dayName the day name
     * @param day the Day
     * @param timeZone the user timeZone
     */
    public void showDayData(Context context, String dayName, DailyData day, String timeZone) {

        //Setting the location to the current location of the device because the api only provide the timezone as location
        AnimationUtility.fadeTextOutUpdateThanFadeIn(mLocationLabel, mCityName);
        Log.i(ConstantHolder.TAG, "the City Name: " + mCityName);

        AnimationUtility.rotateTextThanUpdate(mTemperatureLabel,
                String.format(Locale.ENGLISH, "%d",
                        WeatherUtil.getTemperatureInInt(day.getTemperatureMax())));


        AnimationUtility.fadeTextOutUpdateThanFadeIn(mApparentTemperature,
                String.format(Locale.getDefault(),
                        context.getString(R.string.apparentTemperatureValue),
                        WeatherUtil.getTemperatureInInt(day.getApparentTemperatureMax())));

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mTimeLabel, dayName);

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mHumidityValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.humidity_value),
                        WeatherUtil.getHumidityPourcentage(day.getHumidity())));

        if (day.getPrecipType() != null) {
            mPrecipLabel.setText(String.format(Locale.getDefault(),
                    context.getString(R.string.precipeChanceTypeLabel),
                    day.getPrecipType()));
        }

        AnimationUtility.slideTextRightThanLeft(mPrecipValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.precipChanceValue),
                        WeatherUtil.getPrecipPourcentage(day.getPrecipProbability())));

        AnimationUtility.rotateTextThanUpdate(mSummaryLabel, day.getSummary());

        getSunriseAndSunset(timeZone, day);

        AnimationUtility.slideTextLeftThanRight(mWindSpeedValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.windSpeedValue),
                        WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed())));

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mCloudCoverValue,
                String.format(Locale.ENGLISH,
                        context.getString(R.string.cloudCoverValue),
                        WeatherUtil.getCloudCoverPourcentage(day.getCloudCover())));

        mIconImageView.setImageDrawable(ContextCompat
                .getDrawable(context, WeatherUtil.getIconId(day.getIcon())));

        mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(day.getIcon()));
    }

    private void getSunriseAndSunset(String timeZone, DailyData day) {
        AnimationUtility.slideTextLeftThanRight(mSunriseTimeValue,
                WeatherUtil.getFormattedTime(day.getSunriseTime(),
                        timeZone));

        AnimationUtility.slideTextRightThanLeft(mSunsetTimeValue,
                WeatherUtil.getFormattedTime(day.getSunsetTime(),
                        timeZone));
    }

    private void getSunriseAndSunset(String timeZone, long sunrise, long sunset) {
        AnimationUtility.slideTextLeftThanRight(mSunriseTimeValue,
                        WeatherUtil.getFormattedTime(sunrise,
                                timeZone));

                AnimationUtility.slideTextRightThanLeft(mSunsetTimeValue,
                        WeatherUtil.getFormattedTime(sunset,
                                timeZone));
    }

    public void updateCityName(String cityName) {
        mCityName = cityName;
    }
}
