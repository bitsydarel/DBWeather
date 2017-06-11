package com.dbeginc.dbweather.ui;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 29.05.17.
 * Base Fragment
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
    @Inject
    public PublishSubject<LiveNews> youtubeEvents;
    @Inject
    public PublishSubject<Pair<DataSnapshot, String>> liveDatabaseUpdateEvent;
    @Inject
    public Resources resources;
    @Inject
    public Context appContext;

    public static final String LOADING = "LOADING";

    public static final String NOT_LOADING = "NOT_LOADING";
    public static final String VISIBLE = "VISIBLE";
    public static final String NOT_VISIBLE = "NOT_VISIBLE";

    protected static PublishSubject<String> newsLayoutDataUpdateEvent = PublishSubject.create();
    protected static PublishSubject<Boolean> newsDataUpdateEvent = PublishSubject.create();
    protected static PublishSubject<Throwable> errorEventDispatcher = PublishSubject.create();

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

    protected Context getAppContext() {
        return appContext;
    }
}
