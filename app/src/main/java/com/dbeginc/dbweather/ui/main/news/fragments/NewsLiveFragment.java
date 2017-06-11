package com.dbeginc.dbweather.ui.main.news.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsLiveTabLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.customviews.DraggableListener;
import com.dbeginc.dbweather.ui.main.news.adapter.NewsLiveAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE;

/**
 * Created by darel on 07.06.17.
 * News Live Fragment
 */

public class NewsLiveFragment extends BaseFragment implements INewsLive, DraggableListener {
    private NewsLiveAdapter newsLiveAdapter;
    private NewsLiveTabLayoutBinding binding;
    private YoutubeFragment youtubeFragment;
    private LinearLayoutManager mainLinearLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private NewsLivePresenter presenter;
    private boolean isActivityAttached;
    private final Handler handler = new Handler();
    private final List<LiveNews> liveNewsList = new ArrayList<>();
    private final PublishSubject<Boolean> updateLiveSourceDataEvent = PublishSubject.create();

    public static synchronized NewsLiveFragment newInstance(@NonNull final List<LiveNews> liveNewsList) {
        final NewsLiveFragment newsLiveFragmentFragment = new NewsLiveFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LIVE_SOURCE, (ArrayList<? extends Parcelable>) liveNewsList);
        newsLiveFragmentFragment.setArguments(bundle);
        return newsLiveFragmentFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        presenter = new NewsLivePresenter(this, mAppDataProvider, updateLiveSourceDataEvent);
        final Bundle arguments = getArguments();

        if (bundle != null && bundle.containsKey(LIVE_SOURCE)) {
            liveNewsList.clear();
            liveNewsList.addAll(arguments.getParcelableArrayList(LIVE_SOURCE));
            youtubeFragment = (YoutubeFragment) getChildFragmentManager().findFragmentById(R.id.youtubePlayerContainer);

        } else if (arguments != null && arguments.containsKey(LIVE_SOURCE) && liveNewsList.isEmpty()) {
            liveNewsList.addAll(arguments.getParcelableArrayList(LIVE_SOURCE));
            youtubeFragment = YoutubeFragment.newInstance(liveNewsList.get(0).liveUrl.get());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIVE_SOURCE, (ArrayList<? extends Parcelable>) liveNewsList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.news_live_tab_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mainLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        isActivityAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isActivityAttached = false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.post(this::setupLiveView);
        binding.youtubeViewContainer.setDraggableListener(this);
    }

    @Override
    public void onStop() {
        presenter.clearState();
        super.onStop();
    }

    @Override
    public void setupLiveView() {
        if (newsLiveAdapter == null) {
            newsLiveAdapter = new NewsLiveAdapter(liveNewsList, youtubeEvents, updateLiveSourceDataEvent, presenter.getSubscriptions());
            binding.listOfLiveNews.setAdapter(newsLiveAdapter);
            binding.listOfLiveNews.setLayoutManager(linearLayoutManager);
            binding.listOfLiveNews.setHasFixedSize(true);
            binding.mainListOfLiveNews.setAdapter(newsLiveAdapter);
            binding.mainListOfLiveNews.setLayoutManager(mainLinearLayoutManager);
            binding.mainListOfLiveNews.setHasFixedSize(true);

        } else {
            binding.listOfLiveNews.setAdapter(newsLiveAdapter);
            binding.listOfLiveNews.setLayoutManager(linearLayoutManager);
            binding.listOfLiveNews.setHasFixedSize(true);
            binding.mainListOfLiveNews.setAdapter(newsLiveAdapter);
            binding.mainListOfLiveNews.setLayoutManager(mainLinearLayoutManager);
            binding.mainListOfLiveNews.setHasFixedSize(true);
        }
        if (isActivityAttached) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.youtubePlayerContainer, youtubeFragment)
                    .commit();
        }

        binding.mainListOfLiveNews.setVisibility(View.GONE);
        handler.postDelayed(() -> {
            binding.youtubeViewContainer.minimize();
            binding.mainListOfLiveNews.setVisibility(View.VISIBLE);
        }, 1000);
    }

    @Override
    public void showLiveView(@NonNull final LiveNews liveNews) {
        binding.youtubeViewContainer.maximize();
        presenter.sendLiveUrlToPlayer(liveNews.liveUrl.get());
    }

    @Override
    public void updateLiveData(@NonNull final List<LiveNews> liveNewsList) {
        newsLiveAdapter.updateLiveSource(liveNewsList);
    }

    /**
     * Called when the view is maximized.
     */
    @Override
    public void onMaximized() {
        binding.mainListOfLiveNews.setVisibility(View.GONE);
    }

    /**
     * Called when the view is minimized.
     */
    @Override
    public void onMinimized() {
        binding.mainListOfLiveNews.setVisibility(View.VISIBLE);
    }

    /**
     * Called when the view is closed to the left.
     */
    @Override
    public void onClosedToLeft() {
        binding.mainListOfLiveNews.setVisibility(View.VISIBLE);
    }

    /**
     * Called when the view is closed to the right.
     */
    @Override
    public void onClosedToRight() {
        binding.mainListOfLiveNews.setVisibility(View.VISIBLE);
    }
}
