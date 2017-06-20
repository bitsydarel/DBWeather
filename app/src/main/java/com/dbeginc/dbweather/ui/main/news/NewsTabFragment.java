package com.dbeginc.dbweather.ui.main.news;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsTabLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.news.feed.NewsFeed;
import com.dbeginc.dbweather.ui.main.news.live.NewsLiveFragment;
import com.google.android.gms.ads.AdRequest;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by darel on 28.05.17.
 * News Fragment
 */

public class NewsTabFragment extends BaseFragment implements INewsView, OnTabSelectListener {
    private static final String NEWS_FEED_FRAGMENT = "NEWS_FEED_FRAGMENT";
    private static final String NEWS_LIVE_FRAGMENT = "NEWS_LIVE_FRAGMENT";
    private NewsTabLayoutBinding mBinding;
    private NewsFeed newsFeedFragment;
    private NewsLiveFragment newsLiveFragmentFragment;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final List<Article> mArticles = new ArrayList<>();
    private final List<LiveNews> liveNewsList = new ArrayList<>();

    public static synchronized NewsTabFragment newInstance(@NonNull final List<Article> articles) {
        final NewsTabFragment fragment = new NewsTabFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (savedInstanceState != null && savedInstanceState.containsKey(NEWS_DATA_KEY)) {
                if (mArticles.isEmpty()) {
                    mArticles.clear();
                    mArticles.addAll(savedInstanceState.getParcelableArrayList(NEWS_DATA_KEY));
                    liveNewsList.clear();
                    liveNewsList.addAll(savedInstanceState.getParcelableArrayList(LIVE_SOURCE));
                }

            if (newsLiveFragmentFragment == null) {
                newsLiveFragmentFragment = NewsLiveFragment.newInstance(liveNewsList);
            }
            if (newsFeedFragment == null) { newsFeedFragment = NewsFeed.newInstance(mArticles); }

        } else if (arguments != null){
            mArticles.addAll(arguments.getParcelableArrayList(NEWS_DATA_KEY));
            mAppDataProvider.getLiveSources()
                    .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                    .observeOn(schedulersProvider.getUIScheduler())
                    .subscribe(list -> {
                        NewsTabFragment.this.liveNewsList.addAll(list);
                        newsLiveFragmentFragment = NewsLiveFragment.newInstance(list);
                    });
            newsFeedFragment = NewsFeed.newInstance(mArticles);
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
        setupAds();
        setupTopTabs();
        mBinding.srlRefreshTimeline.setOnRefreshListener(this::refreshRequest);
        subscriptions.add(
                newsLayoutDataUpdateEvent.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(this::handleRequestUpdate)
        );
        subscriptions.add(
                errorEventDispatcher.subscribeOn(schedulersProvider.getComputationThread())
                        .subscribe(this::showError, this::showError)
        );
    }

    private void setupTopTabs() {
        mBinding.newsTopTabs.setOnTabSelectListener(this, true);
    }

    @Override
    public void onDestroyView() {
        subscriptions.clear();
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View getView() {
        return mBinding.getRoot();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) mArticles);
        outState.putParcelableArrayList(LIVE_SOURCE, (ArrayList<? extends Parcelable>) liveNewsList);
    }

    @Override
    public void showError(final Throwable throwable) {
        Snackbar.make(mBinding.newsTabLayout, R.string.news_error_message, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void handleRequestUpdate(@NonNull final String status) {
        switch (status) {
            case VISIBLE:
                mBinding.srlRefreshTimeline.setVisibility(View.VISIBLE);
                break;

            case NOT_VISIBLE:
                mBinding.srlRefreshTimeline.setVisibility(View.GONE);
                break;

            case LOADING:
                mBinding.srlRefreshTimeline.setRefreshing(true);
                break;

            case NOT_LOADING:
                mBinding.srlRefreshTimeline.setRefreshing(false);
                break;

            default:
                mBinding.srlRefreshTimeline.setRefreshing(true);
                break;
        }
    }

    @Override
    public void refreshRequest() {
        mBinding.srlRefreshTimeline.setRefreshing(true);
        newsDataUpdateEvent.onNext(true);
    }

    private void setupAds() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .addTestDevice("C20BB1C5369BFDFD4992ED89CD62F271")
                .build();
        mBinding.newsViewAds.loadAd(adRequest);
    }

    @Override
    public void onTabSelected(int id) {

        if (id == R.id.tab_news_feed) {
            mBinding.srlRefreshTimeline.setVisibility(View.VISIBLE);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.newsContainer, newsFeedFragment, NEWS_FEED_FRAGMENT)
                    .commit();
        } else {
            mBinding.srlRefreshTimeline.setVisibility(View.GONE);
            getChildFragmentManager()
                  .beginTransaction()
                  .replace(R.id.newsContainer, newsLiveFragmentFragment, NEWS_LIVE_FRAGMENT)
                  .commit();
        }
    }
}
