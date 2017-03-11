package com.darelbitsy.dbweather.ui.helper;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.ui.CurrentWeatherFragment;
import com.darelbitsy.dbweather.ui.animation.AnimationUtility;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.darelbitsy.dbweather.helper.utility.WeatherUtil.mColorPicker;

/**
 * Created by Darel Bitsy on 03/03/17.
 * class that manage day switch
 */

public class DaySwitcherHelper {
    @BindView(R2.id.current_weather_layout)
    RelativeLayout mMainLayout;
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
    @BindView(R.id.dayButton1)
    Button mondayButton;
    @BindView(R.id.dayButton2)
    Button tuesdayButton;
    @BindView(R.id.dayButton3)
    Button wednesdayButton;
    @BindView(R.id.dayButton4)
    Button thursdayButton;
    @BindView(R.id.dayButton5)
    Button fridayButton;
    @BindView(R.id.dayButton6)
    Button saturdayButton;
    @BindView(R.id.dayButton7)
    Button sundayButton;

    private final CurrentWeatherFragment  mCurrentFragment;
    private List<DailyData> mDailyData;
    private String currentDayName;
    private Currently mCurrently;
    private String mCityName;


    public DaySwitcherHelper(CurrentWeatherFragment currentFragment,
                             View view,
                             Currently currently,
                             List<DailyData> dailyData,
                             String cityName) {

        mCurrently = currently;
        ButterKnife.bind(this, view);
        mCurrentFragment = currentFragment;
        mDailyData = dailyData;
        mCityName = cityName;
        setTypeFace();
    }

