package com.darelbitsy.dbweather.views.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.ActivityWeatherBinding;
import com.darelbitsy.dbweather.extensions.helper.ColorManager;
import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.extensions.services.LocationTracker;
import com.darelbitsy.dbweather.extensions.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.presenters.activities.RxWeatherActivityPresenter;
import com.darelbitsy.dbweather.provider.repository.DatabaseUserCitiesRepository;
import com.darelbitsy.dbweather.views.adapters.CustomFragmentAdapter;
import com.darelbitsy.dbweather.views.adapters.listAdapter.HourAdapter;
import com.darelbitsy.dbweather.views.adapters.listAdapter.NewsAdapter;
import com.darelbitsy.dbweather.views.animation.CubeOutTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.LOCATION_UPDATE;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_GET_ACCOUNT;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.UPDATE_REQUEST;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 11/02/17.
 * WeatherActivity of the application
 * Handle location update and set viewPager
 */

public class WeatherActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener,
        IWeatherActivityView {

    private CustomFragmentAdapter mFragmentAdapter;
    private BroadcastReceiver mLocationBroadcast;
    private final Handler mUpdateHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;

    private NewsAdapter mNewsAdapter;
    private HourAdapter mHourAdapter;

    private final List<WeatherInfo> mWeatherInfoList = new ArrayList<>();
    private final List<Article> mNewses = new ArrayList<>();
    private final Handler mMyHandler = new Handler();
    private final SparseArray<GeoName> sparseArrayOfIdAndLocation = new SparseArray<>();

    private final ColorManager mColorPicker = ColorManager.newInstance();
    private SubMenu locationSubMenu;
    private RxWeatherActivityPresenter mMainPresenter;
    private ActivityWeatherBinding mWeatherActivityBinder;

    @Inject SharedPreferences sharedPreferences;


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return respondToMenuItemClick(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        return respondToMenuItemClick(item);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        return respondToMenuItemClick(item);
    }

    @Override
    public void showWeather(final Pair<List<WeatherInfo>, List<HourlyData>> weatherInfo) {
        mWeatherInfoList.clear();
        mWeatherInfoList.addAll(weatherInfo.first);
        mWeatherActivityBinder.dbweatherMainLayout
                .setBackgroundResource(mColorPicker
                        .getBackgroundColor(weatherInfo.first.get(0).icon.get()));

        if (mFragmentAdapter != null) {
            mUpdateHandler.post(() -> mFragmentAdapter.updateFragments(weatherInfo.first));
        }
        if (mHourAdapter != null) {

            mHourAdapter.updateData(weatherInfo.second);
        } else {

            mHourAdapter = new HourAdapter(weatherInfo.second);
            setupHourlyRecyclerView();
        }
        Log.i(ConstantHolder.TAG, "City Name: " + weatherInfo.first.get(0).locationName.get());
    }

    @Override
    public void showNews(final List<Article> articles) {
        mNewses.clear();
        mNewses.addAll(articles);

        if(mNewsAdapter == null) {
            setupNewsScrollView();
        } else {
            mMyHandler.post(() -> mNewsAdapter.updateContent(articles));
        }
    }

    @Override
    public void showNetworkWeatherErrorMessage() {

    }

    @Override
    public void showNetworkNewsErrorMessage() {

    }

    @Override
    public void setupNavigationDrawerWithCities(final List<GeoName> listOfLocation) {
        mWeatherActivityBinder.navigationView.setNavigationItemSelectedListener(this);

        final Menu navigationViewMenu = mWeatherActivityBinder.navigationView.getMenu();
        locationSubMenu = navigationViewMenu.findItem(R.id.location_config_id)
                .setOnMenuItemClickListener(this)
                .getSubMenu();

        final int location_size = listOfLocation.size();

        for (int index = 0; index < location_size; index++) {
            final GeoName location = listOfLocation.get(index);
            final int id = View.generateViewId();
            sparseArrayOfIdAndLocation.append(id, location);

            final MenuItem item = locationSubMenu.add(R.id.cities_menu_id, id, Menu.FLAG_APPEND_TO_GROUP,
                    String.format(Locale.getDefault(), "%s, %s", location.getName(), location.getCountryName()));

            item.setIcon(R.drawable.city_location_icon);
            item.setEnabled(true);

            setupNavigationDrawer();
        }
    }

    @Override
    public void setupNavigationDrawerWithNoCities() {
        mWeatherActivityBinder.navigationView.setNavigationItemSelectedListener(this);
        setupNavigationDrawer();
    }

    @Override
    public void saveState(final Bundle bundle) {
        mMainPresenter.saveState(bundle);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void showScreenshotAttempError() {

    }

    @Override
    public void launchActivity(@NonNull final Intent intent) {
        startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWeatherActivityBinder = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        mMainPresenter =
                new RxWeatherActivityPresenter(new DatabaseUserCitiesRepository(this),
                        this,
                        mAppDataProvider);

        mMainPresenter.configureView();

        if (getIntent() != null) {

            mWeatherInfoList.clear();
            mWeatherInfoList.addAll(getIntent().getParcelableArrayListExtra(WEATHER_INFO_KEY));
            mNewses.clear();
            mNewses.addAll(getIntent().getParcelableArrayListExtra(NEWS_DATA_KEY));

        } else if (savedInstanceState != null && savedInstanceState.containsKey(WEATHER_INFO_KEY)) {

            mWeatherInfoList.clear();
            mWeatherInfoList.addAll(savedInstanceState.getParcelableArrayList(WEATHER_INFO_KEY));
            mWeatherInfoList.clear();
            mWeatherInfoList.addAll(savedInstanceState.getParcelableArrayList(NEWS_DATA_KEY));
        }

        setSupportActionBar(mWeatherActivityBinder.weatherToolbar.toolbarId);

        if (mAppDataProvider.getGpsPermissionStatus()) {
            startService(new Intent(getApplicationContext(), LocationTracker.class));
        } else { super.askLocationPermIfNeeded(); }

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) && !mAppDataProvider.getAccountPermissionStatus()) {
            super.askAccountInfoPermIfNeeded();
        }

        mFragmentAdapter = new CustomFragmentAdapter(getSupportFragmentManager(),
                mWeatherInfoList);
        mWeatherActivityBinder.viewPager.setAdapter(mFragmentAdapter);
        mWeatherActivityBinder.viewPager.setPageTransformer(false, new CubeOutTransformer());

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mWeatherActivityBinder.weatherDrawerLayout,
                mWeatherActivityBinder.weatherToolbar.toolbarId,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(final View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mWeatherActivityBinder.weatherDrawerLayout
                .addDrawerListener(mDrawerToggle);
        mLocationBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context,
                                  final Intent intent) {
                receiveBroadcast(intent.getAction(), intent);
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (super.isNetworkAvailable()) {
            if (mMainPresenter.isFirstRun()) {
                mMainPresenter.setFirstRun(false);
            } else {
                mMainPresenter.getNews();
                mMainPresenter.getWeather();
            }
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mMyHandler.post(mDrawerToggle::syncState);

        mWeatherActivityBinder.weatherToolbar.shareIcon.setOnClickListener(view -> {
            final AnimatorSet animatorSet = new AnimatorSet().setDuration(225);
            final ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 0.0f);
            final ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 0.0f);

            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setRepeatCount(1);
            scaleY.setRepeatCount(1);

            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.start();
            shareWeatherInfo();
        });
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WEATHER_INFO_KEY, (ArrayList<? extends Parcelable>) mWeatherInfoList);
        outState.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) mNewses);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(WEATHER_INFO_KEY)) {

            mWeatherInfoList.clear();
            mWeatherInfoList.addAll(savedInstanceState.getParcelableArrayList(WEATHER_INFO_KEY));
            mWeatherInfoList.clear();
            mWeatherInfoList.addAll(savedInstanceState.getParcelableArrayList(NEWS_DATA_KEY));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.userSelectedCityFromDrawer(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE);
        intentFilter.addAction(UPDATE_REQUEST);
        registerReceiver(mLocationBroadcast,
                intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationBroadcast != null) {
            unregisterReceiver(mLocationBroadcast);
        }
        mMainPresenter.userSelectedCityFromDrawer(false);
        mMainPresenter.clearState(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String[] permissions,
                                           final @NonNull int[] grantResults) {

        // Checking if the user cancelled, the permission
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            mMainPresenter.setGpsPermissionStatus(true);
            startService(new Intent(getApplicationContext(), LocationTracker.class));
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_GET_ACCOUNT
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            mMainPresenter.setAccountPermissionStatus(true);
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            mMainPresenter.setWritePermissionStatus(true);
            shareWeatherInfo();
        }
        super.askAccountInfoPermIfNeeded();
        mMainPresenter.getNews();
    }

    private void shareWeatherInfo() {
        if (mMainPresenter.getWritePermissionStatus()) {
            try {
                mMainPresenter.shareScreenShot(this);
            } catch (final IOException e) {
                showScreenshotAttempError();
            }
        } else {
            super.askWriteToExtPermIfNeeded();
        }
    }

    private void receiveBroadcast(@NonNull final String action, @NonNull final Intent intent) {
        if (action.equalsIgnoreCase(LOCATION_UPDATE)) {
            WeatherUtil.saveCoordinates(intent.getExtras().getDouble("latitude"),
                    intent.getExtras().getDouble("longitude"),
                    DatabaseOperation.newInstance(getApplicationContext()));

            if (!mMainPresenter.didUserSelectedCityFromDrawer()) {
                mMainPresenter.getWeather();
            }

        } else if (action.equalsIgnoreCase(UPDATE_REQUEST)) {
            if (!mMainPresenter.didUserSelectedCityFromDrawer()) {
                mMainPresenter.getWeather();
            } else {
                final Pair<String, double[]> selectedUserCity = mMainPresenter.getSelectedUserCity(mWeatherInfoList.get(0).locationName.get());
                final double[] coordinates = selectedUserCity.second;

                if (super.isNetworkAvailable()) {
                    mMainPresenter.getWeatherForCity(selectedUserCity.first, coordinates[0], coordinates[1]);
                } else {
                    mMainPresenter.loadWeatherForCity(selectedUserCity.first, coordinates[0], coordinates[1]);
                }
            }
        }
    }

    private void setupHourlyRecyclerView() {
        mWeatherActivityBinder.hourlyRecyclerView.setAdapter(mHourAdapter);
        mWeatherActivityBinder.hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getRealMetrics(metrics);

        final float height = metrics.heightPixels;

        mMainPresenter.saveRecyclerBottomLimit(height);
    }

    public void setupNavigationDrawer() {
        final Menu menu = mWeatherActivityBinder.navigationView.getMenu();
        final CompoundButton.OnCheckedChangeListener notificationConfigurationListener = (buttonView, isChecked) -> {
            if (isChecked) {
                mMainPresenter.setWeatherNotificationStatus(true);
            } else {
                mMainPresenter.setWeatherNotificationStatus(false);
            }
        };

        final CompoundButton.OnCheckedChangeListener newsConfigurationListener = (buttonView, isChecked) -> {
            if (isChecked) {
                mMainPresenter.setNewsTranslationStatus(true);
            } else {
                mMainPresenter.setNewsTranslationStatus(false);
            }
        };

        final Menu newsSubMenu = menu.findItem(R.id.news_config_id)
                .setOnMenuItemClickListener(this)
                .setEnabled(true)
                .getSubMenu();

        final Menu weatherSubMenu = menu.findItem(R.id.weather_config_id)
                .setOnMenuItemClickListener(this)
                .setEnabled(true)
                .getSubMenu();

        final SwitchCompat notification_switch = (SwitchCompat)
                MenuItemCompat.getActionView(weatherSubMenu.findItem(R.id.notification_config_id));

        final SwitchCompat news_translation_switch = (SwitchCompat)
                MenuItemCompat.getActionView(newsSubMenu.findItem(R.id.news_translation_config_id));

        notification_switch.setOnCheckedChangeListener(notificationConfigurationListener);
        news_translation_switch.setOnCheckedChangeListener(newsConfigurationListener);

        notification_switch.setChecked(mMainPresenter.getWeatherNotificationStatus());
        news_translation_switch.setChecked(mMainPresenter.getNewsTranslationStatus());
    }

    // Setup the news scroll view and fetch it with data if available
    private void setupNewsScrollView() {
        mNewsAdapter = new NewsAdapter(mNewses);
        mWeatherActivityBinder.newsRecyclerView.setAdapter(mNewsAdapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false) {
            @Override
            public void smoothScrollToPosition(final RecyclerView recyclerView,
                                               final RecyclerView.State state,
                                               final int position) {

                final LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    private static final float SPEED = 4500f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(final DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
        mWeatherActivityBinder.newsRecyclerView.setLayoutManager(layoutManager);
        mWeatherActivityBinder.newsRecyclerView.setHasFixedSize(true);

        final int speedScroll = 4000;
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                if(count < mNewsAdapter.getItemCount()){
                    if(count == mNewsAdapter.getItemCount() -1){
                        flag = false;

                    } else if(count == 0){ flag = true; }

                    if(flag) { count++; }
                    else { count--; }

                    mWeatherActivityBinder.newsRecyclerView.smoothScrollToPosition(count);
                    if (mWeatherActivityBinder.newsRecyclerView.getVisibility() == View.VISIBLE) {
                        mMyHandler.postDelayed(this, speedScroll);
                    }
                }
            }
        };
        mMyHandler.postDelayed(runnable, speedScroll);
    }

    private boolean respondToMenuItemClick(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.add_location_id) {
            startActivity(new Intent(getApplicationContext(), AddLocationActivity.class));
            finish();

        } else if (id == R.id.select_news_source_config_id) {
            startActivity(new Intent(getApplicationContext(), NewsConfigurationActivity.class));
            finish();

        } else if (id == R.id.current_location) {
            mMainPresenter.getWeather();
            mMainPresenter.userSelectedCityFromDrawer(false);
            mWeatherActivityBinder.weatherDrawerLayout.closeDrawers();

        } else if (sparseArrayOfIdAndLocation.indexOfKey(id) >= 0) {
            final GeoName location = sparseArrayOfIdAndLocation.get(id);
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            final DialogInterface.OnClickListener displayListener = (dialog, which) -> {
                final String cityName = String.format(Locale.getDefault(),
                        "%s, %s", location.getName(), location.getCountryName());

                mMainPresenter.getWeatherForCity(cityName,
                        latitude,
                        longitude);
                mMainPresenter.userSelectedCityFromDrawer(true);
                mMainPresenter.setSelectedUserCity(cityName, latitude, longitude);
            };
            mWeatherActivityBinder.weatherDrawerLayout.closeDrawers();

            new AlertDialog.Builder(this)
                    .setMessage(String.format(Locale.getDefault(),
                            WeatherActivity.this.getApplicationContext().getString(R.string.removeOrDisplay),
                            location.getName()))
                    .setNegativeButton(R.string.remove, (dialog, which) -> {

                        locationSubMenu.removeItem(id);
                        mMainPresenter.removeCityFromUserCities(location);
                        mMainPresenter.getWeather();
                        mMainPresenter.userSelectedCityFromDrawer(false);

                    })
                    .setPositiveButton(R.string.display, displayListener)
                    .create()
                    .show();
        }
        return true;
    }
}