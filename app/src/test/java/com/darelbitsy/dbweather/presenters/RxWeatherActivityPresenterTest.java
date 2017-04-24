package com.darelbitsy.dbweather.weather;

import android.os.Bundle;
import android.test.mock.MockContext;

import com.darelbitsy.dbweather.presenters.RxWeatherActivityPresenter;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.models.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.views.activities.IWeatherActivityView;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Darel Bitsy on 23/04/17.
 * RxWeatherActivityPresenter Test
 */
public class RxWeatherActivityPresenterTest {

    @Test
    public void shouldPass() {
        Assert.assertEquals(1, 1);
    }

    @Test
    public void shouldPassUserCityToPresenter() {

        final MockContext context = new MockContext();

        final IWeatherActivityView<List<WeatherInfo>, List<Article>> view =
                new MockWeatherActivity();

        final IUserCitiesRepository repository= new MockUserCitiesRepository();

        final RxWeatherActivityPresenter presenter =
                new RxWeatherActivityPresenter(context, repository, view);

        presenter.loadUserCities();

    }

    private class MockUserCitiesRepository implements IUserCitiesRepository {
        @Override
        public List<GeoName> getUserCities() {
            return null;
        }
    }

    private class MockWeatherActivity implements IWeatherActivityView<List<WeatherInfo>, List<Article>> {
        @Override
        public void requestWeatherUpdate() {

        }

        @Override
        public void requestNewsUpdate() {

        }

        @Override
        public void showWeather(final List<WeatherInfo> weatherInfoList) {

        }

        @Override
        public void showNews(final List<Article> articles) {

        }

        @Override
        public void showNetworkWeatherErrorMessage() {

        }

        @Override
        public void showNetworkNewsErrorMessage() {

        }

        @Override
        public void setupNavigationDrawer(final List<GeoName> listOfLocation) {
            Assert.assertNotNull("SetupNavigationDrawer received null", listOfLocation);
        }

        @Override
        public void saveState(final Bundle bundle) {

        }
    }
}