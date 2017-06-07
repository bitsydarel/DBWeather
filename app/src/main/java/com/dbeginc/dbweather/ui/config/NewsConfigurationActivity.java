package com.dbeginc.dbweather.ui.config;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsConfigurationActivityBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.ui.config.adapter.NewsConfigurationAdapter;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Darel Bitsy on 11/02/17.
 * News Configuration Activity
 */
public class NewsConfigurationActivity extends AppCompatActivity implements INewsConfiguration {
    private NewsConfigurationActivityBinding mNewsBinding;
    private final PublishSubject<Pair<String, Pair<Integer, Integer>>> adapterEventPublisher = PublishSubject.create();
    @Inject
    AppDataProvider dataProvider;
    private NewsConfigurationPresenter presenter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWeatherApplication.getComponent().inject(this);

        mNewsBinding = DataBindingUtil.setContentView(this, R.layout.news_configuration_activity);
        presenter = new NewsConfigurationPresenter(this, dataProvider);
        presenter.loadNewsSourcesConfigs();
        presenter.prepareBackHome();
//        setSupportActionBar(mNewsBinding.newsConfigToolbar.rootNewsConfigToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mNewsBinding.newsConfigToolbar.backToMainActivity.setOnClickListener(v -> {
//            startActivity(mHomeIntent);
//            finish();
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.clearState();
    }

    @Override
    public void notifySuccessSavedConfig() {
        Snackbar.make(mNewsBinding.getRoot(),
                getApplicationContext().getString(R.string.successfully_saved_configuration), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void notifyErrorWhileSavingConfig() {
        Snackbar.make(mNewsBinding.getRoot(),
                getApplicationContext().getString(R.string.unsuccessfully_saved_configuration), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public PublishSubject<Pair<String, Pair<Integer, Integer>>> getConfigChangeEvent() {
        return adapterEventPublisher;
    }

    @Override
    public void showConfigItems(@Nonnull final Map<String, Pair<Integer, Integer>> configData) {
        final NewsConfigurationAdapter adapter = new NewsConfigurationAdapter(configData, adapterEventPublisher);
        mNewsBinding.newsConfigRecyclerView.setAdapter(adapter);
        mNewsBinding.newsConfigRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        mNewsBinding.newsConfigRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void addNewsToHomeIntent(@Nonnull final List<Article> articles) {
//        mHomeIntent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
    }

    @Override
    public void addWeatherToHomeIntent(@Nonnull final WeatherData weatherData) {
//        mHomeIntent.putExtra(WEATHER_INFO_KEY, weatherData);
    }

    @Override
    public Context getContext() { return getApplicationContext(); }


}
