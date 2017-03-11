package com.darelbitsy.dbweather.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.NewsAdapter;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.api.GetNewsesHelper;
import com.darelbitsy.dbweather.helper.api.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.news.Article;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.Daily;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.services.LocationTracker;
import com.darelbitsy.dbweather.ui.animation.AnimationUtility;
import com.darelbitsy.dbweather.ui.helper.DaySwitcherHelper;
import com.darelbitsy.dbweather.ui.widgets.RainFallView;
import com.darelbitsy.dbweather.ui.widgets.SnowFallView;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.helper.utility.WeatherUtil.mColorPicker;
import static com.darelbitsy.dbweather.ui.MainActivity.subscriptions;

/**
 * Created by Darel Bitsy on 10/02/17.
 * this Fragment display the current weather
 * data
 */

public class CurrentWeatherFragment extends Fragment {
    @BindView(R2.id.current_weather_layout)
    RelativeLayout mMainLayout;

    private Button currentFocusedButton;
    private HorizontalScrollView mScrollView;
    private RelativeLayout.LayoutParams mParams;
    private Button currentDayButton;
    private String currentDayName;
    private String nextDayName;
    private RecyclerView mNewsRecyclerView;
    private ArrayList<Article> mNewses;
    private DatabaseOperation mDatabase;
    private Currently mCurrently;
    private Daily mDailyData;
    private String mCityName;
    private String mTimeZone;

    private NewsAdapter mNewsAdapter;
    private View mView;
    private SwipeRefreshLayout refreshLayout;
    private final Handler mHandler = new Handler();

    private Single<Weather> mWeatherObservableWithNetwork;
    private Single<Weather> mWeatherObservableWithoutNetwork;
    private Single<ArrayList<Article>> mNewsesObservableWithNetwork;
    private Single<ArrayList<Article>> mNewsesObservableWithoutNetwork;

    private boolean isSubscriptionDoneWithNetwork;
    private DaySwitcherHelper mDaySwitcherHelper;

