package com.dbeginc.dbweather.ui.main;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;
import android.view.WindowManager;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ActivityDbweatherBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.main.config.ConfigFragment;
import com.dbeginc.dbweather.ui.main.news.NewsTabFragment;
import com.dbeginc.dbweather.ui.main.weather.WeatherTabFragment;
import com.dbeginc.dbweather.utils.services.KillCheckerService;
import com.dbeginc.dbweather.utils.services.LocationTracker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CACHE_NAME;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.UPDATE_REQUEST;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

public class DBWeatherActivity extends BaseActivity implements DBWeatherRootView, OnTabSelectListener {

    private BroadcastReceiver locationBroadcastReceiver;
    private DBWeatherPresenter presenter;
    private WeatherTabFragment weatherFragment;
    private NewsTabFragment newsTabFragment;
    private WeatherData weatherData;
    private ArrayList<Article> listOfNews;
    private final Fragment configFragment = new ConfigFragment();

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    private ActivityDbweatherBinding  binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dbweather);

        presenter = new DBWeatherPresenter(this, weatherDataUpdateEvent, newsUpdateEvent, mAppDataProvider);
        final Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(WEATHER_INFO_KEY)) {
                weatherData = intent.getParcelableExtra(WEATHER_INFO_KEY);
                weatherFragment = WeatherTabFragment.newInstance(weatherData);
            }

            if (intent.hasExtra(NEWS_DATA_KEY)) {
                listOfNews = intent.getParcelableArrayListExtra(NEWS_DATA_KEY);
                newsTabFragment = NewsTabFragment.newInstance(listOfNews);
            }
            startService(new Intent(getApplicationContext(), KillCheckerService.class));
        }

        binding.bottomBar.setOnTabSelectListener(this);
        binding.bottomBar.selectTabWithId(R.id.tab_weather);
        binding.bottomBar.setDefaultTab(R.id.tab_weather);

        locationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                locationUpdateEvent.onNext(intent.getAction());
            }
        };

        if (presenter.getGpsPermissionStatus()) { startService(new Intent(getApplicationContext(), LocationTracker.class)); }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            voiceQuery.onNext(query);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter.isFirstRun()) {
            presenter.setFirstRun(false);
        }

        if (isNetworkAvailable()) { presenter.getNews(); }
        else { showNetworkNotAvailable(); }

        if (((DBWeatherApplication) getApplication()).isFirebaseAvailable()) {
            FirebaseDatabase.getInstance(((DBWeatherApplication) getApplication()).getFirebaseApp()).getReference(LIVE_SOURCE).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, final String s) {
                    presenter.addNewLiveSource(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    presenter.addNewLiveSource(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    presenter.removeLiveSource(dataSnapshot);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    presenter.addNewLiveSource(dataSnapshot);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }

        final IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE);
        intentFilter.addAction(UPDATE_REQUEST);
        registerReceiver(locationBroadcastReceiver, intentFilter);

        presenter.subscribeToEvents(weatherDataUpdateEvent, newsUpdateEvent);
    }

    @Override
    protected void onPause() {
        presenter.clearState(new File(getCacheDir(), CACHE_NAME));
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (locationBroadcastReceiver != null) { unregisterReceiver(locationBroadcastReceiver); }
        super.onStop();
    }

    @Override
    public void onTabSelected(final int id) {
        int color = getResources().getColor(R.color.colorPrimaryDark);

        if (id == R.id.tab_weather) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left,
                            R.anim.slide_out_left)
                    .replace(R.id.tabContent, weatherFragment)
                    .commit();

        } else if (id == R.id.tab_news) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left,
                            R.anim.slide_out_left)
                    .replace(R.id.tabContent, newsTabFragment)
                    .commit();
            color = getResources().getColor(R.color.newsStatusBarColor);

        } else if (id == R.id.tab_config) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left,
                            R.anim.slide_out_left)
                    .replace(R.id.tabContent, configFragment)
                    .commit();
            color = getResources().getColor(R.color.configStatusBarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void updateWeather(@NonNull final WeatherData weatherData) {
        this.weatherData = weatherData;
        weatherFragment.getArguments().putParcelable(WEATHER_INFO_KEY, this.weatherData);
    }

    @Override
    public void updateNews(@NonNull final List<Article> news) {
        listOfNews.clear();
        listOfNews.addAll(news);
        newsTabFragment.getArguments().putParcelableArrayList(NEWS_DATA_KEY, listOfNews);
    }

    @Override
    public void showNetworkNotAvailable() {
        Snackbar.make(binding.clTabsContainer, getString(R.string.network_unavailable_message), Snackbar.LENGTH_LONG)
                .show();
    }
}
