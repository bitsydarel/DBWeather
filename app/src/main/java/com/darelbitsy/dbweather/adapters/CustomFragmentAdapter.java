package com.darelbitsy.dbweather.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.ui.WeatherFragment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 13/02/17.
 * My custom Fragment adapter
 */

public class CustomFragmentAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private final DailyData[] listOfData = new DailyData[7];
    private final WeatherFragment[] listOfFragments =
            new WeatherFragment[7];

    private Weather mWeather;
    private Currently mCurrently;

    private final View mParentLayout;


    public CustomFragmentAdapter(View parentLayout, FragmentManager fm, Weather weatherData) {
        super(fm);
        mWeather = weatherData;
        mCurrently = weatherData.getCurrently();

        setupDailyData(weatherData.getDaily().getData(),
                weatherData.getTimezone(), listOfData);
        mParentLayout = parentLayout;
    }

    public View getParentLayout() {
        return mParentLayout;
    }

    private void setupDailyData(List<DailyData> data,
                                String timeZone,
                                DailyData[] listFragments) {

        Calendar calendar = Calendar.getInstance();
        String currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault());

        int count = 0;
        boolean isTodaySet = false;
        boolean isTomorrowSet = false;

        Integer currentDayIndex = null;

        while (count < 7) {
            for (DailyData day : data) {
                if (count == 7) { break; }

                if (!isTodaySet &&
                        currentDayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(),
                                timeZone))) {

                    count = 0;
                    mCurrently.setSunriseTime(day.getSunriseTime());
                    mCurrently.setSunsetTime(day.getSunsetTime());
                    currentDayIndex = count++;
                    isTodaySet = true;

                } else if (currentDayIndex != null
                        && count == (currentDayIndex + 1)) {

                    listFragments[1] = day;

                    count++;
                    isTomorrowSet = true;

                } else if (isTodaySet && isTomorrowSet) {
                    listFragments[count++] = day;

                }
            }
        }

    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position of the fragment to get
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                WeatherFragment fragment =
                        WeatherFragment.newInstance(mCurrently, mWeather.getCityName());
                fragment.setAdapter(this);
                listOfFragments[0] = fragment;
                return fragment;

            case 1:
                WeatherFragment dayFragment1 =
                        WeatherFragment.newInstance(listOfData[1], mWeather.getCityName());
                dayFragment1.setAdapter(this);
                listOfFragments[1] = dayFragment1;
                return dayFragment1;

            case 2:
                WeatherFragment dayFragment2 =
                        WeatherFragment.newInstance(listOfData[2], mWeather.getCityName());
                dayFragment2.setAdapter(this);
                listOfFragments[2] = dayFragment2;
                return dayFragment2;

            case 3:
                WeatherFragment dayFragment3 =
                        WeatherFragment.newInstance(listOfData[3], mWeather.getCityName());
                dayFragment3.setAdapter(this);
                listOfFragments[3] = dayFragment3;
                return dayFragment3;

            case 4:
                WeatherFragment dayFragment4 =
                        WeatherFragment.newInstance(listOfData[4], mWeather.getCityName());
                dayFragment4.setAdapter(this);
                listOfFragments[4] = dayFragment4;
                return dayFragment4;

            case 5:
                WeatherFragment dayFragment5 =
                        WeatherFragment.newInstance(listOfData[5], mWeather.getCityName());
                dayFragment5.setAdapter(this);
                listOfFragments[5] = dayFragment5;
                return dayFragment5;

            case 6:
                WeatherFragment dayFragment6 =
                        WeatherFragment.newInstance(listOfData[6], mWeather.getCityName());
                dayFragment6.setAdapter(this);
                listOfFragments[6] = dayFragment6;
                return dayFragment6;

            default:
                WeatherFragment defaultFragment =
                        WeatherFragment.newInstance(mCurrently, mWeather.getCityName());
                defaultFragment.setAdapter(this);
                listOfFragments[0] = defaultFragment;
                return defaultFragment;
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return listOfData.length;
    }

    public void updateWeatherOnFragment(Weather weatherData) {
        mWeather = weatherData;
        mCurrently = weatherData.getCurrently();
        for (int index = 0; index < listOfFragments.length; index++) {
            if (listOfFragments[index] != null) {
                if (index == 0) {
                    listOfFragments[index].updateDataFromActivity(mCurrently, weatherData.getCityName());
                } else {
                    listOfFragments[index].updateDataFromActivity(weatherData.getCityName());
                }
            }
        }

        setupDailyData(weatherData.getDaily().getData(),
                weatherData.getTimezone(),
                listOfData);
    }
}
