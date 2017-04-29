package com.darelbitsy.dbweather.presenters;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.ui.main.RxWeatherActivityPresenter;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.ui.main.IWeatherActivityView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Darel Bitsy on 23/04/17.
 * RxWeatherActivityPresenter Test
 */
public class RxWeatherActivityPresenterTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    IUserCitiesRepository repository;

    @Mock
    IWeatherActivityView view;

    @Mock
    AppDataProvider mDataProvider;

    @Mock
    Weather mWeather;

    private RxWeatherActivityPresenter mPresenter;
    private final List<GeoName> mEmptyList = Collections.emptyList();
    private final List<GeoName> MANY_GEONAMES = Arrays.asList(new GeoName(), new GeoName(), new GeoName());

    private final List<Article> articles = new ArrayList<>(Arrays.asList(new Article(), new Article()));
    private final List<Article> emptyArticles = Collections.emptyList();

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        mPresenter = new RxWeatherActivityPresenter(
                repository,
                view,
                mDataProvider);

    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldConfigureViewWithData() {
        Mockito.when(mDataProvider.getNewsFromDatabase()).thenReturn(Single.just(articles));
        Mockito.when(mDataProvider.getWeatherFromDatabase()).thenReturn(Single.just(mWeather));
        Mockito.when(repository.getUserCities()).thenReturn(Single.just(MANY_GEONAMES));

        mPresenter.configureView();

        Mockito.verify(view).showNews(articles);
        Mockito.verify(view).showNetworkWeatherErrorMessage();
        Mockito.verify(view).setupNavigationDrawerWithCities(MANY_GEONAMES);
    }

    @Test
    public void shouldPassUserCitiesToPresenter() {
        Mockito.when(repository.getUserCities()).thenReturn(Single.just(MANY_GEONAMES));

        mPresenter.loadUserCitiesMenu();

        Mockito.verify(view).setupNavigationDrawerWithCities(MANY_GEONAMES);
    }

    @Test
    public void shouldHandleNoUserCitiesPassedToPresenter() {
        Mockito.when(repository.getUserCities()).thenReturn(Single.just(mEmptyList));

        mPresenter.loadUserCitiesMenu();

        Mockito.verify(view).setupNavigationDrawerWithNoCities();
    }

    @Test
    public void shouldPassNewsInfoToPresenter() {
        Mockito.when(mDataProvider.getNewsFromApi()).thenReturn(Single.just(articles));

        mPresenter.getNews();

        Mockito.verify(view).showNews(articles);
    }

    @Test
    public void shouldLoadNewsInfoToPresenter() {
        Mockito.when(mDataProvider.getNewsFromDatabase()).thenReturn(Single.just(articles));

        mPresenter.loadNews();

        Mockito.verify(view).showNews(articles);
    }

    @Test
    public void shouldNotPassWeatherInfoToPresenter() {
        Mockito.when(mDataProvider.getWeatherFromApi()).thenReturn(Single.just(mWeather));

        mPresenter.getWeather();

        Mockito.verify(view).showNetworkWeatherErrorMessage();
    }

    @Test
    public void shouldNotPassNewsDataToPresenter() {
        Mockito.when(mDataProvider.getNewsFromApi()).thenReturn(Single.just(emptyArticles));

        mPresenter.getNews();

        Mockito.verify(view).showNetworkNewsErrorMessage();
    }
}