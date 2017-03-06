package com.darelbitsy.dbweather.ui.helper;

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
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.ui.CurrentWeatherFragment;

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
    @BindView(R.id.humidityValue)
    TextView mHumidityValue;
    @BindView(R.id.precipLabel)
    TextView mPrecipLabel;
    @BindView(R.id.precipValue)
    TextView mPrecipValue;
    @BindView(R.id.windSpeedValue)
    TextView mWindSpeedValue;
    @BindView(R.id.cloudCoverValue)
    TextView mCloudCoverValue;
    @BindView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @BindView(R.id.monday)
    Button mondayButton;
    @BindView(R.id.tuesday)
    Button tuesdayButton;
    @BindView(R.id.wednesday)
    Button wednesdayButton;
    @BindView(R.id.thursday)
    Button thursdayButton;
    @BindView(R.id.friday)
    Button fridayButton;
    @BindView(R.id.saturday)
    Button saturdayButton;
    @BindView(R.id.sunday)
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
     * @param timeZone
     */
    public void showWeatherByDay(String dayName, String nextDayName, String timeZone) {
        if(mDailyData.size() > 0) {
            for(DailyData day : mDailyData) {
                if (dayName.equalsIgnoreCase(mCurrentFragment
                        .getString(R.string.today_label))
                        && WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone).equalsIgnoreCase(currentDayName)) {

                    setCurrentWeather(timeZone, day);
                }

                if(dayName.equalsIgnoreCase(mCurrentFragment.getString(R.string.tomorrow_label))
                        && WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone).equals(nextDayName)) {

                    showDayData(dayName, day, timeZone);

                }

                if(WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone).equalsIgnoreCase(dayName)
                        && !WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone).equalsIgnoreCase(currentDayName)) {

                    showDayData(dayName, day, timeZone);

                }
            }
        }
    }

    private void setCurrentViews(String timeZone) {
        mTemperatureLabel.setText(String.format(Locale.ENGLISH,
                "%d",
                WeatherUtil.getTemperatureInInt(mCurrently.getTemperature())));

        mApparentTemperature.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.apparentTemperatureValue),
                WeatherUtil.getTemperatureInInt(mCurrently.getApparentTemperature())));

        mTimeLabel.setText(String.format(Locale.getDefault(),
                mCurrentFragment.getString(R.string.time_label),
                WeatherUtil.getFormattedTime(mCurrently.getTime(), timeZone)));

        mHumidityValue.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.humidity_value),
                WeatherUtil.getHumidityPourcentage(mCurrently.getHumidity())));

        if (mCurrently.getPrecipType() != null) {
            mPrecipLabel.setText(String.format(Locale.getDefault(),
                    mCurrentFragment.getString(R.string.precipeChanceTypeLabel),
                    mCurrently.getPrecipType()));
        }

        //Setting the location to the current location of the device because the api only provide the timezone as location
        mLocationLabel.setText(mCityName);
        Log.i(ConstantHolder.TAG, "the City Name: " + mCityName);

        mPrecipValue.setText(String.format(Locale.getDefault(),
                mCurrentFragment.getString(R.string.precipChanceValue),
                WeatherUtil.getPrecipPourcentage(mCurrently.getPrecipProbability())));

        mSummaryLabel.setText(mCurrently.getSummary());

        mWindSpeedValue.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.windSpeedValue),
                WeatherUtil.getWindSpeedMeterPerHour(mCurrently.getWindSpeed())));

        mCloudCoverValue.setText(String.format(Locale.ENGLISH,
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
        mTemperatureLabel.setText(String.format(Locale.ENGLISH,
                "%d",
                WeatherUtil.getTemperatureInInt(day.getTemperatureMax())));

        mApparentTemperature.setText(String.format(Locale.getDefault(),
                mCurrentFragment.getString(R.string.apparentTemperatureValue),
                WeatherUtil.getTemperatureInInt(day.getApparentTemperatureMax())));

        mTimeLabel.setText(dayName);

        mHumidityValue.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.humidity_value),
                WeatherUtil.getHumidityPourcentage(day.getHumidity())));

        mPrecipValue.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.precipChanceValue),
                WeatherUtil.getPrecipPourcentage(day.getPrecipProbability())));

        mSummaryLabel.setText(day.getSummary());

        getSunriseAndSunset(timeZone, day);

        mWindSpeedValue.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.windSpeedValue),
                WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed())));

        mCloudCoverValue.setText(String.format(Locale.ENGLISH,
                mCurrentFragment.getString(R.string.cloudCoverValue),
                WeatherUtil.getCloudCoverPourcentage(day.getCloudCover())));

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(mCurrentFragment.getActivity(), WeatherUtil.getIconId(day.getIcon())));
        mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(day.getIcon()));
    }

    private void getSunriseAndSunset(String timeZone, DailyData day) {
        mSunriseTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunriseTime(),
                timeZone));
        mSunsetTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunsetTime(),
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

                mSunriseTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunriseTime(),
                        timeZone));

                mSunsetTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunsetTime(),
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
