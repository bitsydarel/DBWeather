package com.darelbitsy.dbweather.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.darelbitsy.dbweather.models.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.weather.Currently;
import com.darelbitsy.dbweather.models.datatypes.weather.DailyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.views.fragments.WeatherFragment;

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


    public CustomFragmentAdapter(final View parentLayout,
                                 final FragmentManager fm,
                                 final Weather weatherData) {
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

    private void setupDailyData(final List<DailyData> data,
                                final String timeZone,
                                final DailyData[] listFragments) {

        final Calendar calendar = Calendar.getInstance();
        final String currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
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
                    if (listOfFragments[1] != null) {
                        listOfFragments[1].updateDataFromActivity(mWeather.getCityName(), day);
                    }

                    count++;
                    isTomorrowSet = true;

                } else if (isTodaySet && isTomorrowSet) {
                    listFragments[count] = day;
                    if (listOfFragments[count] != null) {
                        listOfFragments[count].updateDataFromActivity(mWeather.getCityName(), day);
                    }
                    count++;
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
    public Fragment getItem(final int position) {
        switch (position) {
            case 0:
                if (listOfFragments[0] == null) {
                    final WeatherFragment fragment =
                            WeatherFragment.newInstance(mCurrently, mWeather.getCityName());
                    fragment.setAdapter(this);
                    listOfFragments[0] = fragment;

                } else {
                    listOfFragments[0]
                            .updateDataFromActivity(mCurrently, mWeather.getCityName());
                }
                return listOfFragments[0];

            case 1:
                if (listOfFragments[1] == null) {
                    final WeatherFragment dayFragment1 =
                            WeatherFragment.newInstance(listOfData[1], mWeather.getCityName());
                    dayFragment1.setAdapter(this);
                    listOfFragments[1] = dayFragment1;
                } else {
                    listOfFragments[1]
                            .updateDataFromActivity(mWeather.getCityName(), listOfData[1]);
                }
                return listOfFragments[1];

            case 2:
                if (listOfFragments[2] == null) {
                    final WeatherFragment dayFragment2 =
                            WeatherFragment.newInstance(listOfData[2], mWeather.getCityName());
                    dayFragment2.setAdapter(this);
                    listOfFragments[2] = dayFragment2;
                } else {
                    listOfFragments[2].updateDataFromActivity(mWeather.getCityName(), listOfData[2]);
                }

                return listOfFragments[2];

            case 3:
                if (listOfFragments[3] == null) {
                    final WeatherFragment dayFragment3 =
                            WeatherFragment.newInstance(listOfData[3], mWeather.getCityName());
                    dayFragment3.setAdapter(this);
                    listOfFragments[3] = dayFragment3;
                } else {
                    listOfFragments[3].updateDataFromActivity(mWeather.getCityName(), listOfData[3]);
                }

                return listOfFragments[3];

            case 4:
                if (listOfFragments[4] == null) {
                    final WeatherFragment dayFragment4 =
                            WeatherFragment.newInstance(listOfData[4], mWeather.getCityName());
                    dayFragment4.setAdapter(this);
                    listOfFragments[4] = dayFragment4;
                } else {
                    listOfFragments[4].updateDataFromActivity(mWeather.getCityName(), listOfData[4]);
                }

                return listOfFragments[4];

            case 5:
                if (listOfFragments[5] == null) {
                    final WeatherFragment dayFragment5 =
                            WeatherFragment.newInstance(listOfData[5], mWeather.getCityName());
                    dayFragment5.setAdapter(this);
                    listOfFragments[5] = dayFragment5;
                } else {
                    listOfFragments[5].updateDataFromActivity(mWeather.getCityName(), listOfData[5]);
                }

                return listOfFragments[5];

            case 6:
                if (listOfFragments[6] == null) {
                    final WeatherFragment dayFragment6 =
                            WeatherFragment.newInstance(listOfData[6], mWeather.getCityName());
                    dayFragment6.setAdapter(this);
                    listOfFragments[6] = dayFragment6;

                } else {
                    listOfFragments[6].updateDataFromActivity(mWeather.getCityName(), listOfData[6]);
                }

                return listOfFragments[6];

            default:
                if (listOfFragments[0] == null) {
                    final WeatherFragment defaultFragment =
                            WeatherFragment.newInstance(mCurrently, mWeather.getCityName());
                    defaultFragment.setAdapter(this);
                    listOfFragments[0] = defaultFragment;
                } else {
                    listOfFragments[0].updateDataFromActivity(mCurrently, mWeather.getCityName());
                }
                return listOfFragments[0];
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return listOfData.length;
    }

    public void updateWeatherOnFragment(final Weather weatherData) {
        mWeather = weatherData;
        mCurrently = weatherData.getCurrently();

        if (listOfFragments[0] != null) {
            listOfFragments[0].updateDataFromActivity(mCurrently, weatherData.getCityName());
        }

        setupDailyData(weatherData.getDaily().getData(),
                weatherData.getTimezone(),
                listOfData);

        notifyDataSetChanged();
    }
}
