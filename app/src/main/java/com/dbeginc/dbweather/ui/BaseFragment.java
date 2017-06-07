package com.dbeginc.dbweather.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 29.05.17.
 */

public class BaseFragment extends Fragment {
    @Inject
    public AppDataProvider mAppDataProvider;
    @Inject
    public PublishSubject<String> locationUpdateEvent;
    @Inject
    public List<GeoName> mListOfLocation;
    @Inject
    public PublishSubject<WeatherData> weatherDataUpdateEvent;

    @Inject
    public PublishSubject<List<Article>> newsUpdateEvent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DBWeatherApplication.getComponent()
                .inject(this);
    }

    protected boolean isNetworkAvailable() {
        final NetworkInfo networkInfo;
        final ConnectivityManager manager = (ConnectivityManager)
                getActivity().getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
