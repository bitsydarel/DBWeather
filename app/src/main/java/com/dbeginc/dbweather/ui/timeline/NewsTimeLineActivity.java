package com.dbeginc.dbweather.ui.timeline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ActivityNewsTimeLineBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.main.WeatherActivity;
import com.dbeginc.dbweather.ui.timeline.webclients.CustomWebClient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.disposables.CompositeDisposable;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CUSTOM_TAB_PACKAGE_NOT_FOUND;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

public class NewsTimeLineActivity extends BaseActivity implements INewsTimeLineView {

    private ActivityNewsTimeLineBinding binding;
    private NewsTimeLineAdapter adapter;
    private NewsTimeLinePresenter presenter;
    private final List<Article> mNews = new ArrayList<>();
    private final Intent mHomeIntent = new Intent();
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private CustomTabsIntent customTabsIntent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_time_line);
        setSupportActionBar(binding.toolbar.TimelineToolbar);

        presenter = new NewsTimeLinePresenter(this, mAppDataProvider, subscriptions);
        setupWebView();
        setupTimeLine();

        final Intent intent = getIntent();

        if (intent != null && intent.hasExtra(NEWS_DATA_KEY)) { showNewsFeed(intent.getParcelableArrayListExtra(NEWS_DATA_KEY)); }
        else {
            binding.srlRefreshTimeline.setRefreshing(true);
            presenter.loadNews();
        }

        binding.srlRefreshTimeline.setOnRefreshListener(() -> {
            presenter.getNews();
            binding.srlRefreshTimeline.setRefreshing(true);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribeToPublisher();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String customTabPackage = mAppDataProvider.getCustomTabPackage();

        if (!customTabPackage.equalsIgnoreCase(CUSTOM_TAB_PACKAGE_NOT_FOUND)) {
            customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setToolbarColor(Color.BLUE)
                    .enableUrlBarHiding()
                    .build();
            customTabsIntent.intent.setPackage(customTabPackage);
        }
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        binding.toolbar.backToMainActivity.setOnClickListener(view -> onBackPressed());
        presenter.getWeatherForBackHome();
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.clear();
    }

    @Override
    public void onBackPressed() {
        if (binding.wbvBrowser.canGoBack()) { binding.wbvBrowser.goBack(); }
        else {
            if (binding.wbvBrowser.getVisibility() == View.VISIBLE) {
                adapter.updateDate(mNews);
                binding.toolbar.toolbarTitle.setText(getString(R.string.timeline_label));
                manageVisibility(false);

            } else {
                mHomeIntent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) mNews);
                startActivity(new Intent(getApplicationContext(), WeatherActivity.class));
                finish();
            }
        }
    }

    @Override
    public void showNewsFeed(@Nonnull final List<Article> articles) {
        manageVisibility(false);
        binding.srlRefreshTimeline.setRefreshing(false);

        if (adapter == null) {
            adapter = new NewsTimeLineAdapter(articles, presenter.getDetailsEvent(), subscriptions);
            binding.rcvTimeline.setAdapter(adapter);

        } else { adapter.updateDate(articles); }

        mNews.clear();
        mNews.addAll(articles);
    }

    @Override
    public void showDetails(@Nonnull final String url) {
        if (customTabsIntent != null) { customTabsIntent.launchUrl(this, Uri.parse(url)); }
        else {
            manageVisibility(true);
            binding.wbvBrowser.loadUrl(url);
        }
    }

    @Override
    public void showError(final Throwable throwable) {
        Snackbar.make(binding.getRoot(), R.string.news_error_message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public Context getContext() { return getApplicationContext(); }

    @Override
    public void addWeatherToHomeIntent(@Nonnull final WeatherData weatherData) {
        mHomeIntent.putExtra(WEATHER_INFO_KEY, weatherData);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        final WebSettings webSettings = binding.wbvBrowser.getSettings();
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            webSettings.setSupportZoom(true);

        } else { webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); }

        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        binding.wbvBrowser.setWebViewClient(new CustomWebClient());
        binding.wbvBrowser.setWebChromeClient(new CustomChromeClient());
        binding.wbvBrowser.setVerticalScrollBarEnabled(true);
    }

    private void setupTimeLine() {
        binding.rcvTimeline.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcvTimeline.setHasFixedSize(true);
    }

    private void manageVisibility(final boolean showShowDetails) {
        if (showShowDetails) {
            binding.srlRefreshTimeline.setVisibility(View.GONE);
            binding.rcvTimeline.setVisibility(View.GONE);
            binding.wbvBrowser.setVisibility(View.VISIBLE);
            binding.toolbar.backToMainActivity.setImageResource(R.drawable.back_button_icon);

        } else {
            binding.wbvBrowser.setVisibility(View.GONE);
            binding.rcvTimeline.setVisibility(View.VISIBLE);
            binding.srlRefreshTimeline.setVisibility(View.VISIBLE);
            binding.toolbar.backToMainActivity.setImageResource(R.drawable.home_icon);
        }
    }

    private class CustomChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(final WebView view, final String title) {
            super.onReceivedTitle(view, title);
            binding.toolbar.toolbarTitle.setText(title);
        }
    }
}