    private void setTypeFace() {
        Typeface appTypeFace = AppUtil
                .getAppGlobalTypeFace(mCurrentFragment.getActivity());

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
            mondayButton.setTypeface(appTypeFace);
            tuesdayButton.setTypeface(appTypeFace);
            wednesdayButton.setTypeface(appTypeFace);
            thursdayButton.setTypeface(appTypeFace);
            fridayButton.setTypeface(appTypeFace);
            saturdayButton.setTypeface(appTypeFace);
            sundayButton.setTypeface(appTypeFace);
        }
    }

    public void setCurrentWeather(String timeZone) {
        setCurrentViews(timeZone);
        getSunriseAndSunset(timeZone);
    }

    private void setCurrentWeather(String timeZone, DailyData day) {
        setCurrentWeather(timeZone);
        getSunriseAndSunset(timeZone, day);
    }

    /**
     * Show the weather of the choosed day
     * @param dayName is the day name
     * @param timeZone user timeZone
     */
    public void showWeatherByDay(String dayName, String nextDayName, String timeZone) {
        if(!mDailyData.isEmpty()) {
            for(DailyData day : mDailyData) {
                if (dayName.equalsIgnoreCase(mCurrentFragment
                        .getString(R.string.today_label))
                        && WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone)
                        .equalsIgnoreCase(currentDayName)) {

                    setCurrentWeather(timeZone, day);

                } else if (dayName.equalsIgnoreCase(mCurrentFragment.getString(R.string.tomorrow_label))
                        &&
                        nextDayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone))) {

                    showDayData(dayName, day, timeZone);

                } else if (dayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone))) {

                    showDayData(dayName, day, timeZone);

                }
            }
        }
    }

    private void setCurrentViews(String timeZone) {

        AnimationUtility.rotateTextThanUpdate(mTemperatureLabel,
                String.format(Locale.ENGLISH,
                        "%d",
                        WeatherUtil.getTemperatureInInt(mCurrently.getTemperature())));

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mApparentTemperature,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.apparentTemperatureValue),
                        WeatherUtil.getTemperatureInInt(mCurrently.getApparentTemperature())));


        AnimationUtility.fadeTextOutUpdateThanFadeIn(mTimeLabel,
                String.format(Locale.getDefault(),
                        mCurrentFragment.getString(R.string.time_label),
                        WeatherUtil.getFormattedTime(mCurrently.getTime(), timeZone)));

        AnimationUtility.slideTextUpThanUpdate(mHumidityValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.humidity_value),
                        WeatherUtil.getHumidityPourcentage(mCurrently.getHumidity())));

        if (mCurrently.getPrecipType() != null) {
            mPrecipLabel.setText(String.format(Locale.getDefault(),
                    mCurrentFragment.getString(R.string.precipeChanceTypeLabel),
                    mCurrently.getPrecipType()));
        } else {
            mPrecipLabel.setText("RAIN/SNOW");
        }

        //Setting the location to the current location of the device because the api only provide the timezone as location
        AnimationUtility.fadeTextOutUpdateThanFadeIn(mLocationLabel, mCityName);
        Log.i(ConstantHolder.TAG, "the City Name: " + mCityName);


        AnimationUtility.slideTextRightThanLeft(mPrecipValue,
                String.format(Locale.getDefault(),
                        mCurrentFragment.getString(R.string.precipChanceValue),
                        WeatherUtil.getPrecipPourcentage(mCurrently.getPrecipProbability())));

        AnimationUtility.rotateTextThanUpdate(mSummaryLabel, mCurrently.getSummary());

        AnimationUtility.slideTextLeftThanRight(mWindSpeedValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.windSpeedValue),
                        WeatherUtil.getWindSpeedMeterPerHour(mCurrently.getWindSpeed())));


        AnimationUtility.slideTextUpThanUpdate(mCloudCoverValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.cloudCoverValue),
                        WeatherUtil.getCloudCoverPourcentage(mCurrently.getCloudCover())));

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(mCurrentFragment.getActivity(),
                WeatherUtil.getIconId(mCurrently.getIcon())));


        mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mCurrently.getIcon()));
    }

    /**
     * Show the weather of the choosed day
     * @param dayName the day name
     * @param day the Day
     * @param timeZone the user timeZone
     */
    private void showDayData(String dayName, DailyData day, String timeZone) {

        AnimationUtility.rotateTextThanUpdate(mTemperatureLabel,
                String.format(Locale.ENGLISH, "%d",
                        WeatherUtil.getTemperatureInInt(day.getTemperatureMax())));


        AnimationUtility.fadeTextOutUpdateThanFadeIn(mApparentTemperature,
                String.format(Locale.getDefault(),
                        mCurrentFragment.getString(R.string.apparentTemperatureValue),
                        WeatherUtil.getTemperatureInInt(day.getApparentTemperatureMax())));

        AnimationUtility.fadeTextOutUpdateThanFadeIn(mTimeLabel, dayName);

        AnimationUtility.slideTextUpThanUpdate(mHumidityValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.humidity_value),
                        WeatherUtil.getHumidityPourcentage(day.getHumidity())));

        if (day.getPrecipType() != null) {
            mPrecipLabel.setText(String.format(Locale.getDefault(),
                    mCurrentFragment.getString(R.string.precipeChanceTypeLabel),
                    day.getPrecipType()));
        }

        AnimationUtility.slideTextRightThanLeft(mPrecipValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.precipChanceValue),
                        WeatherUtil.getPrecipPourcentage(day.getPrecipProbability())));

        AnimationUtility.rotateTextThanUpdate(mSummaryLabel, day.getSummary());

        getSunriseAndSunset(timeZone, day);

        AnimationUtility.slideTextLeftThanRight(mWindSpeedValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.windSpeedValue),
                        WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed())));

        AnimationUtility.slideTextUpThanUpdate(mCloudCoverValue,
                String.format(Locale.ENGLISH,
                        mCurrentFragment.getString(R.string.cloudCoverValue),
                        WeatherUtil.getCloudCoverPourcentage(day.getCloudCover())));

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(mCurrentFragment.getActivity(), WeatherUtil.getIconId(day.getIcon())));
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

    private void getSunriseAndSunset(String timeZone) {
        for (DailyData day : mDailyData) {
            //Getting current day
            currentDayName = WeatherUtil.getDayOfTheWeek(mCurrently.getTime(), timeZone);

            //Checking if the current day match the day in the loop
            if (WeatherUtil
                    .getDayOfTheWeek(day.getTime(), timeZone)
                    .equals(currentDayName)) {

                AnimationUtility.slideTextLeftThanRight(mSunriseTimeValue,
                        WeatherUtil.getFormattedTime(day.getSunriseTime(),
                                timeZone));

                AnimationUtility.slideTextRightThanLeft(mSunsetTimeValue,
                        WeatherUtil.getFormattedTime(day.getSunsetTime(),
                                timeZone));
            }
        }
    }

    public void updateDailyData(List<DailyData> dailyData) {
        mDailyData.clear();
        mDailyData.addAll(dailyData);
    }

    public void updateCurrentWeatherData(Currently currently) {
        mCurrently = currently;
    }

    public void updateCityName(String cityName) {
        mCityName = cityName;
    }
}
