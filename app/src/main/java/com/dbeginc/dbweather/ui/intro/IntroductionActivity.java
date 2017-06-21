package com.dbeginc.dbweather.ui.intro;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.IntroActivityLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.intro.location.LocationIntroPage;
import com.dbeginc.dbweather.ui.intro.news.NewsIntroPage;
import com.dbeginc.dbweather.ui.intro.searchlocation.SearchLocationPage;
import com.dbeginc.dbweather.ui.intro.waiting.WaitingPage;
import com.dbeginc.dbweather.ui.main.DBWeatherActivity;
import com.dbeginc.dbweather.utils.services.LocationTracker;

import java.util.ArrayList;
import java.util.List;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.UPDATE_REQUEST;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by darel on 14.06.17.
 * Intro Activity
 */

public class IntroductionActivity extends BaseActivity implements IIntroView {

    private static final int LOCATION_PERMISSION_PAGE = 1;
    private static final int ACCOUNT_PERMISSION_PAGE = 2;
    private static final String INTRO_DATA = "INTRO_DATA";
    private IntroPresenter presenter;
    private BroadcastReceiver mLocationBroadcast;
    private Intent homeIntent;
    private int pageIndex = 1;
    private IntroActivityLayoutBinding binding;

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            voiceQuery.onNext(query);
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.intro_activity_layout);
        presenter = new IntroPresenter(this, mAppDataProvider, permissionEvent, locationUpdateEvent);
        pageIndex = 1;
        mLocationBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) { receiveBroadcast(intent.getAction()); }
        };
        presenter.getNews();
        presenter.initiateLiveSourcesTable();

        if (savedInstanceState != null) {
            homeIntent = savedInstanceState.containsKey(INTRO_DATA) ?
                    savedInstanceState.getParcelable(INTRO_DATA) : new Intent();

            if (!homeIntent.hasExtra(NEWS_DATA_KEY)) { presenter.getNews(); }
            if (!homeIntent.hasExtra(WEATHER_INFO_KEY)) { presenter.getWeather(); }

        } else {
            homeIntent = new Intent();
            presenter.getNews();
        }

        if (pageIndex == 1) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.push_up_in,
                            R.anim.push_up_out)
                    .replace(R.id.introFragmentContainer, new LocationIntroPage())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE);
        intentFilter.addAction(UPDATE_REQUEST);
        registerReceiver(mLocationBroadcast, intentFilter);
    }

    @Override
    protected void onStop() {
        if (mLocationBroadcast != null) { unregisterReceiver(mLocationBroadcast); }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INTRO_DATA, homeIntent);
    }

    @Override
    protected void onDestroy() {
        presenter.clearState();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    private void receiveBroadcast(@NonNull final String action) {
        if (action.equalsIgnoreCase(LOCATION_UPDATE)) { presenter.getWeather(); }
    }

    @Override
    public void onPermissionEvent(final boolean isGranted) {
        if (isGranted) {
            if (pageIndex == LOCATION_PERMISSION_PAGE) { handlePositiveLocationButton(); }
            else if (pageIndex == ACCOUNT_PERMISSION_PAGE) { handlePositiveAccountButton(); }

        } else if (hasAllData()) { closeView(); } else {

            if (pageIndex > 1) { goToLastPage(); }
            else { goToSearchPage(); }
        }
    }

    private void handlePositiveLocationButton() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mAppDataProvider.getGpsPermissionStatus()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.push_up_in,
                            R.anim.push_up_out)
                    .replace(R.id.introFragmentContainer, new NewsIntroPage())
                    .commit();
            pageIndex++;
            askLocationPermIfNeeded();
            startService(new Intent(getApplicationContext(), LocationTracker.class));

        } else { askLocationPermIfNeeded(); }
    }

    private void handlePositiveAccountButton() {
            if (hasAllData()) { closeView(); }
            else {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.push_up_in,
                                R.anim.push_up_out)
                        .replace(R.id.introFragmentContainer, new WaitingPage())
                        .commit();
                pageIndex++;
            }

    }

    private boolean hasAllData() {
        return homeIntent.hasExtra(NEWS_DATA_KEY) && homeIntent.hasExtra(WEATHER_INFO_KEY);
    }

    private void goToLastPage() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.push_up_in,
                        R.anim.push_up_out)
                .replace(R.id.introFragmentContainer, new WaitingPage())
                .commit();
        pageIndex++;
    }

    private void goToSearchPage() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.push_up_in,
                        R.anim.push_up_out)
                .replace(R.id.introFragmentContainer, new SearchLocationPage())
                .commit();
        pageIndex++;
    }

    @Override
    public void addNewsToData(@NonNull final List<Article> articles) {
        homeIntent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        if (pageIndex > 2 && homeIntent.hasExtra(WEATHER_INFO_KEY)) {
            closeView();
        }
    }

    @Override
    public void addWeatherToData(@NonNull WeatherData weather) {
        homeIntent.putExtra(WEATHER_INFO_KEY, weather);
        presenter.setFromCurrentWeather();
        if (pageIndex > 2 && homeIntent.hasExtra(NEWS_DATA_KEY)) {
            closeView();
        }
    }

    @Override
    public Context getContext() { return getApplicationContext(); }

    @Override
    public void onLocationEvent(@NonNull final String locationEvent) {
        receiveBroadcast(locationEvent);
        if (pageIndex > 1) { pageIndex = 3; }
        goToLastPage();
    }

    @Override
    public void onNewsError() {
        Snackbar.make(binding.introLayoutRoot, R.string.connection_issue, Snackbar.LENGTH_LONG)
                .show();
        presenter.getNews();
    }

    @Override
    public void onWeatherError() {
        Snackbar.make(binding.introLayoutRoot, R.string.connection_issue, Snackbar.LENGTH_LONG)
                .show();
        presenter.getWeather();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && pageIndex == LOCATION_PERMISSION_PAGE) {

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.push_up_in,
                                R.anim.push_up_out)
                        .replace(R.id.introFragmentContainer, new NewsIntroPage())
                        .commitAllowingStateLoss();
                pageIndex++;
                startService(new Intent(getApplicationContext(), LocationTracker.class));
        }
    }

    private void closeView() {
        homeIntent.setClass(this, DBWeatherActivity.class);
        startActivity(homeIntent);
        presenter.clearState();
        finish();
    }
}
