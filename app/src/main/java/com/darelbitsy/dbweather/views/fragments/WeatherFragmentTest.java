package com.darelbitsy.dbweather.views.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.FragmentWeatherBinding;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.presenters.fragments.WeatherFragmentPresenter;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Fragment Representing weather info
 */

public class WeatherFragmentTest extends Fragment implements IWeatherFragmentView<WeatherInfo> {

    private WeatherFragmentPresenter mPresenter;
    private FragmentWeatherBinding mFragmentWeatherBinding;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new WeatherFragmentPresenter(this);

        if (savedInstanceState != null) {
            mPresenter.restoreState(savedInstanceState);

        } else {
            mPresenter.restoreState(getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        mFragmentWeatherBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false);

        return mFragmentWeatherBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mPresenter.showFallingSnowOrRain(mFragmentWeatherBinding.currentWeatherLayout, getActivity().getApplicationContext(), layoutParams);

        mFragmentWeatherBinding.refreshLayout.setOnRefreshListener(this::requestUpdate);
    }

    @Override
    public void showData(@NonNull final WeatherInfo weatherInfo) {
        mPresenter.showData(weatherInfo);
    }

    @Override
    public void requestUpdate() {
        final Intent updateRequest = new Intent(ConstantHolder.UPDATE_REQUEST);
        getActivity().sendBroadcast(updateRequest);
    }
}
