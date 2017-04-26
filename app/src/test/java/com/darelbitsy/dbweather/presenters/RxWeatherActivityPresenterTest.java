package com.darelbitsy.dbweather.presenters;

import android.content.Context;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.presenters.activities.RxWeatherActivityPresenter;
import com.darelbitsy.dbweather.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.provider.schedulers.ISchedulersProvider;
import com.darelbitsy.dbweather.views.activities.IWeatherActivityView;

import org.junit.After;
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
    Context context;

    @Mock
    IUserCitiesRepository repository;

    @Mock
    IWeatherActivityView view;

    private RxWeatherActivityPresenter mPresenter;

    @Before
    public void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(schedulerCallable -> Schedulers.trampoline());

        Mockito.when(context.getApplicationContext()).thenReturn(context);

        mPresenter = new RxWeatherActivityPresenter(
                context,
                repository,
                view,
                Schedulers.trampoline());

    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
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
}