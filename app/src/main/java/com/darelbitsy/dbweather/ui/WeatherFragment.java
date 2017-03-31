package com.darelbitsy.dbweather.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.CustomFragmentAdapter;
import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.services.LocationTracker;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.ui.helper.DaySwitcherHelper;
import com.darelbitsy.dbweather.ui.widgets.RainFallView;
import com.darelbitsy.dbweather.ui.widgets.SnowFallView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil.mColorPicker;
import static com.darelbitsy.dbweather.ui.MainActivity.subscriptions;

/**
 * Created by Darel Bitsy on 23/03/17.
 * Weather fragment he present weather data to the user.
 */

public class WeatherFragment extends Fragment {
    @BindView(R.id.current_weather_layout)
    ConstraintLayout mMainLayout;

    private RelativeLayout.LayoutParams mParams;
    private DatabaseOperation mDatabase;
    private Currently mCurrently;
    private DailyData mDailyData;
    private String mCityName;
    private String mTimeZone;

    private View mCurrentView;
    private SwipeRefreshLayout refreshLayout;
    private final Handler mHandler = new Handler();

    private Single<Weather> mWeatherObservableWithNetwork;
    private Single<Weather> mWeatherObservableWithoutNetwork;

    private DaySwitcherHelper mDaySwitcherHelper;
    private CustomFragmentAdapter mAdapter;

    public static WeatherFragment newInstance(Currently currently, String cityName) {
        WeatherFragment weatherFragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.CURRENT_WEATHER_KEY, currently);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    public static WeatherFragment newInstance(DailyData dailyData, String cityName) {
        WeatherFragment weatherFragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.DAY_WEATHER_KEY, dailyData);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    public void setAdapter(CustomFragmentAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDatabase = new DatabaseOperation(context);
        setupObservables(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        mCurrently = args.getParcelable(ConstantHolder.CURRENT_WEATHER_KEY);
        mDailyData = args.getParcelable(ConstantHolder.DAY_WEATHER_KEY);
        mCityName = args.getString(ConstantHolder.CITY_NAME_KEY);
        subscriptions.add(mWeatherObservableWithoutNetwork
                .subscribeWith(new WeatherFragment.CurrentWeatherObserver()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.current_weather_layout, container, false);
        ButterKnife.bind(this, mCurrentView);
        AndroidThreeTen.init(getActivity());

        mDaySwitcherHelper = new DaySwitcherHelper(this,
                mCurrentView,
                mCityName);

        return mCurrentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler.post(() -> initialize(mCurrentView));

        if (mCurrently != null) {

            setupBackground(mCurrently.getIcon());
            if (AppUtil.isGpsPermissionOn(getActivity())) {
                mHandler.post(() -> getActivity()
                        .startService(new Intent(getActivity(), LocationTracker.class)));
            }

        } else {
            setupBackground(mDailyData.getIcon());
        }
    }

    private void initialize(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0099cc"),
                Color.parseColor("#ff33b5e5"),
                Color.parseColor("#ff99cc00"),
                Color.parseColor("#ff669900"));

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            updateData();
        });

        mParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        if (mCurrently != null) {
            mHandler.post(() -> {
                mDaySwitcherHelper.setCurrentViews(getContext(),
                        mCurrently,
                        mTimeZone,
                        mCurrently.getSunriseTime(),
                        mCurrently.getSunsetTime());

                showFallingSnowOrRain();
            });
        }

