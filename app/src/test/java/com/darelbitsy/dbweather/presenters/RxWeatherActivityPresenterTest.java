package com.darelbitsy.dbweather.presenters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.presenters.activities.RxWeatherActivityPresenter;
import com.darelbitsy.dbweather.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.views.activities.IWeatherActivityView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 23/04/17.
 * RxWeatherActivityPresenter Test
 */
public class RxWeatherActivityPresenterTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Context context;

    @Mock
    IUserCitiesRepository repository;

    @Mock
    MockWeatherActivity view;
    private RxWeatherActivityPresenter mPresenter;

    @Before
    public void setUp() {
        Mockito.when(context.getApplicationContext()).thenReturn(context);

        mPresenter = new RxWeatherActivityPresenter(context,
                repository,
                view);
    }

    @Test
    public void shouldPassUserCitiesToPresenter() {

        final List<GeoName> geoNames = Arrays.asList(new GeoName(), new GeoName(), new GeoName());

        Mockito.when(repository.getUserCities()).thenReturn(Single.just(geoNames));

        mPresenter.loadUserCitiesMenu();

        Mockito.verify(view).setupNavigationDrawerWithCities(geoNames);
    }

    @Test
    public void shouldHandleNoUserCitiesPassedToPresenter() {

        Mockito.when(repository.getUserCities()).thenReturn(Single.just(Collections.emptyList()));

        mPresenter.loadUserCitiesMenu();

        Mockito.verify(view).setupNavigationDrawerWithNoCities();
    }

    @Test
    public void shouldNotPassWeatherInfoToPresenter() {
        mPresenter.getWeather();

        Mockito.verify(view).showNetworkWeatherErrorMessage();
    }

    @Test
    public void shouldNotPassNewsDataToPresenter() {
        mPresenter.getNews();

        Mockito.verify(view).showNetworkNewsErrorMessage();
    }

    // Extended this class because my presenter need IWeatherActivityView with List of weatherInfo and List of Article
    private class MockWeatherActivity implements IWeatherActivityView<Pair<List<WeatherInfo>, List<HourlyData>>, List<Article>> {

        @Override
        public void showWeather(final Pair<List<WeatherInfo>, List<HourlyData>> listListPair) {}
        @Override
        public void showNews(final List<Article> articles) {}
        @Override
        public void showNetworkWeatherErrorMessage() {}
        @Override
        public void showNetworkNewsErrorMessage() {}
        @Override
        public void setupNavigationDrawerWithCities(final List<GeoName> listOfLocation) {}
        @Override
        public void setupNavigationDrawerWithNoCities() {}

        @Override
        public void saveState(final Bundle bundle) {}
    }
}