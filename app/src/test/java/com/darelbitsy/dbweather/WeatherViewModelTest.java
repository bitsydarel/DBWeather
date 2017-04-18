package com.darelbitsy.dbweather;

import android.content.Context;
import android.test.mock.MockContext;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Darel Bitsy on 14/04/17.
 */

public class WeatherViewModelTest {

    @Test
    public void TestGetWeatherInfo() {
        final Context mockContext = new MockContext();
        final WeatherViewModel model = new WeatherViewModel(mockContext);
        Assert.assertTrue(model.getFullWeatherInfo()
                .blockingGet()
                .cityName.get()
                .equalsIgnoreCase("Ternopil, Ukraine"));
    }
}
