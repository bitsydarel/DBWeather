package com.darelbitsy.dbweather.ui.main;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Daily;
import com.darelbitsy.dbweather.models.datatypes.weather.DailyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Hourly;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.repository.IUserCitiesRepository;

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

    private final Weather mWeather = new Weather();

    private RxWeatherActivityPresenter mPresenter;
    private static final List<GeoName> mEmptyList = Collections.emptyList();
    private final List<GeoName> MANY_GEONAMES = Arrays.asList(new GeoName(), new GeoName(), new GeoName());

    private final List<Article> articles = new ArrayList<>(Arrays.asList(new Article(), new Article()));
    private final List<Article> emptyArticles = Collections.emptyList();


    /**
     * Setup the test class before running the tests
     * define trampoline scheduler as main and io thread
     */
    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        mPresenter = new RxWeatherActivityPresenter(
                repository,
                view,
                mDataProvider);

        mWeather.setHourly(new Hourly());
        mWeather.getHourly().setData(Arrays.asList(new HourlyData(), new HourlyData()));
        mWeather.setDaily(new Daily());
        mWeather.getDaily().setData(Arrays.asList(new DailyData(), new DailyData()));
    }

    /**
     * Reset the schedulers state
     */
    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }


    /**
     * Testing case when presenter should receive user
     * subscribed city from database
     */
    @Test
    public void shouldPassUserCitiesToPresenter() {
        Mockito.when(repository.getUserCities()).thenReturn(Single.just(MANY_GEONAMES));

        mPresenter.loadUserCitiesMenu();

        Mockito.verify(view).setupNavigationDrawerWithCities(MANY_GEONAMES);
    }

    /**
     * Test case when presenter receive no user
     * subscribed city from database
     */
    @Test
    public void shouldHandleNoUserCitiesPassedToPresenter() {
        Mockito.when(repository.getUserCities()).thenReturn(Single.just(mEmptyList));

        mPresenter.loadUserCitiesMenu();

        Mockito.verify(view).setupNavigationDrawerWithNoCities();
    }

    /**
     * Test case when presenter receive news from api
     */
    @Test
    public void shouldPassNewsToPresenter() {
        Mockito.when(mDataProvider.getNewsFromApi()).thenReturn(Single.just(articles));

        mPresenter.getNews();

        Mockito.verify(view).showNews(articles);
    }

    /**
     * Test case when presenter receive news from database
     */
    @Test
    public void shouldLoadNewsInfoToPresenter() {
        Mockito.when(mDataProvider.getNewsFromDatabase()).thenReturn(Single.just(articles));

        mPresenter.loadNews();

        Mockito.verify(view).showNews(articles);
    }

    /**
     * Test case when presenter don't receive news from database
     */
    @Test
    public void shouldNotPassNewsDataToPresenter() {
        Mockito.when(mDataProvider.getNewsFromApi()).thenReturn(Single.just(emptyArticles));

        mPresenter.getNews();

        Mockito.verify(view).showNetworkNewsErrorMessage();
    }
}