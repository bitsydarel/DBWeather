package com.darelbitsy.dbweather.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.darelbitsy.dbweather.helper.ColorManager;
import com.darelbitsy.dbweather.helper.MemoryLeakChecker;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.SELECTED_CITY_LATITUDE;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.SELECTED_CITY_LONGITUDE;

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
    private final ColorManager mColorManager = ColorManager.newInstance();
    private SharedPreferences mSharedPreferences;
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    public static WeatherFragment newInstance(final Currently currently,
                                              final String cityName) {

        final WeatherFragment weatherFragment = new WeatherFragment();

        final Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.CURRENT_WEATHER_KEY, currently);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    public static WeatherFragment newInstance(final DailyData dailyData,
                                              final String cityName) {
        final WeatherFragment weatherFragment = new WeatherFragment();

        final Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.DAY_WEATHER_KEY, dailyData);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    public void setAdapter(final CustomFragmentAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDatabase = DatabaseOperation.newInstance(context);
        setupObservables(context);
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();

        mCurrently = args.getParcelable(ConstantHolder.CURRENT_WEATHER_KEY);
        mDailyData = args.getParcelable(ConstantHolder.DAY_WEATHER_KEY);
        mCityName = args.getString(ConstantHolder.CITY_NAME_KEY);

        if (mSharedPreferences.getBoolean(IS_FROM_CITY_KEY, false) && mSharedPreferences.contains(SELECTED_CITY_LATITUDE)) {
            subscriptions.add(GetWeatherHelper.newInstance(getActivity())
                    .getObservableWeatherForCityFromApi(mCityName,
                            Double.longBitsToDouble(mSharedPreferences.getLong(SELECTED_CITY_LATITUDE, 0)),
                                    Double.longBitsToDouble(mSharedPreferences.getLong(SELECTED_CITY_LONGITUDE, 0)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new CurrentWeatherObserver()));
        } else {
            subscriptions.add(mWeatherObservableWithoutNetwork
                    .subscribeWith(new CurrentWeatherObserver()));
        }
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

        refreshLayout = (SwipeRefreshLayout) mCurrentView.findViewById(R.id.refreshLayout);
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
        return mCurrentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler.post(this::initialize);

        if (mCurrently != null && AppUtil.isGpsPermissionOn(getActivity())) {
            mHandler.post(() -> getActivity()
                    .startService(new Intent(getActivity().getApplicationContext(), LocationTracker.class)));
        }
    }

    @Override
    public void onDestroyView() {
        subscriptions.dispose();
        MemoryLeakChecker.getRefWatcher(getActivity()).watch(this);
        super.onDestroyView();
    }

    private void initialize() {

        if (mCurrently != null) {
            mHandler.post(() -> {
                mDaySwitcherHelper.setCurrentViews(mCurrentView,
                        mCurrently,
                        mTimeZone,
                        mCurrently.getSunriseTime(),
                        mCurrently.getSunsetTime());

                showFallingSnowOrRain();
            });

        } else if (mDailyData != null) {
            mHandler.post(() ->
                    mDaySwitcherHelper
                            .showDayData(mCurrentView,
                                    WeatherUtil.getDayOfTheWeek(mDailyData.getTime(), mTimeZone),
                                    mDailyData, mTimeZone));
        }

    }

    /**
     *  this function Fetch the layout with the new data
     */
    private void updateDisplay() {
        mHandler.post(() -> {
            if (mCurrently != null) {
                mDaySwitcherHelper.setCurrentViews(mCurrentView,
                        mCurrently,
                        mTimeZone,
                        mCurrently.getSunriseTime(),
                        mCurrently.getSunsetTime());

                showFallingSnowOrRain();
            }
            if (mDailyData != null) {
                mDaySwitcherHelper.showDayData(mCurrentView, WeatherUtil.getDayOfTheWeek(mDailyData.getTime(), mTimeZone),
                        mDailyData, mTimeZone);
            }
        });
    }

    private void showFallingSnowOrRain() {
        if("snow".equalsIgnoreCase(mCurrently.getIcon())) {

            if(mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                mMainLayout.addView(new SnowFallView(getActivity().getApplicationContext()), mParams);
            }

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
            }

            new Handler().post(() -> AppUtil.setupVideoBackground(R.raw.snow_background,
                    getActivity()
                            .getApplicationContext(),
                    mMainLayout));

        } else if("rain".equalsIgnoreCase(mCurrently.getIcon())) {

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                mMainLayout.addView(new RainFallView(getActivity()
                        .getApplicationContext()),
                        mParams);
            }

            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
            }

            new Handler().post(() -> AppUtil
                    .setupVideoBackground(R.raw.rain_background,
                            getActivity()
                                    .getApplicationContext(),
                            mMainLayout));

        } else if ("sleet".equalsIgnoreCase(mCurrently.getIcon())) {

            if (mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                    mMainLayout.addView(new RainFallView(getActivity()
                            .getApplicationContext()),
                            mParams);
            }

            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                mMainLayout.addView(new SnowFallView(getActivity()
                        .getApplicationContext()),
                        mParams);
            }

        } else {

            if (mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
            }

            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
            }

            VideoView videoView = (VideoView) mCurrentView.findViewById(R.id.backgroundVideo);
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
        }
    }

    private void updateData() {
        boolean isNetworkAvailable = AppUtil.isNetworkAvailable(getActivity()
                .getApplicationContext());

        if (mSharedPreferences.getBoolean(IS_FROM_CITY_KEY, false)
                && mSharedPreferences.contains(SELECTED_CITY_LATITUDE)
                && isNetworkAvailable) {

            subscriptions.add(GetWeatherHelper.newInstance(getActivity())
                    .getObservableWeatherForCityFromApi(mCityName,
                            Double.longBitsToDouble(mSharedPreferences.getLong(SELECTED_CITY_LATITUDE, 0)),
                            Double.longBitsToDouble(mSharedPreferences.getLong(SELECTED_CITY_LONGITUDE, 0)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new CurrentWeatherObserver()));

        } else {
            if (isNetworkAvailable) {
                subscriptions.add(mWeatherObservableWithNetwork
                        .subscribeWith(new CurrentWeatherObserver()));

            } else {
                mWeatherObservableWithoutNetwork
                        .subscribeWith(new CurrentWeatherObserver());

            }
        }
    }

    public void updateDataFromActivity(final Currently currently,
                                       final String cityName) {
        mCurrently = currently;
        mHandler.post(WeatherFragment.this::updateDisplay);
        mCityName = cityName;
        mDaySwitcherHelper.updateCityName(mCityName);
        mAdapter.getParentLayout()
                .setBackgroundResource(mColorManager
                        .getBackgroundColor(currently.getIcon()));
    }

    public void updateDataFromActivity(final String cityName) {
        mCityName = cityName;
        mDaySwitcherHelper.updateCityName(cityName);
        mHandler.post(WeatherFragment.this::updateDisplay);
    }


    private void setupObservables(final Context context) {
        mWeatherObservableWithNetwork = GetWeatherHelper.newInstance(context)
                .getObservableWeatherFromApi(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mWeatherObservableWithoutNetwork = GetWeatherHelper.newInstance(context)
                .getObservableWeatherFromDatabase(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private final class CurrentWeatherObserver extends DisposableSingleObserver<Weather> {
        @Override
        public void onSuccess(final Weather weather) {
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

            if(mAdapter != null) {
                if (mCurrently != null) {
                    mAdapter.getParentLayout()
                            .setBackgroundResource(mColorManager
                                    .getBackgroundColor(mCurrently.getIcon()));
                }

                mAdapter.updateWeatherOnFragment(weather);
            }

            if (mDaySwitcherHelper != null) {
                mDaySwitcherHelper.updateCityName(mCityName);
            }

            mHandler.post(WeatherFragment.this::updateDisplay);

            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in weather fragment: " + e.getMessage());
        }
    }
}
