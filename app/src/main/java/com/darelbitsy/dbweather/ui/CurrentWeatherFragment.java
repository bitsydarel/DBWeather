package com.darelbitsy.dbweather.ui;

import android.app.Fragment;
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
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.adapters.NewsAdapter;
import com.darelbitsy.dbweather.helper.GetNewsData;
import com.darelbitsy.dbweather.helper.GetWeatherData;
import com.darelbitsy.dbweather.news.News;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.darelbitsy.dbweather.ui.MainActivity.TAG;

/**
 * Created by Darel Bitsy on 10/02/17.
 * this Fragment display the current weather
 * data
 */

public class CurrentWeatherFragment extends Fragment {
    @BindView(R2.id.activity_main)
    RelativeLayout mMainLayout;

    //Defining TextView needed
    @BindView(R2.id.temperatureLabel)
    TextView mTemperatureLabel;
    @BindView(R2.id.timeLabel) TextView mTimeLabel;
    @BindView(R2.id.humidityValue) TextView mHumidityValue;
    @BindView(R2.id.locationLabel) TextView mLocationLabel;
    @BindView(R2.id.precipValue) TextView mPrecipValue;
    @BindView(R2.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R2.id.weekSummary) TextView mWeekSummary;

    //Defining ImageView and ImageButton to manipulate
    @BindView(R2.id.iconImageView)
    ImageView mIconImageView;

    private Button currentFocusedButton;
    private HorizontalScrollView mScrollView;
    private RelativeLayout.LayoutParams mParams;
    private Handler mHandler;
    private Button currentDayButton;
    private String currentDayName;
    private WeatherApi mWeather;
    private Day[] mDays;
    private final ColorManager mColorPicker = new ColorManager();
    private GetWeatherData mWeatherData;
    private GetNewsData mNewsData;
    private News[] mNewses;

