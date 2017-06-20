package com.dbeginc.dbweather.ui.main.config;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ConfigTabLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.config.adapter.ConfigurationItemAdapter;
import com.dbeginc.dbweather.ui.main.config.help.HelpFragment;
import com.dbeginc.dbweather.ui.main.config.managecities.ManageCitiesFragment;
import com.dbeginc.dbweather.ui.main.config.newssource.NewsSourceFragment;
import com.google.android.gms.ads.AdRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 28.05.17.
 * Configuration Activity
 */

public class ConfigFragment extends BaseFragment implements IConfigurationView {

    private static final int MANAGE_LOCATIONS = 242;
    private static final int WEATHER_NOTIFICATION = 223;
    private static final int NEWS_SOURCE = 33;
    private static final int TRANSLATE_NEWS = 380;
    private static final int HELP = 243;

    private ConfigTabLayoutBinding binding;
    private ManageCitiesFragment manageCitiesFragment;
    private ConfigurationItemAdapter adapter;
    private final List<GeoName> locationList = new ArrayList<>();
    private final PublishSubject<Pair<Integer, Boolean>> configClickEvent = PublishSubject.create();
    private final Handler handler = new Handler();
    private ConfigPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ConfigPresenter(this, mAppDataProvider);
        presenter.loadUserCities();
        adapter = setupConfigurationItem();
    }

    private ConfigurationItemAdapter setupConfigurationItem() {
        final ConfigurationItem manageCitiesConfig = new ConfigurationItem();
        manageCitiesConfig.icon.set(R.drawable.city_location_icon);
        manageCitiesConfig.id.set(MANAGE_LOCATIONS);
        manageCitiesConfig.label.set(getAppContext().getString(R.string.manage_cities));
        manageCitiesConfig.hasSwitch.set(false);

        final ConfigurationItem weatherNotification = new ConfigurationItem();
        weatherNotification.icon.set(R.drawable.ic_notifications_black_24dp);
        weatherNotification.id.set(WEATHER_NOTIFICATION);
        weatherNotification.label.set(getAppContext().getString(R.string.notification_config_title));
        weatherNotification.hasSwitch.set(true);
        weatherNotification.isChecked.set(mAppDataProvider.getWeatherNotificationStatus());

        final ConfigurationItem selectNewsSource = new ConfigurationItem();
        selectNewsSource.icon.set(R.drawable.news_icon);
        selectNewsSource.id.set(NEWS_SOURCE);
        selectNewsSource.label.set(getAppContext().getString(R.string.select_news_source_config_title));
        selectNewsSource.hasSwitch.set(false);

        final ConfigurationItem translateNewsFeed = new ConfigurationItem();
        translateNewsFeed.icon.set(R.drawable.ic_g_translate_black_24dp);
        translateNewsFeed.id.set(TRANSLATE_NEWS);
        translateNewsFeed.label.set(getAppContext().getString(R.string.news_translation_config_title));
        translateNewsFeed.hasSwitch.set(true);
        translateNewsFeed.isChecked.set(mAppDataProvider.getNewsTranslationStatus());

        final ConfigurationItem helpConfig = new ConfigurationItem();
        helpConfig.icon.set(R.drawable.ic_help_black_24dp);
        helpConfig.id.set(HELP);
        helpConfig.label.set(getAppContext().getString(R.string.help_config));
        helpConfig.hasSwitch.set(false);

        return new ConfigurationItemAdapter(Arrays.asList(manageCitiesConfig, weatherNotification, selectNewsSource, translateNewsFeed, helpConfig),
                configClickEvent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.config_tab_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.subscribeToClickEvent();
        handler.post(this::setupAds);
        handler.post(this::setupListOfConfig);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.clearState();
    }

    private void setupListOfConfig() {
        binding.configurationItems.setAdapter(adapter);
        binding.configurationItems.setLayoutManager(new LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false));
        binding.configurationItems.setHasFixedSize(true);
    }

    private void setupAds() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .addTestDevice("C20BB1C5369BFDFD4992ED89CD62F271")
                .build();
        binding.adVConfig.loadAd(adRequest);
    }

    @Override
    public void loadCities(@NonNull List<GeoName> geoNames) {
        if (locationList.isEmpty()) { locationList.addAll(geoNames); }
        else {
            locationList.clear();
            locationList.addAll(geoNames);
        }
        manageCitiesFragment = ManageCitiesFragment.newInstance(locationList);
    }

    @Override
    public void respondToClick(final Pair<Integer, Boolean> clickEvent) {
        switch (clickEvent.first) {
            case MANAGE_LOCATIONS:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, manageCitiesFragment)
                        .commit();
                onClickEvent(true);
                break;

            case WEATHER_NOTIFICATION:
                mAppDataProvider.setWeatherNotificationStatus(clickEvent.second);
                break;

            case NEWS_SOURCE:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new NewsSourceFragment())
                        .commit();
                onClickEvent(true);
                break;

            case TRANSLATE_NEWS:
                mAppDataProvider.setNewsTranslationStatus(clickEvent.second);
                break;

            case HELP:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HelpFragment())
                        .commit();
                onClickEvent(true);
                break;

            default:
                break;
        }
    }

    @Override
    public PublishSubject<Pair<Integer, Boolean>> getConfigurationItemClickEvent() {
        return configClickEvent;
    }

    @Override
    public PublishSubject<Boolean> getConfigurationBackEvent() {
        return configurationBackEvent;
    }

    @Override
    public void onClickEvent(boolean isChildVisible) {
        binding.setIsChildVisible(isChildVisible);
    }
}
