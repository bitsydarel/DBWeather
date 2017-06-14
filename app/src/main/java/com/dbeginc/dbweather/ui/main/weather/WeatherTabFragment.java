package com.dbeginc.dbweather.ui.main.weather;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.WeatherTabLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.weather.Alert;
import com.dbeginc.dbweather.models.datatypes.weather.HourlyData;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.animation.ForegroundToBackgroundTransformer;
import com.dbeginc.dbweather.ui.main.weather.adapters.CustomFragmentAdapter;
import com.dbeginc.dbweather.ui.main.weather.adapters.HourAdapter;
import com.dbeginc.dbweather.utils.helper.ColorManager;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;

import java.util.List;
import java.util.Locale;

import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_ALERT_ID;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by darel on 28.05.17.
 * Weather Activity
 */

public class WeatherTabFragment extends BaseFragment implements IWeatherView, SearchView.OnSuggestionListener, ViewPager.OnPageChangeListener {
    private static final int LAST_PAGE_INDEX = 6;
    private WeatherTabLayoutBinding layoutBinding;
    private WeatherPresenter presenter;
    private CustomFragmentAdapter fragmentAdapter;
    private HourAdapter hourAdapter;
    private WeatherData weatherData;
    private GeoName selectedLocation;
    private Drawable floatingButtonIcon;
    private String gpsLocationName;
    private ComponentName componentName;
    private int floatingButtonCount = 1;
    private final Handler handler = new Handler();
    private final ColorManager colorManager = ColorManager.getInstance();
    private static final String COMPONENT_NAME_KEY = "COMPONENT_NAME_KEY";
    private final ViewGroup.LayoutParams floatingButtonLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    public static synchronized WeatherTabFragment newInstance(@NonNull final WeatherData weatherData) {
        final WeatherTabFragment weatherTabFragment = new WeatherTabFragment();
        final Bundle args = new Bundle();
        args.putParcelable(WEATHER_INFO_KEY, weatherData);
        weatherTabFragment.setArguments(args);
        return weatherTabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new WeatherPresenter(this, mAppDataProvider);

        final Bundle arguments = getArguments();
        if (savedInstanceState != null) {
            final WeatherData data = savedInstanceState.getParcelable(WEATHER_INFO_KEY);
            weatherData = data != null ? data : weatherData;
            componentName = savedInstanceState.getParcelable(COMPONENT_NAME_KEY);

        } else if (arguments != null) {
            final WeatherData data = arguments.getParcelable(WEATHER_INFO_KEY);
            weatherData = data != null ? data : weatherData;
            if (presenter.isCurrentWeatherFromGps() || gpsLocationName == null) {
                gpsLocationName = weatherData.getWeatherInfoList().get(0).locationName.get();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        componentName = getActivity().getComponentName();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(WEATHER_INFO_KEY, weatherData);
        outState.putParcelable(COMPONENT_NAME_KEY, componentName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutBinding = DataBindingUtil.inflate(inflater, R.layout.weather_tab_layout, container, false);
        return layoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.post(() -> setupLocationLookupFeature(view.getContext()));
        handler.post(this::setupAds);
        setupViewPager(weatherData);
        setupHourlyRecyclerView(weatherData.getHourlyWeatherList());


        final int backgroundColor = colorManager.getBackgroundColor(weatherData.getWeatherInfoList().get(0).icon.get());
        layoutBinding.nsvContainer.setBackgroundResource(backgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layoutBinding.nsvContainer.setNestedScrollingEnabled(true);
            layoutBinding.nsvContainer.setVerticalScrollBarEnabled(true);
            layoutBinding.nsvContainer.setScrollbarFadingEnabled(true);
            layoutBinding.nsvContainer.setSmoothScrollingEnabled(true);
        }
        layoutBinding.viewPager.setPageTransformer(false, new ForegroundToBackgroundTransformer());
        layoutBinding.civLeftButton.setOnClickListener(v -> goToPreviousPage());
        layoutBinding.civRightButton.setOnClickListener(v -> goToNextPage());
        layoutBinding.civLeftButton.setRippleColor(resources.getColor(R.color.colorAccent));
        layoutBinding.civRightButton.setRippleColor(resources.getColor(R.color.colorAccent));
        layoutBinding.srlContainer.setOnRefreshListener(() -> handler.post(() -> {
            if (presenter.isCurrentWeatherFromGps()) { presenter.getWeather(); }
            else {
                presenter.getWeatherForCity(selectedLocation);
                presenter.setCurrentWeatherFromGps(false);
            }
        }));
        handleBackButtonVisibility(0);

        setupFloatingMenu();
        handler.post(presenter::loadUserCities);
        presenter.subscribeToEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!presenter.isFirstRun() && isNetworkAvailable() && presenter.isCurrentWeatherFromGps()) {
            presenter.getWeather();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.clearState();
    }

    @Override
    public void setupLocationLookupFeature(@NonNull final Context context) {
        final SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        layoutBinding.searchLocationView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        layoutBinding.searchLocationView.setIconifiedByDefault(false);
        layoutBinding.searchLocationView.setSubmitButtonEnabled(false);
        layoutBinding.searchLocationView.setOnSuggestionListener(this);
    }

    @Override
    public void showWeather(@NonNull final WeatherData weatherData) {
        this.weatherData = weatherData;
        fragmentAdapter.updateFragments(weatherData.getWeatherInfoList());
        if (hourAdapter != null) { hourAdapter.updateData(weatherData.getHourlyWeatherList()); }
        if (weatherData.getAlertList() != null) { showWeatherAlerts(weatherData.getAlertList()); }
        final int backgroundColor = colorManager.getBackgroundColor(weatherData.getWeatherInfoList().get(0).icon.get());
        layoutBinding.nsvContainer.setBackgroundResource(backgroundColor);
        layoutBinding.srlContainer.setRefreshing(false);
        weatherDataUpdateEvent.onNext(weatherData);
        if (presenter.isCurrentWeatherFromGps()) {
            gpsLocationName = this.weatherData.getWeatherInfoList().get(0).locationName.get();
            layoutBinding.currentLocationMenuItem.setLabelText(gpsLocationName);
        }
    }

    @Override
    public void showWeatherAlerts(@NonNull List<Alert> listWeatherAlert) {
        final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        for (final Alert alert : listWeatherAlert) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

            final Intent browserIntent = new Intent()
                    .setAction(Intent.ACTION_VIEW)
                    .setData(Uri.parse(alert.getUri().isEmpty() ? "https://github.com/404" : alert.getUri()))
                    .addCategory(Intent.CATEGORY_BROWSABLE);

            final int notificationId = WEATHER_ALERT_ID;

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(alert.getTitle());
            builder.setContentText(alert.getDescription());
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            builder.setCategory(NotificationCompat.CATEGORY_ALARM);
            builder.setContentIntent(PendingIntent.getActivity(getActivity(), notificationId, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

            builder.setContentInfo(String.format(Locale.getDefault(),
                    getString(R.string.notification_alert_city)
                    , weatherData.getWeatherInfoList().get(0).locationName.get()));

            notificationManager.notify(notificationId, builder.build());
        }
    }

    @Override
    public Context getAppContext() { return super.getAppContext(); }

    @Override
    public PublishSubject<String> getLocationUpdateEvent() { return locationUpdateEvent; }

    @Override
    public PublishSubject<String> getVoiceSearchEvent() { return voiceQuery; }

    @Override
    public void onLocationUpdate(@NonNull String action) {
        if (action.equalsIgnoreCase(LOCATION_UPDATE) && !presenter.isCurrentWeatherFromGps()) {
            if (isNetworkAvailable()) { presenter.getWeather(); }
            else { showNetworkNotAvailableMessage(); }
        }
    }

    /**
     * This method handle voice query event
     *
     * @param query user voice query
     */
    @Override
    public void onVoiceQueryReceived(@NonNull final String query) {
        layoutBinding.searchLocationView.setQuery(query, false);
        layoutBinding.searchLocationView.requestFocusFromTouch();
    }

    @Override
    public boolean isNetworkAvailable() { return super.isNetworkAvailable(); }

    @Override
    public void showNetworkNotAvailableMessage() {
        Snackbar.make(layoutBinding.weatherTabLayout, R.string.network_unavailable_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void showWeatherError() {
        Snackbar.make(layoutBinding.weatherTabLayout, getString(R.string.weather_error_message), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, v -> presenter.retryWeatherRequest())
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void loadUserCities(@NonNull final List<GeoName> userCities) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            floatingButtonIcon = VectorDrawableCompat.create(resources, R.drawable.city_location_icon, getActivity() != null ? getActivity().getTheme() : null);
        } else {
            floatingButtonIcon = ResourcesCompat.getDrawable(resources, R.drawable.city_location_icon, getActivity() != null ? getActivity().getTheme() : null);
        }

        final int locationSize = userCities.size() > 6 ? 6 : userCities.size();

        for (int index = 0; index < locationSize; index++ ) {
            addFloatingButtonToMenu(userCities.get(index));
        }
    }

    private void addFloatingButtonToMenu(GeoName location) {
        final FloatingActionButton button = new FloatingActionButton(getAppContext());
        button.setLabelText(String.format(Locale.getDefault(), "%s, %s", location.getName(), location.getCountryName()));
        button.setImageDrawable(floatingButtonIcon);
        button.setColorNormal(Color.WHITE);
        button.setColorPressedResId(R.color.colorPrimary);
        button.setColorRippleResId(R.color.colorPrimaryDark);
        button.setLayoutParams(floatingButtonLayoutParams);
        button.setOnClickListener(v -> handler.post(() -> {
            presenter.setCurrentWeatherFromGps(false);
            presenter.doWeSaveWeather(true);
            presenter.getWeatherForCity(location);
            selectedLocation = location;
            layoutBinding.srlContainer.setRefreshing(true);
            layoutBinding.floatingLocationsMenu.close(true);
        }));
        layoutBinding.floatingLocationsMenu.addMenuButton(button);
        floatingButtonCount++;
    }

    @Override
    public void notifySuccessfullAddedLocation() {
        Snackbar.make(layoutBinding.weatherTabLayout, R.string.successfully_added_city, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.WHITE)
                .show();
    }

    @Override
    public void addLocationToMenu(@NonNull final GeoName location) { addFloatingButtonToMenu(location); }

    @Override
    public boolean onSuggestionSelect(int i) { return true; }

    @Override
    public boolean onSuggestionClick(int i) {
        selectedLocation = mListOfLocation.get(i);

        final DialogInterface.OnClickListener cancelLocationClick = (dialog, which) -> dialog.cancel();
        final DialogInterface.OnClickListener displayClick = (dialog, which) -> handler.post(() -> {
            presenter.getWeatherForCity(selectedLocation);
            presenter.setCurrentWeatherFromGps(false);
            presenter.doWeSaveWeather(false);
            handler.postAtTime(this::closeSoftKeyboard, 5000);
            layoutBinding.srlContainer.setRefreshing(true);
        });
        final DialogInterface.OnClickListener addToFavorite = (dialog, which) -> handler.post(() -> {
            presenter.doWeSaveWeather(true);
            presenter.setCurrentWeatherFromGps(false);
            presenter.getWeatherForCity(selectedLocation);
            presenter.addToFavorite(selectedLocation);
            if (floatingButtonCount < 7) {
                presenter.isLocationInDatabase(
                        String.format(Locale.getDefault(), "%s, %s", selectedLocation.getName(), selectedLocation.getCountryName()),
                        selectedLocation
                );
            }
            handler.postAtTime(this::closeSoftKeyboard, 5000);
            layoutBinding.srlContainer.setRefreshing(true);
        });

        new AlertDialog.Builder(getActivity())
                .setMessage(String.format(Locale.getDefault(),
                        getAppContext().getString(R.string.alert_add_location_text),
                        selectedLocation.getName()))
                .setNegativeButton(android.R.string.cancel, cancelLocationClick)
                .setNeutralButton(R.string.display, displayClick)
                .setPositiveButton(R.string.display_add, addToFavorite)
                .create()
                .show();
        return true;
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int i) {
        handleBackButtonVisibility(i);
        handleNextButtonVisibility(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    private void setupAds() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .addTestDevice("C20BB1C5369BFDFD4992ED89CD62F271")
                .build();
        layoutBinding.adVMain.loadAd(adRequest);
    }

    private void setupFloatingMenu() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            layoutBinding.currentLocationMenuItem.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.current_location_icon, getActivity() != null ? getActivity().getTheme() : null));
            layoutBinding.floatingLocationsMenu.getMenuIconView().setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.add_location_icon, getActivity() != null ? getActivity().getTheme() : null));
        } else {
            layoutBinding.currentLocationMenuItem.setImageDrawable(ResourcesCompat.getDrawable(resources,
                    R.drawable.current_location_icon, getActivity() != null ? getActivity().getTheme() : null));
            layoutBinding.floatingLocationsMenu.getMenuIconView()
                    .setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.add_location_icon, getActivity() != null ? getActivity().getTheme() : null));
        }

        layoutBinding.currentLocationMenuItem.setOnClickListener(v -> handler.post(() -> {
            presenter.setCurrentWeatherFromGps(true);
            presenter.doWeSaveWeather(false);
            presenter.getWeather();
            layoutBinding.srlContainer.setRefreshing(true);
            layoutBinding.floatingLocationsMenu.close(true);
        }));

        layoutBinding.floatingLocationsMenu.setOnMenuToggleListener(b -> {
            if (b) { handleMenuButtonClicked(); }
            else {
                final int currentItem = layoutBinding.viewPager.getCurrentItem();
                handleBackButtonVisibility(currentItem);
                handleNextButtonVisibility(currentItem);
            }
        });
        layoutBinding.currentLocationMenuItem.setLabelText(gpsLocationName);
    }

    private void handleMenuButtonClicked() {
        if (layoutBinding.civRightButton.getVisibility() == View.VISIBLE) {
            layoutBinding.civRightButton.setVisibility(View.INVISIBLE);
        }
        if (layoutBinding.civLeftButton.getVisibility() == View.VISIBLE) {
            layoutBinding.civLeftButton.setVisibility(View.INVISIBLE);
        }
    }

    private void handleBackButtonVisibility(final int position) {
        if (position == 0) {
            layoutBinding.civLeftButton.setVisibility(View.INVISIBLE);
            layoutBinding.civLeftButton.setEnabled(false);

        } else {
            layoutBinding.civLeftButton.setVisibility(View.VISIBLE);
            layoutBinding.civLeftButton.setEnabled(true);
        }
    }

    private void handleNextButtonVisibility(final int position) {
        if (position == LAST_PAGE_INDEX) {
            layoutBinding.civRightButton.setVisibility(View.INVISIBLE);
            layoutBinding.civRightButton.setEnabled(false);

        } else {
            layoutBinding.civRightButton.setVisibility(View.VISIBLE);
            layoutBinding.civRightButton.setEnabled(true);
        }
    }

    /**
     * Handle The Back Navigation Button Clicked
     */
    private void goToPreviousPage() {
        layoutBinding.viewPager.setCurrentItem(layoutBinding.viewPager.getCurrentItem() - 1, true);
    }

    private void goToNextPage() {
        layoutBinding.viewPager.setCurrentItem(layoutBinding.viewPager.getCurrentItem() + 1);
    }

    /**
     * Setup Hourly RecyclerView
     * @param hourlyData a list of hourly weather data to display
     */
    private void setupHourlyRecyclerView(@NonNull final List<HourlyData> hourlyData) {
        hourAdapter = new HourAdapter(hourlyData, presenter.getRxSubscriptions());
        layoutBinding.hourlyRecyclerView.setHasFixedSize(true);
        layoutBinding.hourlyRecyclerView.setAdapter(hourAdapter);
        layoutBinding.hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(getAppContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * Setup Weather ViewPager
     * @param weatherData weather Data
     */
    private void setupViewPager(WeatherData weatherData) {
        fragmentAdapter = new CustomFragmentAdapter(getChildFragmentManager(),
                weatherData.getWeatherInfoList());

        layoutBinding.weatherTabLayout.setBackgroundColor(colorManager.getBackgroundColor(weatherData.getWeatherInfoList().get(0).icon.get()));
        layoutBinding.viewPager.setAdapter(fragmentAdapter);
        layoutBinding.viewPager.addOnPageChangeListener(this);
    }

    private void closeSoftKeyboard() {
        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        layoutBinding.searchLocationView.setQuery("", false);
        layoutBinding.searchLocationView.setIconified(false);
        layoutBinding.searchLocationView.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(layoutBinding.searchLocationView.getWindowToken(), 0);
    }
}