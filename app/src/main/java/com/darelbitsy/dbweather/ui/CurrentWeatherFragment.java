package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.NewsAdapter;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.api.GetNewsesHelper;
import com.darelbitsy.dbweather.helper.api.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.news.News;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.Daily;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.widgets.RainFallView;
import com.darelbitsy.dbweather.widgets.SnowFallView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Darel Bitsy on 10/02/17.
 * this Fragment display the current weather
 * data
 */

public class CurrentWeatherFragment extends Fragment {
    @BindView(R2.id.current_weather_layout)
    RelativeLayout mMainLayout;

    //Defining TextView needed
    @BindView(R2.id.temperatureLabel)
    TextView mTemperatureLabel;
    @BindView(R2.id.apparentTemperature) TextView mApparentTemperature;
    @BindView(R2.id.timeLabel) TextView mTimeLabel;
    @BindView(R2.id.humidityValue) TextView mHumidityValue;
    @BindView(R2.id.locationLabel) TextView mLocationLabel;
    @BindView(R2.id.precipValue) TextView mPrecipValue;
    @BindView(R2.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R2.id.sunriseTime) TextView mSunriseTimeValue;
    @BindView(R2.id.sunsetTime) TextView mSunsetTimeValue;
    @BindView(R2.id.windSpeedValue) TextView mWindSpeedValue;
    @BindView(R2.id.cloudCoverValue) TextView mCloudCoverValue;

    //Defining ImageView and ImageButton to manipulate
    @BindView(R2.id.iconImageView)
    ImageView mIconImageView;

    private final ColorManager mColorPicker = new ColorManager();

    private Button currentFocusedButton;
    private HorizontalScrollView mScrollView;
    private RelativeLayout.LayoutParams mParams;
    private Handler mHandler;
    private Button currentDayButton;
    private String currentDayName;
    private String nextDayName;
    private RecyclerView mNewsRecyclerView;

