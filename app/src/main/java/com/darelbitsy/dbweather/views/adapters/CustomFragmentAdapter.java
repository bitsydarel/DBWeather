package com.darelbitsy.dbweather.views.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.views.fragments.WeatherFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Custom View Pager Adapter
 */

public class CustomFragmentAdapter extends android.support.v4.app.FragmentStatePagerAdapter  {

    private final List<WeatherInfo> mWeatherInfoList = new ArrayList<>();
    private final SparseArray<WeakReference<WeatherFragment>> listOfFragments = new SparseArray<>();

    public CustomFragmentAdapter(final FragmentManager fm,
                                 @NonNull final List<WeatherInfo> weatherInfoList) {
        super(fm);
        mWeatherInfoList.clear();
        mWeatherInfoList.addAll(weatherInfoList);
    }

    @Override
    public Fragment getItem(final int position) {
        final WeatherFragment weatherFragment = WeatherFragment.newInstance(mWeatherInfoList.get(position));
        listOfFragments.put(position, new WeakReference<>(weatherFragment));
        return weatherFragment;
    }

    @Override
    public int getCount() {
        return mWeatherInfoList.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final WeatherFragment weatherFragment = (WeatherFragment) super.instantiateItem(container, position);
        listOfFragments.put(position, new WeakReference<>(weatherFragment));
        return weatherFragment;
    }

    public void updateFragments(@NonNull final List<WeatherInfo> weatherInfoList) {
        mWeatherInfoList.clear();
        mWeatherInfoList.addAll(weatherInfoList);

        final int weatherInfoList_size = mWeatherInfoList.size();

        for (int index = 0; index < weatherInfoList_size; index++) {
            final WeakReference<WeatherFragment> weatherFragmentWeakReference = listOfFragments.get(index);

            if (weatherFragmentWeakReference != null) {
                final WeatherFragment weatherFragment = weatherFragmentWeakReference.get();

                if (weatherFragment != null) {
                    weatherFragment.showData(mWeatherInfoList.get(index));
                }
            }
        }
    }
}
