package com.dbeginc.dbweather.ui.main;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ActivityWeatherBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.Alert;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.repository.DatabaseUserCitiesRepository;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.addlocation.AddLocationActivity;
import com.dbeginc.dbweather.ui.animation.CubeOutTransformer;
import com.dbeginc.dbweather.ui.config.NewsConfigurationActivity;
import com.dbeginc.dbweather.ui.main.adapters.CustomFragmentAdapter;
import com.dbeginc.dbweather.ui.main.adapters.HourAdapter;
import com.dbeginc.dbweather.ui.main.adapters.NewsAdapter;
import com.dbeginc.dbweather.ui.timeline.NewsTimeLineActivity;
import com.dbeginc.dbweather.utils.helper.ColorManager;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;
import com.dbeginc.dbweather.utils.services.LocationTracker;
import com.dbeginc.dbweather.utils.utility.AppUtil;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CACHE_NAME;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_CHECKED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.RECEIVED_NEWS_FEED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.RECEIVED_WEATHER;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.UPDATE_REQUEST;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_ALERT_ID;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 11/02/17.
 * WeatherActivity of the application
 * Handle location update and set viewPager
 */

public class WeatherActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener,
        IWeatherActivityView {

    private static final int RECYCLER_NEWS_SPEED_SCROLL = 4000;
    private CustomFragmentAdapter mFragmentAdapter;
    private BroadcastReceiver mLocationBroadcast;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsAdapter mNewsAdapter;
    private HourAdapter mHourAdapter;
    private SubMenu locationSubMenu;
    private RxWeatherActivityPresenter mMainPresenter;
    private ActivityWeatherBinding mWeatherActivityBinder;
    @Inject
    SharedPreferences sharedPreferences;

    private final WeatherData mWeatherData = new WeatherData();
    private final List<Article> mNewses = new ArrayList<>();
    private final Handler mUpdateHandler = new Handler();
    private final Handler mMyHandler = new Handler();
    private final SparseArray<GeoName> sparseArrayOfIdAndLocation = new SparseArray<>();
    private final ColorManager mColorPicker = ColorManager.newInstance();


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) { return respondToMenuItemClick(item); }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) { return respondToMenuItemClick(item); }

    @Override
    public boolean onMenuItemClick(final MenuItem item) { return respondToMenuItemClick(item); }

    @Override
    public void showWeather(final WeatherData weatherData) {
        Answers.getInstance().logCustom(new CustomEvent("WEATHER_DATA")
        .putCustomAttribute(RECEIVED_WEATHER, String.format("RECEIVED WEATHER DATA at %s",
                WeatherUtil.getHour(System.currentTimeMillis(), null))));

        setWeatherData(weatherData);
        mWeatherActivityBinder.dbweatherMainLayout
                .setBackgroundResource(mColorPicker
                        .getBackgroundColor(mWeatherData.getWeatherInfoList().get(0).icon.get()));

        if (mFragmentAdapter != null) { mUpdateHandler.post(() -> mFragmentAdapter.updateFragments(mWeatherData.getWeatherInfoList())); }
        else {
            mFragmentAdapter = new CustomFragmentAdapter(getSupportFragmentManager(), mWeatherData.getWeatherInfoList());
            mWeatherActivityBinder.viewPager.setAdapter(mFragmentAdapter);
        }

        if (mHourAdapter != null) { mHourAdapter.updateData(mWeatherData.getHourlyWeatherList()); }
        else { setupHourlyRecyclerView(); }

        if (weatherData.getAlertList() != null && !weatherData.getAlertList().isEmpty()) {
            for (final Alert alert : weatherData.getAlertList()) { showAlerts(alert); }
        }
    }

    @Override
    public void showNews(final List<Article> articles) {
        Answers.getInstance().logCustom(new CustomEvent(ConstantHolder.NEWS_FEED)
        .putCustomAttribute(RECEIVED_NEWS_FEED, String.format("RECEIVED NEWS FEED at %s",
                WeatherUtil.getHour(System.currentTimeMillis(), null))));

        mNewses.clear();
        mNewses.addAll(articles);

        if(mNewsAdapter == null) { setupNewsRecyclerView(); }

        else { mMyHandler.post(() -> mNewsAdapter.updateContent(articles)); }
    }

    @Override
    public void showNetworkWeatherErrorMessage() {
        final Snackbar snackbar = Snackbar
                .make(mWeatherActivityBinder.getRoot(), getString(R.string.weather_error_message), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, v -> mMainPresenter.retryWeatherRequest())
                .setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void showNetworkNewsErrorMessage() {
        final Snackbar snackbar = Snackbar
                .make(mWeatherActivityBinder.getRoot(), getString(R.string.news_error_message), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, v -> mMainPresenter.retryNewsRequest())
                .setActionTextColor(Color.RED);
        snackbar.show();
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
    public void requestUpdate() {
        if (!mMainPresenter.didUserSelectedCityFromDrawer()) {
            if (isNetworkAvailable()) { mMainPresenter.getWeather(); }
            else { showNetworkNotAvailableMessage(); }
        }
        else {
            final Pair<String, double[]> selectedUserCity = mMainPresenter.getSelectedUserCity();
            final double[] coordinates = selectedUserCity.second;

            if (isNetworkAvailable()) {
                mMainPresenter.getWeatherForCity(selectedUserCity.first, coordinates[0], coordinates[1]);
            }
            else {
                showNetworkNotAvailableMessage();
                mMainPresenter.loadWeatherForCity(selectedUserCity.first, coordinates[0], coordinates[1]);
            }
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void showScreenshotAttemptError() {
        Snackbar.make(mWeatherActivityBinder.getRoot(), R.string.screenshot_attemp_error, Snackbar.LENGTH_LONG);
    }

    @Override
    public void showNetworkNotAvailableMessage() {
        final Snackbar snackbar = Snackbar
                .make(mWeatherActivityBinder.getRoot(), R.string.network_unavailable_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void showAlerts(@Nonnull final Alert weatherAlert) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        final Intent browserIntent = new Intent()
                .setAction(Intent.ACTION_VIEW)
                .setData(Uri.parse(weatherAlert.getUri().isEmpty() ? "https://github.com/404" : weatherAlert.getUri()))
                .addCategory(Intent.CATEGORY_BROWSABLE);

        final int notificationId = WEATHER_ALERT_ID;

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(weatherAlert.getTitle());
        builder.setContentText(weatherAlert.getDescription());
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setCategory(NotificationCompat.CATEGORY_ALARM);
        builder.setContentIntent(PendingIntent.getActivity(this, notificationId, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        builder.setContentInfo(String.format(Locale.getDefault(),
                getString(R.string.notification_alert_city)
                , mWeatherData.getWeatherInfoList().get(0).locationName.get()));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, builder.build());
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherActivityBinder = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        mMainPresenter = new RxWeatherActivityPresenter(new DatabaseUserCitiesRepository(this),
                        this,
                        mAppDataProvider);

        final Intent intent = getIntent();

        if (intent != null && intent.hasExtra(WEATHER_INFO_KEY)) {

            setWeatherData(intent.getParcelableExtra(WEATHER_INFO_KEY));

            mFragmentAdapter = new CustomFragmentAdapter(getSupportFragmentManager(),
                        mWeatherData.getWeatherInfoList());

            mWeatherActivityBinder.dbweatherMainLayout
                    .setBackgroundResource(mColorPicker
                            .getBackgroundColor(mWeatherData.getWeatherInfoList().get(0).icon.get()));

            mWeatherActivityBinder.viewPager.setAdapter(mFragmentAdapter);

            if (intent.hasExtra(NEWS_DATA_KEY)) {
                mNewses.clear();
                mNewses.addAll(intent.getParcelableArrayListExtra(NEWS_DATA_KEY));

            } else { mMainPresenter.loadNews(); }

        } else if (!mMainPresenter.didUserSelectedCityFromDrawer()) { mMainPresenter.configureView(); }

        setSupportActionBar(mWeatherActivityBinder.weatherToolbar.toolbarId);

        if (mAppDataProvider.getGpsPermissionStatus() ) { startService(new Intent(getApplicationContext(), LocationTracker.class)); }
        else { super.askLocationPermIfNeeded(); }

        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) && !mAppDataProvider.getAccountPermissionStatus()) {
            super.askAccountInfoPermIfNeeded();
        }

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

        mWeatherActivityBinder.weatherDrawerLayout.addDrawerListener(mDrawerToggle);
        mLocationBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context,
                                  final Intent intent) {
                receiveBroadcast(intent.getAction());
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

        if (mNewsAdapter == null && !mNewses.isEmpty()) { setupNewsRecyclerView(); }
        if (mHourAdapter == null && !mWeatherData.getHourlyWeatherList().isEmpty()) { setupHourlyRecyclerView(); }

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

        mMainPresenter.loadUserCitiesMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.unSubscribeToUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.setMainView(this);

        final IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE);
        intentFilter.addAction(UPDATE_REQUEST);
        registerReceiver(mLocationBroadcast,
                intentFilter);


        if (isNetworkAvailable() && !mMainPresenter.isFirstRun() && !mMainPresenter.didUserSelectedCityFromDrawer()) {
            mMainPresenter.getNews();
            mMainPresenter.getWeather();
        }
        if (mMainPresenter.isFirstRun()) { mMainPresenter.setFirstRun(false); }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mNewsAdapter != null) {
            mMainPresenter.getRxSubscriptions()
                    .add(moveRecyclerView(RECYCLER_NEWS_SPEED_SCROLL, 0, mNewsAdapter.getItemCount())
                            .observeOn(mMainPresenter.getSchedulersProvider().getUIScheduler())
                            .subscribeWith(new NewsRecyclerViewObserver()));
        }

        mMyHandler.post(() -> {
            if (!AppUtil.isAlarmSet(getApplicationContext())) {
                AppUtil.setNextAlarm(getApplicationContext());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationBroadcast != null) { unregisterReceiver(mLocationBroadcast); }
        mMainPresenter.userSelectedCityFromDrawer(false);
        mMainPresenter.clearState(new File(getCacheDir(), CACHE_NAME));
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final @NonNull String[] permissions,
                                           final @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checking if the user cancelled, the permission
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            startService(new Intent(getApplicationContext(), LocationTracker.class));

        } else if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            shareWeatherInfo();
        }

        super.askAccountInfoPermIfNeeded();
        if (isNetworkAvailable()) { mMainPresenter.getNews(); }
    }

    private void setWeatherData(final WeatherData weatherData) {
        mWeatherData.setWeatherInfoList(weatherData.getWeatherInfoList());
        mWeatherData.setHourlyWeatherList(weatherData.getHourlyWeatherList());
        mWeatherData.setAlertList(weatherData.getAlertList());
    }

    private void shareWeatherInfo() {
        if (mMainPresenter.getWritePermissionStatus()) {
            try { shareScreenShot(); }
            catch (final IOException e) {
                Crashlytics.logException(e);
                showScreenshotAttemptError();
            }

        } else { super.askWriteToExtPermIfNeeded(); }
    }

    /**
     * Class that handle the scrolling of the news recycler view
     */
    private class NewsRecyclerViewObserver extends DisposableObserver<Integer> {
        @Override
        public void onNext(final Integer position) { mWeatherActivityBinder.newsRecyclerView.smoothScrollToPosition(position); }
        @Override
        public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }

        @Override
        public void onComplete() {
            mMainPresenter.getRxSubscriptions()
                    .add(moveRecyclerView(RECYCLER_NEWS_SPEED_SCROLL, mNewsAdapter.getItemCount(), 0)
                            .observeOn(mMainPresenter.getSchedulersProvider().getUIScheduler())
                            .subscribeWith(new NewsRecyclerViewObserver()));
        }
    }

    /**
     * This method handle location update notification
     * @param action name of the broadcast request action
     */
    private void receiveBroadcast(@NonNull final String action) {
        if (action.equalsIgnoreCase(LOCATION_UPDATE) && !mMainPresenter.didUserSelectedCityFromDrawer()) {
            if (isNetworkAvailable()) { mMainPresenter.getWeather(); }
            else { showNetworkNotAvailableMessage(); }
        }
    }

    /**
     * Setup Hourly RecyclerView
     */
    private void setupHourlyRecyclerView() {
        mHourAdapter = new HourAdapter(mWeatherData.getHourlyWeatherList(), mMainPresenter.getRxSubscriptions());
        mWeatherActivityBinder.hourlyRecyclerView.setHasFixedSize(true);
        mWeatherActivityBinder.hourlyRecyclerView.setAdapter(mHourAdapter);
        mWeatherActivityBinder.hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));
    }

    /**
     * Setup Navigation Drawer items
     */
    public void setupNavigationDrawer() {
        final Menu menu = mWeatherActivityBinder.navigationView.getMenu();
        final CompoundButton.OnCheckedChangeListener notificationConfigurationListener = (buttonView, isChecked) -> {
            if (isChecked) {
                mMainPresenter.setWeatherNotificationStatus(true);
                Answers.getInstance().logCustom(new CustomEvent("WEATHER_NOTIFICATION_CHECKBOX")
                .putCustomAttribute(IS_CHECKED, String.valueOf(true)));
            } else {
                mMainPresenter.setWeatherNotificationStatus(false);
                Answers.getInstance().logCustom(new CustomEvent("WEATHER_NOTIFICATION_CHECKBOX")
                .putCustomAttribute(IS_CHECKED, String.valueOf(false)));
            }
        };

        final CompoundButton.OnCheckedChangeListener newsConfigurationListener = (buttonView, isChecked) -> {
            if (isChecked) {
                mMainPresenter.setNewsTranslationStatus(true);
                Answers.getInstance().logCustom(new CustomEvent("NEWS_NOTIFICATION_CHECKBOX")
                        .putCustomAttribute(IS_CHECKED, String.valueOf(true)));
            } else {
                mMainPresenter.setNewsTranslationStatus(false);
                Answers.getInstance().logCustom(new CustomEvent("NEWS_NOTIFICATION_CHECKBOX")
                        .putCustomAttribute(IS_CHECKED, String.valueOf(false)));
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

    /**
     * Setup the News recycler view
     */
    private void setupNewsRecyclerView() {
        mNewsAdapter = new NewsAdapter(mNewses, mMainPresenter.getRxSubscriptions());
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
    }

    /**
     * This method move the recycler view
     * @param speedScroll the scrolling speed
     * @param startAt the starting point
     * @param endAt the ending point
     * @return RxObservable of Integer
     */
    private Observable<Integer> moveRecyclerView(final int speedScroll, final int startAt, final int endAt) {
        return Observable.intervalRange(startAt, endAt, 1, speedScroll, TimeUnit.MILLISECONDS, mMainPresenter.getSchedulersProvider().getComputationThread())
                .map(Long::intValue);
    }

    /**
     * This method handle item selected from navigation drawer
     * @param item that was selected
     * @return True if handle, False if not
     */
    private boolean respondToMenuItemClick(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.add_location_id) {
            startActivity(new Intent(getApplicationContext(), AddLocationActivity.class));

            Answers.getInstance().logCustom(new CustomEvent("ADD_LOCATION")
            .putCustomAttribute("OPENED", "true"));

            finish();

        } else if (id == R.id.select_news_source_config_id) {
            startActivity(new Intent(getApplicationContext(), NewsConfigurationActivity.class));

            Answers.getInstance().logCustom(new CustomEvent("NEWS_CONFIG")
                    .putCustomAttribute("OPENED", "true"));

            finish();

        } else if (id == R.id.news_timeline_menu) {
            final Intent intent = new Intent(getApplicationContext(), NewsTimeLineActivity.class);
            intent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) mNewses);
            startActivity(intent);
            finish();
        }

        else if (id == R.id.current_location) {
            if (isNetworkAvailable()) { mMainPresenter.getWeather(); }
            else {
                showNetworkNotAvailableMessage();
                mMainPresenter.loadWeather();
            }
            mMainPresenter.userSelectedCityFromDrawer(false);
            mWeatherActivityBinder.weatherDrawerLayout.closeDrawers();

        } else if (sparseArrayOfIdAndLocation.indexOfKey(id) >= 0) {
            final GeoName location = sparseArrayOfIdAndLocation.get(id);
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            final DialogInterface.OnClickListener displayListener = (dialog, which) -> {
                final String cityName = String.format(Locale.getDefault(),
                        "%s, %s", location.getName(), location.getCountryName());

                if (isNetworkAvailable()) { mMainPresenter.getWeatherForCity(cityName, latitude, longitude); }
                else { mMainPresenter.loadWeatherForCity(cityName, latitude, longitude); }

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
                        mMainPresenter.loadWeather();
                        if (isNetworkAvailable()) { mMainPresenter.getWeather(); }
                        mMainPresenter.userSelectedCityFromDrawer(false);
                    })
                    .setPositiveButton(R.string.display, displayListener)
                    .create()
                    .show();
        }
        return true;
    }
}