    private ArrayList<News> mNewses;
    private DatabaseOperation mDatabase;
    private Currently mCurrently;
    private Daily mDailyData;
    private String mCityName;
    private String mTimeZone;
    private NewsAdapter mNewsAdapter;
    private View mView;
    private SwipeRefreshLayout refreshLayout;
    public static CurrentWeatherFragment newInstance(Currently currently, Daily dailyData, ArrayList<News> newses, String cityName) {
        CurrentWeatherFragment currentWeatherFragment = new CurrentWeatherFragment();

        Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.CURRENT_WEATHER_KEY, currently);
        args.putParcelable(ConstantHolder.DAILY_WEATHER_KEY, dailyData);
        args.putParcelableArrayList(ConstantHolder.NEWS_DATA_KEY, newses);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        currentWeatherFragment.setArguments(args);
        return currentWeatherFragment;
    }

    public void updateDataFromActivity(Currently currently, Daily dailyData, String cityName) {
        mCurrently = currently;
        mDailyData = dailyData;
        mCityName = cityName;
        updateDisplay(mView);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mCurrently = args.getParcelable(ConstantHolder.CURRENT_WEATHER_KEY);
        mDailyData = args.getParcelable(ConstantHolder.DAILY_WEATHER_KEY);
        mNewses = args.getParcelableArrayList(ConstantHolder.NEWS_DATA_KEY);
        mCityName = args.getString(ConstantHolder.CITY_NAME_KEY);
        mDatabase = new DatabaseOperation(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.current_weather_layout, container, false);
        ButterKnife.bind(this, mView);
        AndroidThreeTen.init(getActivity());


        initialize(mView);
        mView.setBackgroundResource(mColorPicker.getBackgroundColor(mCurrently.getIcon()));
        return mView;
    }

    /**
     *  this function Fetch the layout with the new data
     */
    private void updateDisplay(View view) {
        if (mDailyData.getData().size() > 0) { setupDayScrollView(view); }

        if(currentFocusedButton != null) {
            currentFocusedButton
                    .setBackgroundColor(Color
                            .parseColor("#30ffffff"));
        }

        scrollToFunc(mHandler, mScrollView, currentDayButton);

        setCurrentWeather();

        setupDayScrollView(view);
    }

    private class GetWeather extends GetWeatherHelper {
        GetWeather(Activity activity) { super(activity); }

        @Override
        protected void onPostExecute(Weather weather) {
            mCityName = weather.getCityName();
            mTimeZone = weather.getTimezone();
            mCurrently = weather.getCurrently();
            mDailyData = weather.getDaily();
            updateDisplay(mView);
            refreshLayout.setRefreshing(false);
        }
    }

    private class GetNewses extends GetNewsesHelper {

        GetNewses(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(ArrayList<News> newses) {
            mNewsAdapter.updateContent(newses);
        }
    }

    private void initialize(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0099cc"),
                Color.parseColor("#ff33b5e5"),
                Color.parseColor("#ff99cc00"),
                Color.parseColor("#ff669900"));

        mNewses = mDatabase.getNewFromDatabase();
        mCurrently = mDatabase.getCurrentWeatherFromDatabase();
        mDailyData.setData(mDatabase.getDailyWeatherFromDatabase());

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            new GetWeather(getActivity()).execute();
            new GetNewses(view.getContext()).execute();
        });

        mParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        if (mCurrently != null) {
            setCurrentWeather();
        }
        setupDayScrollView(view);
        setupNewsScrollView(view);
    }

    private void setCurrentWeather() {
        mTemperatureLabel.setText(String.format(Locale.ENGLISH,
                "%d",
                WeatherUtil.getTemperatureInInt(mCurrently.getTemperature())));

        mApparentTemperature.setText(String.format(Locale.ENGLISH,
                getString(R.string.apparentTemperatureValue),
                WeatherUtil.getTemperatureInInt(mCurrently.getApparentTemperature())));

        mTimeLabel.setText(String.format(Locale.getDefault(),
                getString(R.string.time_label),
                WeatherUtil.getFormattedTime(mCurrently.getTime(), mTimeZone)));

        mHumidityValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.humidity_value),
                WeatherUtil.getHumidityPourcentage(mCurrently.getHumidity())));

        //Setting the location to the current location of the device because the api only provide the timezone as location
        mLocationLabel.setText(mCityName);
        Log.i(ConstantHolder.TAG, "the City Name: "+mCityName);

        mPrecipValue.setText(String.format(Locale.getDefault(),
                getString(R.string.precipChanceValue),
                WeatherUtil.getPrecipPourcentage(mCurrently.getPrecipProbability())));

        mSummaryLabel.setText(mCurrently.getSummary());

        mWindSpeedValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.windSpeedValue),
                WeatherUtil.getWindSpeedMeterPerHour(mCurrently.getWindSpeed())));

        mCloudCoverValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.cloudCoverValue),
                WeatherUtil.getCloudCoverPourcentage(mCurrently.getCloudCover())));

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                WeatherUtil.getIconId(mCurrently.getIcon())));

        if (mDailyData.getData() != null) {
            for (DailyData day : mDailyData.getData()) {
                String today = WeatherUtil.getDayOfTheWeek(mCurrently.getTime(), mTimeZone);

                if (WeatherUtil
                        .getDayOfTheWeek(day.getTime(), mTimeZone)
                        .equals(today)) {

                    mSunriseTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunriseTime(),
                            mTimeZone));

                    mSunsetTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunsetTime(), mTimeZone));

                }
            }
        }

        mMainLayout.setBackgroundResource(mColorPicker
                .getBackgroundColor(mCurrently.getIcon()));

        if("snow".equalsIgnoreCase(mCurrently.getIcon())) {

            if(mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                mMainLayout.addView(new SnowFallView(getActivity()), mParams);
            }

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
            }

        }
        else if("rain".equalsIgnoreCase(mCurrently.getIcon())) {

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                mMainLayout.addView(new RainFallView(getActivity()), mParams);
            }
            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
            }

        } else if ("sleet".equalsIgnoreCase(mCurrently.getIcon())) {

            if (mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                mMainLayout.addView(new RainFallView(getActivity()), mParams);
            }
            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                mMainLayout.addView(new SnowFallView(getActivity()), mParams);
            }

        } else {

            if(mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
            }
            if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
            }

        }

    }

    /**
     * Setup the news scroll view and fetch it with data if available
     */
    private void setupNewsScrollView(View view) {
        final HorizontalScrollView newsHorizontallSV = (HorizontalScrollView) view.findViewById(R.id.newsHorizontallSV);
        mNewsRecyclerView = (RecyclerView) newsHorizontallSV.findViewById(R.id.newsRecyclerView);

            if(mNewses != null && mNewses.get(3) != null) {
                mNewsAdapter = new NewsAdapter(mNewses);
                mNewsRecyclerView.setAdapter(mNewsAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mNewsRecyclerView.setLayoutManager(layoutManager);
                mNewsRecyclerView.setHasFixedSize(true);
            }
    }

    /**
     * @param handler to process the action
     * @param scrollView where to apply the function
     * @param button clicked button
     */
    private void scrollToFunc(Handler handler, HorizontalScrollView scrollView, Button button) {
        if(button != null) {
            handler.post(() -> {
                scrollView.scrollTo(button.getLeft(), button.getTop());
                scrollView.setSmoothScrollingEnabled(true);
                button.setBackgroundColor(Color.parseColor("#80ffffff"));
                currentFocusedButton = button;
            });
        }
    }

    /**
     * Show the weather of the choosed day
     * @param dayName is the day name
     */
    private void showWeatherByDay(String dayName) {
        if(mDailyData.getData().size() > 0) {
            for(DailyData day : mDailyData.getData()) {
                if (dayName.equalsIgnoreCase(getResources()
                        .getString(R.string.today_label))
                        && WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone).equalsIgnoreCase(currentDayName)) {

                    mTemperatureLabel.setText(String.format(Locale.ENGLISH,
                            "%d",
                            WeatherUtil.getTemperatureInInt(mCurrently.getTemperature())));

                    mApparentTemperature.setText(String.format(Locale.ENGLISH,
                            getString(R.string.apparentTemperatureValue),
                            WeatherUtil.getTemperatureInInt(mCurrently.getApparentTemperature())));

                    mTimeLabel.setText(String.format(Locale.getDefault(),
                            getString(R.string.time_label),
                            WeatherUtil.getFormattedTime(mCurrently.getTime(), mTimeZone)));

                    mHumidityValue.setText(String.format(Locale.ENGLISH,
                            getString(R.string.humidity_value),
                            WeatherUtil.getHumidityPourcentage(mCurrently.getHumidity())));

                    mPrecipValue.setText(String.format(Locale.getDefault(),
                            getString(R.string.precipChanceValue),
                            WeatherUtil.getPrecipPourcentage(mCurrently.getPrecipProbability())));

                    mSummaryLabel.setText(mCurrently.getSummary());

                    mSunriseTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunriseTime(), mTimeZone));
                    mSunsetTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunsetTime(), mTimeZone));

                    mWindSpeedValue.setText(WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed()));
                    mCloudCoverValue.setText(WeatherUtil.getCloudCoverPourcentage(day.getCloudCover()));

                    mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), WeatherUtil.getIconId(mCurrently.getIcon())));
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mCurrently.getIcon()));
                }

                if(dayName.equalsIgnoreCase(getResources().getString(R.string.tomorrow_label))
                        && WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone).equals(nextDayName)) {

                    showDayData(dayName, day);

                }

                if(WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone).equalsIgnoreCase(dayName)
                        && !WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone).equalsIgnoreCase(currentDayName)) {

                    showDayData(dayName, day);

                }
            }
        }
    }

    private void showDayData(String dayName, DailyData day) {
        mTemperatureLabel.setText(String.format(Locale.ENGLISH,
                "%d",
                WeatherUtil.getTemperatureInInt(day.getTemperatureMax())));

        mApparentTemperature.setText(String.format(Locale.getDefault(),
                getString(R.string.apparentTemperatureValue),
                WeatherUtil.getTemperatureInInt(day.getApparentTemperatureMax())));

        mTimeLabel.setText(dayName);

        mHumidityValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.humidity_value),
                WeatherUtil.getHumidityPourcentage(day.getHumidity())));

        mPrecipValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.precipChanceValue),
                WeatherUtil.getPrecipPourcentage(day.getPrecipProbability())));

        mSummaryLabel.setText(day.getSummary());

        mSunriseTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunriseTime(), mTimeZone));
        mSunsetTimeValue.setText(WeatherUtil.getFormattedTime(day.getSunsetTime(), mTimeZone));

        mWindSpeedValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.windSpeedValue),
                WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed())));

        mCloudCoverValue.setText(String.format(Locale.ENGLISH,
                getString(R.string.cloudCoverValue),
                WeatherUtil.getCloudCoverPourcentage(day.getCloudCover())));

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), WeatherUtil.getIconId(day.getIcon())));
        mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(day.getIcon()));
    }

    private void setupDayScrollView(View view) {
        mScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScroll);
        final Button mondayButton = (Button) view.findViewById(R.id.monday);
        final Button tuesdayButton = (Button) view.findViewById(R.id.tuesday);
        final Button wednesdayButton = (Button) view.findViewById(R.id.wednesday);
        final Button thursdayButton = (Button) view.findViewById(R.id.thursday);
        final Button fridayButton = (Button) view.findViewById(R.id.friday);
        final Button saturdayButton = (Button) view.findViewById(R.id.saturday);
        final Button sundayButton = (Button) view.findViewById(R.id.sunday);

        mHandler = new Handler();
        Calendar calendar = Calendar.getInstance();
        currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        final View.OnClickListener buttonListener = theView -> {
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
            theView.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = (Button) theView;
            showWeatherByDay(((Button) theView).getText().toString());
        };

        if(mDailyData.getData().get(7) != null) {
            int count = 0;
            Integer currentDayIndex = null;

            Button[] listOfButton = {mondayButton,
                    tuesdayButton,
                    wednesdayButton,
                    thursdayButton,
                    fridayButton,
                    saturdayButton,
                    sundayButton};

            for(DailyData day : mDailyData.getData()) {
                if(count < 7)  {
                    listOfButton[count].setOnClickListener(buttonListener);
                    if (currentDayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone))) {
                        scrollToFunc(mHandler,
                                mScrollView,
                                listOfButton[count]);
                        currentDayButton = listOfButton[count];
                        listOfButton[count].setText(getResources().getString(R.string.today_label));
                        currentDayIndex = count;
                        count++;

                    } else if (currentDayIndex != null
                            && count == (currentDayIndex + 1)) {

                        listOfButton[count].setText(getResources()
                                .getString(R.string.tomorrow_label));
                        nextDayName = WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone);
                        count++;

                    } else {
                        listOfButton[count].setText(WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone));
                        count++;
                    }
                }
                else { break; }
            }
        } else {
            HashMap<String, Integer> dayOfTheWeek = (HashMap<String, Integer>) calendar.getDisplayNames(Calendar.DAY_OF_WEEK,
                    Calendar.LONG,
                    Locale.getDefault());

            for (HashMap.Entry<String, Integer> entry : dayOfTheWeek.entrySet()){
                String dayName = entry.getKey();
                int dayId = entry.getValue();

                if(dayId == Calendar.MONDAY) {
                    mondayButton.setText(dayName);
                    mondayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, mondayButton);
                        mondayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = mondayButton;
                    }
                }
                if(dayId == Calendar.TUESDAY) {
                    tuesdayButton.setText(dayName);
                    tuesdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, tuesdayButton);
                        tuesdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = tuesdayButton;
                    }
                }
                if(dayId == Calendar.WEDNESDAY) {
                    wednesdayButton.setText(dayName);
                    wednesdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, wednesdayButton);
                        wednesdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = wednesdayButton;
                    }
                }
                if(dayId == Calendar.THURSDAY) {
                    thursdayButton.setText(dayName);
                    thursdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, thursdayButton);
                        thursdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = thursdayButton;
                    }
                }
                if(dayId == Calendar.FRIDAY) {
                    fridayButton.setText(dayName);
                    fridayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, fridayButton);
                        fridayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = fridayButton;
                    }
                }
                if(dayId == Calendar.SATURDAY) {
                    saturdayButton.setText(dayName);
                    saturdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, saturdayButton);
                        saturdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = saturdayButton;
                    }
                }
                if(dayId == Calendar.SUNDAY) {
                    sundayButton.setText(dayName);
                    sundayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, sundayButton);
                        sundayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = sundayButton;
                    }
                }
            }
        }
    }
}
