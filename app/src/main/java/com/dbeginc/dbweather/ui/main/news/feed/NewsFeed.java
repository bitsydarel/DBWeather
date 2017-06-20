package com.dbeginc.dbweather.ui.main.news.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsFeedTabLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.news.feed.adapter.NewFeedAdapter;
import com.dbeginc.dbweather.ui.main.news.webclients.CustomWebClient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.disposables.CompositeDisposable;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CUSTOM_TAB_PACKAGE_NOT_FOUND;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

/**
 * Created by darel on 07.06.17.
 * News Feed fragment
 */

public class NewsFeed extends BaseFragment implements INewsFeed {

    private NewsFeedPresenter mPresenter;
    private CustomTabsIntent mCustomTabsIntent;
    private NewsFeedTabLayoutBinding mBinding;
    private NewFeedAdapter mAdapter;
    private final Handler handler = new Handler();
    private final List<Article> mArticles = new ArrayList<>();
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public static synchronized NewsFeed newInstance(@NonNull final List<Article> articles) {
        final NewsFeed newsFeedFragment = new NewsFeed();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        newsFeedFragment.setArguments(bundle);
        return newsFeedFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NewsFeedPresenter(this, mAppDataProvider, mSubscriptions);

        final Bundle arguments = getArguments();

        if (savedInstanceState != null) {
            final ArrayList<Article> list = savedInstanceState.getParcelableArrayList(NEWS_DATA_KEY);
            if (list != null) {
                mArticles.clear();
                mArticles.addAll(list);
            }
        } else if (arguments != null) {
            final ArrayList<Article> list = arguments.getParcelableArrayList(NEWS_DATA_KEY);
            if (list != null && mArticles.isEmpty()) { mArticles.addAll(list); }
        }
        mAdapter = new NewFeedAdapter(mArticles, mPresenter.getDetailsEvent(), mSubscriptions);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) mArticles);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.news_feed_tab_layout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.post(this::setupNewsFeed);
        handler.post(this::setupWebView);
        mSubscriptions.add(
                newsDataUpdateEvent.subscribe(aBoolean -> {
                    if (aBoolean) { mPresenter.getNews(); }
                }, throwable -> Log.e(TAG, throwable.getMessage()))
        );
    }

    private void setupNewsFeed() {
        mBinding.rcvTimeline.setAdapter(mAdapter);
        mBinding.rcvTimeline.setLayoutManager(new LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final String customTabPackage = mAppDataProvider.getCustomTabPackage();

        if (!customTabPackage.equalsIgnoreCase(CUSTOM_TAB_PACKAGE_NOT_FOUND)) {
            mCustomTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setToolbarColor(getResources().getColor(R.color.colorPrimaryDark))
                    .enableUrlBarHiding()
                    .build();
            mCustomTabsIntent.intent.setPackage(customTabPackage);
        }
    }

    @Nullable
    @Override
    public View getView() { return mBinding.getRoot(); }

    @Override
    public void onResume() {
        super.onResume();
        if (mArticles.isEmpty()) {
            newsLayoutDataUpdateEvent.onNext(LOADING);
            if (isNetworkAvailable()) { mPresenter.getNews(); }
            else { mPresenter.loadNews(); }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSubscriptions.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSubscriptions.clear();
    }

    @Override
    public void showNews(@NonNull final List<Article> articles) {
        manageVisibility(false);
        newsLayoutDataUpdateEvent.onNext(NOT_LOADING);
        mAdapter.updateDate(articles);

        if (!mArticles.equals(articles)) {
            mArticles.clear();
            mArticles.addAll(articles);
            newsUpdateEvent.onNext(articles);
        }
    }

    @Override
    public void showDetails(@Nonnull String url) {
        if (mCustomTabsIntent != null) { mCustomTabsIntent.launchUrl(getActivity(), Uri.parse(url)); }
        else {
            manageVisibility(true);
            mBinding.wbvBrowser.loadUrl(url);
        }
    }

    @Override
    public void showError(Throwable throwable) { errorEventDispatcher.onNext(throwable); }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        final WebSettings webSettings = mBinding.wbvBrowser.getSettings();
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

        mBinding.wbvBrowser.setWebViewClient(new CustomWebClient());
        mBinding.wbvBrowser.setWebChromeClient(new WebChromeClient());
        mBinding.wbvBrowser.setVerticalScrollBarEnabled(true);
    }

    private void manageVisibility(final boolean showShowDetails) {
        if (showShowDetails) {
            newsLayoutDataUpdateEvent.onNext(NOT_VISIBLE);
            mBinding.rcvTimeline.setVisibility(View.GONE);
            mBinding.wbvBrowser.setVisibility(View.VISIBLE);

        } else {
            mBinding.wbvBrowser.setVisibility(View.GONE);
            mBinding.rcvTimeline.setVisibility(View.VISIBLE);
            newsLayoutDataUpdateEvent.onNext(VISIBLE);
        }
    }
}
