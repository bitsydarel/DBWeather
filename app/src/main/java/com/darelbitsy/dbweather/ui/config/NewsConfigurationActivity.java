package com.darelbitsy.dbweather.ui.config;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.NewsConfigurationActivityBinding;
import com.darelbitsy.dbweather.ui.config.adapter.NewsConfigurationAdapter;
import com.darelbitsy.dbweather.ui.main.WeatherActivity;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;

/**
 * Created by Darel Bitsy on 11/02/17.
 */
public class NewsConfigurationActivity extends AppCompatActivity {
    private NewsConfigurationActivityBinding mNewsBinding;
    private NewsConfigurationAdapter adapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsBinding = DataBindingUtil.setContentView(this, R.layout.news_configuration_activity);
        setSupportActionBar(mNewsBinding.newsConfigToolbar.rootNewsConfigToolbar);

        final DatabaseOperation database = DatabaseOperation.getInstance(this);

        adapter = new NewsConfigurationAdapter(database.getNewsSources(),
                database, mNewsBinding.getRoot());

        mNewsBinding.newsConfigRecyclerView.setAdapter(adapter);
        mNewsBinding.newsConfigRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        mNewsBinding.newsConfigRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNewsBinding.newsConfigToolbar.backToMainActivity.setOnClickListener(v -> {
            final Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.getRxSubscription()
                .clear();
    }
}