    public static CurrentWeatherFragment newInstance(Currently currently, Daily dailyData, ArrayList<Article> newses, String cityName) {
        CurrentWeatherFragment currentWeatherFragment = new CurrentWeatherFragment();

        Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.CURRENT_WEATHER_KEY, currently);
        args.putParcelable(ConstantHolder.DAILY_WEATHER_KEY, dailyData);
        args.putParcelableArrayList(ConstantHolder.NEWS_DATA_KEY, newses);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        currentWeatherFragment.setArguments(args);
        return currentWeatherFragment;
    }

    private final class CurrentWeatherObserver extends DisposableSingleObserver<Weather> {
        @Override
        public void onSuccess(Weather weather) {
            Log.i(ConstantHolder.TAG, "Inside the currentWeatherObserver Fragment");
            mCityName = weather.getCityName();
            mTimeZone = weather.getTimezone();
            mCurrently = weather.getCurrently();
            mDailyData = weather.getDaily();

            if (mDaySwitcherHelper != null) {
                mDaySwitcherHelper.updateDailyData(mDailyData.getData());
                mDaySwitcherHelper.updateCurrentWeatherData(mCurrently);
                mDaySwitcherHelper.updateCityName(mCityName);
            }

            mHandler.post(() -> updateDisplay(mView));

            if (isSubscriptionDoneWithNetwork) {
                subscriptions.add(mNewsesObservableWithNetwork
                        .subscribeWith(new CurrentNewsesObserver()));

            } else {
                subscriptions.add(mNewsesObservableWithoutNetwork
                        .subscribeWith(new CurrentNewsesObserver()));
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in current fragment: " + e.getMessage());
        }
    }

    private final class CurrentNewsesObserver extends DisposableSingleObserver<ArrayList<Article>> {
        @Override
        public void onSuccess(ArrayList<Article> newses) {
            Log.i(ConstantHolder.TAG, "Inside the currentNewsesObserver Fragment");
            mNewses = newses;
            if (mNewsAdapter != null) {
                mHandler.post(() -> mNewsAdapter.updateContent(newses));
            }

            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.i(ConstantHolder.TAG, "Ho My God, got an error: " + e.getMessage());
        }
    }

    private void setupObservables() {
        mWeatherObservableWithNetwork = new GetWeatherHelper(getActivity())
                .getObservableWeatherFromApi(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mWeatherObservableWithoutNetwork = new GetWeatherHelper(getActivity())
                .getObservableWeatherFromDatabase(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mNewsesObservableWithNetwork = new GetNewsesHelper(getActivity())
                .getNewsesFromApi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mNewsesObservableWithoutNetwork = new GetNewsesHelper(getActivity())
                .getNewsesFromDatabase(mDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void updateDataFromActivity(Currently currently, Daily dailyData, String cityName) {
        mCurrently = currently;
        mDailyData = dailyData;
        mCityName = cityName;
        mHandler.post(() -> updateDisplay(mView));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDatabase = new DatabaseOperation(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        setupObservables();

        mCurrently = args.getParcelable(ConstantHolder.CURRENT_WEATHER_KEY);
        mDailyData = args.getParcelable(ConstantHolder.DAILY_WEATHER_KEY);
        mNewses = args.getParcelableArrayList(ConstantHolder.NEWS_DATA_KEY);
        mCityName = args.getString(ConstantHolder.CITY_NAME_KEY);
        subscriptions.add(mWeatherObservableWithoutNetwork
                .subscribeWith(new CurrentWeatherObserver()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.current_weather_layout, container, false);
        ButterKnife.bind(this, mView);
        AndroidThreeTen.init(getActivity());

        mDaySwitcherHelper = new DaySwitcherHelper(this,
                mView,
                mCurrently,
                mDailyData.getData(),
                mCityName);
        
        mHandler.post(() -> initialize(mView));
        mView.setBackgroundResource(mColorPicker.getBackgroundColor(mCurrently.getIcon()));

        if (AppUtil.isGpsPermissionOn(getActivity())) {
            getActivity().startService(new Intent(getActivity(), LocationTracker.class));
        }
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

        mHandler.post(() -> scrollToFunc(mHandler,
                mScrollView,
                currentDayButton));

        mHandler.post(() -> {
            mDaySwitcherHelper.setCurrentWeather(mTimeZone);
            showFallingSnowOrRain();
        });

        mHandler.post(() -> setupDayScrollView(view));
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
                mDaySwitcherHelper.setCurrentWeather(mTimeZone);
                showFallingSnowOrRain();
            });
        }

        mHandler.post(() -> setupDayScrollView(view));
        mHandler.post(() -> setupNewsScrollView(view));

    }

    private void showFallingSnowOrRain() {
        mHandler.post(() -> {
            if("snow".equalsIgnoreCase(mCurrently.getIcon())) {
                if(mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                    mMainLayout.addView(new SnowFallView(getActivity()), mParams);
                }
                if(mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                    mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
                }

            } else if("rain".equalsIgnoreCase(mCurrently.getIcon())) {
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
                if (mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                    mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
                }
                if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                    mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
                }
            }
        });
    }

    private void updateData() {
        if (AppUtil.isNetworkAvailable(getActivity())) {
            subscriptions.add(mWeatherObservableWithNetwork
                    .subscribeWith(new CurrentWeatherObserver()));

            isSubscriptionDoneWithNetwork = true;

        } else {
            mWeatherObservableWithoutNetwork
                    .subscribeWith(new CurrentWeatherObserver());

        }
    }

    /**
     * Setup the news scroll view and fetch it with data if available
     */
    private void setupNewsScrollView(View view) {
        final HorizontalScrollView newsHorizontallSV = (HorizontalScrollView) view.findViewById(R.id.newsHorizontallSV);
        mNewsRecyclerView = (RecyclerView) newsHorizontallSV.findViewById(R.id.newsRecyclerView);

            if(mNewses != null && !mNewses.isEmpty()) {
                mNewsAdapter = new NewsAdapter(mNewses);
                mNewsRecyclerView.setAdapter(mNewsAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mNewsRecyclerView.setLayoutManager(layoutManager);
                mNewsRecyclerView.setHasFixedSize(true);
                mHandler.postDelayed(() -> AnimationUtility.autoScrollRecyclerView(mNewsRecyclerView), 1000);
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


    private void setupDayScrollView(View view) {
        mScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScroll);

        final Button dayButton1 = (Button) view.findViewById(R.id.dayButton1);
        final Button dayButton2 = (Button) view.findViewById(R.id.dayButton2);
        final Button dayButton3 = (Button) view.findViewById(R.id.dayButton3);
        final Button dayButton4 = (Button) view.findViewById(R.id.dayButton4);
        final Button dayButton5 = (Button) view.findViewById(R.id.dayButton5);
        final Button dayButton6 = (Button) view.findViewById(R.id.dayButton6);
        final Button dayButton7 = (Button) view.findViewById(R.id.dayButton7);


        Calendar calendar = Calendar.getInstance();
        currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault());

        final View.OnClickListener buttonListener = theView -> {
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
            theView.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = (Button) theView;

            mHandler.post(AnimationUtility.dayButtonAnimation(theView)::start);

            mDaySwitcherHelper.showWeatherByDay(((Button) theView)
                            .getText().toString(), nextDayName, mTimeZone);
        };

        dayButton1.setOnClickListener(buttonListener);
        dayButton2.setOnClickListener(buttonListener);
        dayButton3.setOnClickListener(buttonListener);
        dayButton4.setOnClickListener(buttonListener);
        dayButton5.setOnClickListener(buttonListener);
        dayButton6.setOnClickListener(buttonListener);
        dayButton7.setOnClickListener(buttonListener);

        if(mDailyData.getData().size() > 5 &&
                mDailyData.getData().get(7) != null) {

            int count = 1;
            boolean isTodaySet = false;
            boolean isTomorrowSet = false;

            Integer currentDayIndex = null;
            SparseArray<Button> listOfButton = new SparseArray<>();

            listOfButton.put(1, dayButton1);
            listOfButton.put(2, dayButton2);
            listOfButton.put(3, dayButton3);
            listOfButton.put(4, dayButton4);
            listOfButton.put(5, dayButton5);
            listOfButton.put(6, dayButton6);
            listOfButton.put(7, dayButton7);

            while (count < 8) {
                for (DailyData day : mDailyData.getData()) {
                    if (count == 8) { break; }

                    if (!isTodaySet &&
                            currentDayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(),
                            mTimeZone))) {

                        scrollToFunc(mHandler,
                                mScrollView,
                                listOfButton.get(count));

                        count = 1;

                        currentDayButton = listOfButton.get(count);
                        listOfButton.get(count).setText(getString(R.string.today_label));
                        listOfButton.remove(count);
                        currentDayIndex = count++;
                        isTodaySet = true;
                        Log.i("DAY_CHECKER", "Got currentDay at count : " + currentDayIndex);

                    } else if (currentDayIndex != null
                            && count == (currentDayIndex + 1)) {

                        Log.i("DAY_CHECKER", "Got nextDay at count : " + count);
                        listOfButton.get(count).setText(getString(R.string.tomorrow_label));
                        listOfButton.remove(count++);
                        isTomorrowSet = true;
                        nextDayName = WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone);

                    } else if (isTodaySet && isTomorrowSet) {
                        Log.i("DAY_CHECKER", "Got Day at count : " + count);
                        listOfButton.get(count++)
                                    .setText(WeatherUtil.getDayOfTheWeek(day.getTime(), mTimeZone));

                    }
                }
            }

            Log.i("DAY_CHECKER", "While loop Done");

        } else {
            HashMap<String, Integer> dayOfTheWeek = (HashMap<String, Integer>) calendar.getDisplayNames(Calendar.DAY_OF_WEEK,
                    Calendar.LONG,
                    Locale.getDefault());

            for (HashMap.Entry<String, Integer> entry : dayOfTheWeek.entrySet()){
                String dayName = entry.getKey();
                int dayId = entry.getValue();

                if(dayId == Calendar.MONDAY) {
                    dayButton1.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton1);
                        dayButton1.setText(getResources().getString(R.string.today_label));
                        currentDayButton = dayButton1;
                    }
                }
                if(dayId == Calendar.TUESDAY) {
                    dayButton2.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton2);
                        dayButton2.setText(getResources().getString(R.string.today_label));
                        currentDayButton = dayButton2;
                    }
                }
                if(dayId == Calendar.WEDNESDAY) {
                    dayButton3.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton3);
                        dayButton3.setText(getResources().getString(R.string.today_label));
                        currentDayButton = dayButton3;
                    }
                }
                if(dayId == Calendar.THURSDAY) {
                    dayButton4.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton4);
                        dayButton4.setText(getResources().getString(R.string.today_label));
                        currentDayButton = dayButton4;
                    }
                }
                if(dayId == Calendar.FRIDAY) {
                    dayButton5.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton5);
                        dayButton5.setText(getString(R.string.today_label));
                        currentDayButton = dayButton5;
                    }
                }
                if(dayId == Calendar.SATURDAY) {
                    dayButton6.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton6);
                        dayButton6.setText(getResources().getString(R.string.today_label));
                        currentDayButton = dayButton6;
                    }
                }
                if(dayId == Calendar.SUNDAY) {
                    dayButton7.setText(dayName);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, dayButton7);
                        dayButton7.setText(getResources().getString(R.string.today_label));
                        currentDayButton = dayButton7;
                    }
                }
            }
        }
    }
}