    public static CurrentWeatherFragment newInstance(WeatherApi weather, News[] newses) {
        CurrentWeatherFragment currentWeatherFragment = new CurrentWeatherFragment();
        Bundle args = new Bundle();
        args.putParcelable(WelcomeActivity.WEATHER_DATA_KEY, weather);
        args.putParcelableArray(WelcomeActivity.NEWS_DATA_KEY, newses);
        currentWeatherFragment.setArguments(args);
        return currentWeatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeather = getArguments().getParcelable(WelcomeActivity.WEATHER_DATA_KEY);
        mNewses = (News[]) getArguments().getParcelableArray(WelcomeActivity.NEWS_DATA_KEY);
        mDays = mWeather.getDay();
        mWeatherData = new GetWeatherData(getActivity());
        mNewsData = new GetNewsData(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        ButterKnife.bind(this, view);
        AndroidThreeTen.init(getActivity());

        initialize(view, mWeather.getCurrent());
        view.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getCurrent().getIcon()));
        return view;
    }

    private void initialize(View view, Current current) {
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0099cc"),
                Color.parseColor("#ff33b5e5"),
                Color.parseColor("#ff99cc00"),
                Color.parseColor("#ff669900"));

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getActivity().runOnUiThread(() -> {
                mWeatherData.execute();
                mNewsData.execute();
            });

            mWeather = mWeatherData.getWeatherApi();
            mNewses = mNewsData.getNewses();
            updateDisplay(view);
            refreshLayout.setRefreshing(false);
        });

        mParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mTemperatureLabel.setText(String.format(Locale.ENGLISH,
                "%d",
                current.getTemperature()));

        mLocationLabel.setText(current.getCityName());
        if(current.getTime() > 0) {
            mTimeLabel.setText(String.format(Locale.getDefault(), getString(R.string.time_label), current.getFormattedTime()));
        }
        mHumidityValue.setText(String.format(Locale.ENGLISH,
                "%d%%",
                current.getHumidity()));
        mPrecipValue.setText(String.format(Locale.getDefault(), "%d", current.getPrecipChance()));
        mSummaryLabel.setText(current.getSummary());
        mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), current.getIconId()));

        setupDayScrollView(view);
        setupNewsScrollView(view);
    }

    /**
     * Setup the news scroll view and fetch it with data if available
     */
    private void setupNewsScrollView(View view) {
        final RecyclerView newsRecyclerView = (RecyclerView) view.findViewById(R.id.newsRecyclerView);

            if(mNewses != null && mNewses[3] != null) {
                NewsAdapter newsAdapter = new NewsAdapter(Arrays.copyOf(mNewses,
                        mNewses.length,
                        News[].class));
                newsRecyclerView.setAdapter(newsAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                newsRecyclerView.setLayoutManager(layoutManager);
                newsRecyclerView.setHasFixedSize(true);
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
        if(mDays.length > 0) {
            for(Day day : mDays) {
                if (dayName.equalsIgnoreCase(getResources()
                        .getString(R.string.today_label))) {
                    Current current = mWeather.getCurrent();

                    mTemperatureLabel.setText(String.format(Locale.getDefault(),
                            "%d",
                            current.getTemperature()));
                    mTimeLabel.setText(String.format(Locale.getDefault(),
                            getString(R.string.time_label),
                            current.getFormattedTime()));

                    mHumidityValue.setText(String.format(Locale.ENGLISH,
                            "%d%%",
                            current.getHumidity()));
                    mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", current.getPrecipChance()));
                    mSummaryLabel.setText(current.getSummary());
                    mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), current.getIconId()));
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(current.getIcon()));
                }

                if(dayName.equalsIgnoreCase(getResources().getString(R.string.tomorrow_label))) {
                    mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mDays[1].getTemperatureMax()));
                    mTimeLabel.setText(dayName);
                    mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", mDays[1].getHumidity()));
                    mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", mDays[1].getPrecipChance()));
                    mSummaryLabel.setText(mDays[1].getSummary());

                    mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), mDays[1].getIconId()));
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mDays[1].getIcon()));
                }

                if(day.getDayOfTheWeek().equalsIgnoreCase(dayName)
                        && !day.getDayOfTheWeek().equalsIgnoreCase(currentDayName)) {
                    mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", day.getTemperatureMax()));
                    mTimeLabel.setText(dayName);
                    mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", day.getHumidity()));
                    mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", day.getPrecipChance()));
                    mSummaryLabel.setText(day.getSummary());

                    mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), day.getIconId()));
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(day.getIcon()));

                }
            }
        }
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
            view.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = (Button) theView;
            showWeatherByDay(((Button) theView).getText().toString());
        };

        if(mDays[7] != null) {
            int count = 0;
            Button[] listOfButton = {mondayButton,
                    tuesdayButton,
                    wednesdayButton,
                    thursdayButton,
                    fridayButton,
                    saturdayButton,
                    sundayButton};

            for(Day day : mDays) {
                if(count < 7)  {
                    listOfButton[count].setOnClickListener(buttonListener);
                    if (count == 1) {
                        listOfButton[count].setText(getResources()
                                .getString(R.string.tomorrow_label));
                        count++;

                    } else if (currentDayName.equalsIgnoreCase(day.getDayOfTheWeek())) {
                        scrollToFunc(mHandler,
                                mScrollView,
                                listOfButton[count]);
                        currentDayButton = listOfButton[count];
                        listOfButton[count].setText(getResources().getString(R.string.today_label));
                        count++;
                    } else {
                        listOfButton[count].setText(day.getDayOfTheWeek());
                        count++;
                    }
                }
                else { break; }
            }
        } else {
            HashMap<String, Integer> dayOfTheWeek = (HashMap) calendar.getDisplayNames(Calendar.DAY_OF_WEEK,
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

    /**
     *  this function Fetch the layout with the new data
     */
    private void updateDisplay(View view) {
        if (mWeather.getDay().length > 0) { setupDayScrollView(view); }
        if(currentFocusedButton != null) {
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
        }
        scrollToFunc(mHandler, mScrollView, currentDayButton);

        mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getTemperature()));
        mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + " it will be");
        mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getCurrent().getIcon()));

        //Setting the location to the current location of the device because the api only provide the timezone as location
        mLocationLabel.setText(mWeather.getCurrent().getCityName());
        Log.i(TAG, "the City Name: "+mWeather.getCurrent().getCityName());

        mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getCurrent().getHumidity()));
        mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getCurrent().getPrecipChance()));
        mSummaryLabel.setText(mWeather.getCurrent().getSummary());

        mIconImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                mWeather.getCurrent().getIconId()));
        view.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getCurrent().getIcon()));
    }
}
