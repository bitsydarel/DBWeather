package com.darelbitsy.dbweather.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.news.News;
import com.darelbitsy.dbweather.ui.AdMobFragment;
import com.darelbitsy.dbweather.ui.CurrentWeatherFragment;
import com.darelbitsy.dbweather.ui.HourlyFragment;
import com.darelbitsy.dbweather.ui.LunarFragment;
import com.darelbitsy.dbweather.ui.ResumeWeatherFragment;

/**
 * Created by Darel Bitsy on 13/02/17.
 */

public class CustomFragmentAdapter extends FragmentPagerAdapter {
    private final Fragment[] listFragments = new Fragment[5];

    public CustomFragmentAdapter(FragmentManager fm, WeatherApi weatherApi, News[] newses) {
        super(fm);
        listFragments[0] = CurrentWeatherFragment.newInstance(weatherApi, newses);
        
        listFragments[1] = new ResumeWeatherFragment();
        listFragments[3] = new HourlyFragment();
        listFragments[2] = new AdMobFragment();
        listFragments[4] = new LunarFragment();

    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return listFragments[position];
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return listFragments.length;
    }
}
