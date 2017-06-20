package com.dbeginc.dbweather.ui.main.weather.viewpagerfragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.FragmentWeatherBinding;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherInfo;
import com.dbeginc.dbweather.utils.helper.ColorManager;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Fragment Representing weather info
 */

public class WeatherFragment extends Fragment implements IWeatherFragmentView<WeatherInfo> {

    private WeatherFragmentPresenter mPresenter;
    private FragmentWeatherBinding binding;
    private RelativeLayout.LayoutParams mParams;

    public static WeatherFragment newInstance(@NonNull final WeatherInfo weatherInfo) {
        final WeatherFragment weatherFragment = new WeatherFragment();

        final Bundle args = new Bundle();
        args.putParcelable(WEATHER_INFO_KEY, weatherInfo);
        weatherFragment.setArguments(args);

        return weatherFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new WeatherFragmentPresenter(this);
        final Bundle arguments = getArguments();

        if (arguments != null) {
            mPresenter.restoreState(arguments);
        } else if (savedInstanceState != null) {
            mPresenter.restoreState(savedInstanceState);
        }

        mParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(WEATHER_INFO_KEY, mPresenter.getWeatherInfo());
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false);
        binding.setWeatherInfo(mPresenter.getWeatherInfo());
        binding.currentWeatherLayout.setBackgroundResource(ColorManager.getInstance()
                .getBackgroundColor(mPresenter.getWeatherInfo().icon.get()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter.getWeatherInfo().isCurrentWeather.get()) {
            mPresenter.showFallingSnowOrRain(binding.currentWeatherLayout,
                    getActivity().getApplicationContext(), mParams);
        }
        binding.windSpeedValue.setAllCaps(false);
    }

    @Override
    public void showData(@NonNull final WeatherInfo weatherInfo) {
        mPresenter.showData(weatherInfo);
        binding.currentWeatherLayout.setBackgroundResource(ColorManager.getInstance()
                .getBackgroundColor(mPresenter.getWeatherInfo()
                        .icon.get()));

        if (mPresenter.getWeatherInfo().isCurrentWeather.get()) {
            mPresenter.showFallingSnowOrRain(binding.currentWeatherLayout,
                    binding.currentWeatherLayout.getContext(),
                    mParams);
        }
    }
}