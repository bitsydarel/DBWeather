package com.dbeginc.dbweather.ui.main.config.newssource;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsSourceLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.news.Source;
import com.dbeginc.dbweather.models.datatypes.news.Sources;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.config.newssource.adapter.NewsSourceAdapter;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 11.06.17.
 * News Source
 */

public class NewsSourceFragment extends BaseFragment implements NewsSourceView {

    private NewsSourceLayoutBinding binding;
    private NewsSourceAdapter adapter;
    private NewsSourcePresenter presenter;
    private final PublishSubject<Source> itemTouchEvent = PublishSubject.create();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new NewsSourcePresenter(this, mAppDataProvider, itemTouchEvent);
        presenter.getSources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.news_source_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar.configToolbar);
        binding.toolbar.configToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getContext().getString(R.string.subscribe_to_news));
        }
        binding.toolbar.configToolbar.setNavigationOnClickListener(v -> configurationBackEvent.onNext(false));
        binding.swipeRefresh.setOnRefreshListener(() -> presenter.getSources());
        binding.loadingProgress
                .getIndeterminateDrawable()
                .setColorFilter(ResourcesCompat.getColor(view.getResources(), R.color.newsTabColor, appContext.getTheme()),
                        PorterDuff.Mode.SRC_IN);
        presenter.subscribeToTouchEvent();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            inflater.inflate(R.menu.empty_menu, menu);
        }
    }

    @Override
    public void onDestroyView() {
        presenter.clearState();
        super.onDestroyView();
    }

    @Override
    public void loadSources(@NonNull final Sources sources) {
        if (adapter == null) {
            adapter = new NewsSourceAdapter(sources.getSources(), itemTouchEvent);
        }
        binding.newsSourcesRecyclerView.setAdapter(adapter);
        binding.newsSourcesRecyclerView.setLayoutManager(new LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false));
        binding.loadingProgress.setVisibility(View.GONE);
        binding.newsSourcesRecyclerView.setVisibility(View.VISIBLE);
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void notifyNewsSuccessfullySaved() {
        Snackbar.make(binding.newsSourceLayout, getString(R.string.successfully_saved_changes), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void notifyErrorWhileSaved() {
        Snackbar.make(binding.newsSourceLayout, getString(R.string.unsuccessfully_saved), Snackbar.LENGTH_LONG)
                .show();
    }
}
