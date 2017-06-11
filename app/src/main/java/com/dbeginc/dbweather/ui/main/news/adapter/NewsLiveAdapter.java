package com.dbeginc.dbweather.ui.main.news.adapter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsLiveItemsBinding;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.ui.main.news.adapter.ArticleDiffCallback.OLD_INDEX;

/**
 * Created by darel on 07.06.17.
 * Live news source recyclerView
 */

public class NewsLiveAdapter extends RecyclerView.Adapter<NewsLiveAdapter.NewsLiveViewHolder> {


    private final List<LiveNews> liveNewsList = new ArrayList<>();
    private final PublishSubject<LiveNews> streamSelectEvent;
    private final PublishSubject<Boolean> updateLiveSourceDataEvent;
    private final CompositeDisposable compositeDisposable;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private RecyclerView recyclerView;

    public NewsLiveAdapter(@NonNull final List<LiveNews> liveNewsList, @NonNull final PublishSubject<LiveNews> streamSelectedEvent,
                           @NonNull final PublishSubject<Boolean> updateLiveSourceDataEvent, @NonNull final CompositeDisposable compositeDisposable) {
        this.liveNewsList.addAll(liveNewsList);
        this.streamSelectEvent = streamSelectedEvent;
        this.updateLiveSourceDataEvent = updateLiveSourceDataEvent;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public NewsLiveViewHolder onCreateViewHolder(final ViewGroup parent, final int i) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NewsLiveViewHolder(DataBindingUtil.inflate(inflater, R.layout.news_live_items, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsLiveViewHolder newsLiveViewHolder, final int position) {
        newsLiveViewHolder.bindView(liveNewsList.get(position));
    }

    @Override
    public void onBindViewHolder(NewsLiveViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) { onBindViewHolder(holder, position); }
        else {
            compositeDisposable.add(
                    Observable.fromArray(payloads.toArray())
                            .subscribeOn(schedulersProvider.getComputationThread())
                            .flatMap(o -> Observable.just(((Bundle) o).getInt(OLD_INDEX)))
                            .repeatUntil(() -> !recyclerView.isComputingLayout())
                            .observeOn(schedulersProvider.getUIScheduler())
                            .unsubscribeOn(schedulersProvider.getComputationThread())
                            .subscribe(this::notifyNewData)
            );
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    private void notifyNewData(final Integer position) {
        compositeDisposable.add(
                Observable.just(position)
                        .retryUntil(() -> !recyclerView.isComputingLayout())
                        .subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getComputationThread())
                        .subscribe(this::notifyItemChanged, Crashlytics::logException)
        );
    }

    @Override
    public int getItemCount() {
        return liveNewsList.size();
    }

    public void updateLiveSource(List<LiveNews> liveNewsList) {
        compositeDisposable.add(
                Single.fromCallable(() -> DiffUtil.calculateDiff(new LiveSourceDiffCallback(this.liveNewsList, liveNewsList)))
                        .subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getComputationThread())
                        .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                            @Override
                            public void onSuccess(final DiffUtil.DiffResult diffResult) {
                                NewsLiveAdapter.this.liveNewsList.clear();
                                NewsLiveAdapter.this.liveNewsList.addAll(liveNewsList);
                                diffResult.dispatchUpdatesTo(NewsLiveAdapter.this);
                            }
                            @Override
                            public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                        })
        );
    }

    class NewsLiveViewHolder extends RecyclerView.ViewHolder {
        private final NewsLiveItemsBinding binding;

        NewsLiveViewHolder(final NewsLiveItemsBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
            binding.getRoot().setOnClickListener(v -> streamSelectEvent.onNext(binding.getLiveNews()));
            binding.liveNewsReport.setOnClickListener(v -> updateLiveSourceDataEvent.onNext(true));
        }

        void bindView(final LiveNews liveNews) {
            binding.setLiveNews(liveNews);
            binding.executePendingBindings();
        }
    }
}