        if (mDailyData != null) {
            mHandler.post(() ->
                    mDaySwitcherHelper
                            .showDayData(getContext(),
                                    WeatherUtil.getDayOfTheWeek(mDailyData.getTime(), mTimeZone),
                                    mDailyData, mTimeZone));
        }

    }

    private void setupBackground(String icon) {
        if ("rain".equals(icon)) {
            mHandler.post(() -> AppUtil
                    .setupVideoBackground(R.raw.rain_background, getActivity(), mCurrentView));

        } else if ("snow".equals(icon)) {
            mHandler.post(() -> AppUtil
                    .setupVideoBackground(R.raw.snow_background, getActivity(), mCurrentView));

        } else {
            VideoView videoView = (VideoView)
                    mCurrentView.findViewById(R.id.backgroundVideo);

            videoView.stopPlayback();
            videoView.refreshDrawableState();
            videoView.setVisibility(View.INVISIBLE);

            mCurrentView.setBackgroundResource(mColorPicker.getBackgroundColor(icon));
        }
    }

    /**
     *  this function Fetch the layout with the new data
     */
    private void updateDisplay() {
        mHandler.post(() -> {
            if (mCurrently != null) {
                mDaySwitcherHelper.setCurrentViews(getContext(),
                        mCurrently,
                        mTimeZone,
                        mCurrently.getSunriseTime(),
                        mCurrently.getSunsetTime());

                showFallingSnowOrRain();
            }
            if (mDailyData != null) {
                mDaySwitcherHelper.showDayData(getContext(), WeatherUtil.getDayOfTheWeek(mDailyData.getTime(), mTimeZone),
                        mDailyData, mTimeZone);
            }
        });
    }

    private void showFallingSnowOrRain() {
        if("snow".equalsIgnoreCase(mCurrently.getIcon())) {

            if(mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                mMainLayout.addView(new SnowFallView(getActivity()), mParams);
            }

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
            }

            new Handler().post(() -> AppUtil.setupVideoBackground(R.raw.rain_background,
                    getActivity(),
                    mMainLayout));

        } else if("rain".equalsIgnoreCase(mCurrently.getIcon())) {

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                mMainLayout.addView(new RainFallView(getActivity()), mParams);
            }

            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
            }

            new Handler().post(() -> AppUtil
                    .setupVideoBackground(R.raw.snow_background, getActivity(), mMainLayout));

        } else if ("sleet".equalsIgnoreCase(mCurrently.getIcon())) {

            if (mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                    mMainLayout.addView(new RainFallView(getActivity()), mParams);
            }

            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                mMainLayout.addView(new SnowFallView(getActivity()), mParams);
            }

        } else {

            if (mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
            }

            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
            }
        }
    }

    private void updateData() {
        if (AppUtil.isNetworkAvailable(getActivity())) {
            subscriptions.add(mWeatherObservableWithNetwork
                    .subscribeWith(new CurrentWeatherObserver()));

        } else {
            mWeatherObservableWithoutNetwork
                    .subscribeWith(new CurrentWeatherObserver());

        }
    }

    public void updateDataFromActivity(Currently currently, String cityName) {
        if (mCurrently != null) {
            mCurrently = currently;
            mHandler.post(WeatherFragment.this::updateDisplay);
        }
        mCityName = cityName;
        mDaySwitcherHelper.updateCityName(mCityName);
    }


    private void setupObservables(Context context) {
        mWeatherObservableWithNetwork = new GetWeatherHelper(context)
                .getObservableWeatherFromApi(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mWeatherObservableWithoutNetwork = new GetWeatherHelper(context)
                .getObservableWeatherFromDatabase(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private final class CurrentWeatherObserver extends DisposableSingleObserver<Weather> {
        @Override
        public void onSuccess(Weather weather) {
            Log.i(ConstantHolder.TAG, "Inside the currentWeatherObserver Fragment");
            mCityName = weather.getCityName();
            mTimeZone = weather.getTimezone();
            if (mCurrently != null) {
                mCurrently = weather.getCurrently();

            } else if (mDailyData != null){
                for (DailyData day: weather.getDaily().getData()) {
                    if (WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone)
                            .equals(WeatherUtil.getDayOfTheWeek(mDailyData.getTime(), mTimeZone))) {
                        mDailyData = day;
                    }
                }
            }

            if (mAdapter != null) { mAdapter.updateWeatherOnFragment(weather); }

            if (mDaySwitcherHelper != null) {
                mDaySwitcherHelper.updateCityName(mCityName);
            }

            mHandler.post(WeatherFragment.this::updateDisplay);

            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in weather fragment: " + e.getMessage());
        }
    }
}
