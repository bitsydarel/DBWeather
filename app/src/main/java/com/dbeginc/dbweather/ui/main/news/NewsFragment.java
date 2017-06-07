package com.dbeginc.dbweather.ui.main.news;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsTabLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.news.adapter.NewsTimeLineAdapter;
import com.dbeginc.dbweather.ui.main.news.webclients.CustomWebClient;
import com.google.android.gms.ads.AdRequest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.disposables.CompositeDisposable;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CUSTOM_TAB_PACKAGE_NOT_FOUND;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by darel on 28.05.17.
 * News Fragment
 */

public class NewsFragment extends BaseFragment implements INewsView {
    private NewsTabLayoutBinding mBinding;
    private NewsTimeLineAdapter mAdapter;
    private final List<Article> mArticles = new ArrayList<>();
    private NewsPresenter mPresenter;
    private CustomTabsIntent mCustomTabsIntent;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public static synchronized NewsFragment newInstance(@NonNull final List<Article> articles) {
        final NewsFragment fragment = new NewsFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NewsPresenter(this, mAppDataProvider, mSubscriptions);

        final Bundle arguments = getArguments();

        if (arguments != null) {
            final ArrayList<Article> list = arguments.getParcelableArrayList(NEWS_DATA_KEY);
            if (list != null) {
                mArticles.clear();
                mArticles.addAll(list);
            }
        }
        else if (savedInstanceState != null) {
            final ArrayList<Article> list = savedInstanceState.getParcelableArrayList(NEWS_DATA_KEY);
            if (list != null) {
                mArticles.clear();
                mArticles.addAll(list);
            }
        }

        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.news_tab_layout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupWebView();
        setupAds();
        mBinding.srlRefreshTimeline.setOnRefreshListener(() -> {
            mPresenter.getNews();
            mBinding.srlRefreshTimeline.setRefreshing(true);
        });
    }

    @Nullable
    @Override
    public View getView() {
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mArticles.isEmpty()) {
            mBinding.srlRefreshTimeline.setRefreshing(true);
            mPresenter.loadNews();
        }
        if (!mArticles.isEmpty()) { showNews(mArticles); }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) mArticles);
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

    @Override
    public void showNews(@NonNull List<Article> articles) {
        manageVisibility(false);
        mBinding.srlRefreshTimeline.setRefreshing(false);

        if (mAdapter == null) {
            mAdapter = new NewsTimeLineAdapter(articles, mPresenter.getDetailsEvent(), mSubscriptions);
            mBinding.rcvTimeline.setAdapter(mAdapter);

        } else {
            if (mBinding.rcvTimeline.getAdapter() == null) { mBinding.rcvTimeline.setAdapter(mAdapter); }
            mAdapter.updateDate(articles);
        }

        if (!mArticles.equals(articles)) {
            mArticles.clear();
            mArticles.addAll(articles);
            newsUpdateEvent.onNext(articles);
        }
    }

    @Override
    public void showDetails(@Nonnull final String url) {
        if (mCustomTabsIntent != null) { mCustomTabsIntent.launchUrl(getActivity(), Uri.parse(url)); }
        else {
            manageVisibility(true);
            mBinding.wbvBrowser.loadUrl(url);
        }
    }

    @Override
    public void showError(final Throwable throwable) {
        Snackbar.make(mBinding.newsTabLayout, R.string.news_error_message, Snackbar.LENGTH_LONG)
                .show();
    }

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

    private void setupAds() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .addTestDevice("C20BB1C5369BFDFD4992ED89CD62F271")
                .build();
        mBinding.newsViewAds.loadAd(adRequest);
    }

    private void manageVisibility(final boolean showShowDetails) {
        if (showShowDetails) {
            mBinding.srlRefreshTimeline.setVisibility(View.GONE);
            mBinding.rcvTimeline.setVisibility(View.GONE);
            mBinding.wbvBrowser.setVisibility(View.VISIBLE);

        } else {
            mBinding.wbvBrowser.setVisibility(View.GONE);
            mBinding.rcvTimeline.setVisibility(View.VISIBLE);
            mBinding.srlRefreshTimeline.setVisibility(View.VISIBLE);
        }
    }
}
