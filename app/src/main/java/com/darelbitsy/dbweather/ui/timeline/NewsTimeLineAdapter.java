package com.darelbitsy.dbweather.ui.timeline;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.NewsTimelineItemBinding;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Bitsy Darel on 14.05.17.
 * News TimeLine RecyclerView Adapter
 */

public class NewsTimeLineAdapter extends RecyclerView.Adapter<NewsTimeLineAdapter.TimeLineViewHolder> {

    private final List<Article> articles = new ArrayList<>();
    private final NewsTimeLinePresenter presenter;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.newInstance();
    private final CompositeDisposable compositeDisposable;

    NewsTimeLineAdapter(@Nonnull final List<Article> articles, @Nonnull final NewsTimeLinePresenter presenter,
                        @Nonnull final CompositeDisposable compositeDisposable) {

        this.presenter = presenter;

        if (this.articles.isEmpty()) { this.articles.addAll(articles); }
        else {
            this.articles.clear();
            this.articles.addAll(articles);
        }

        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TimeLineViewHolder(DataBindingUtil.inflate(inflater, R.layout.news_timeline_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) { holder.bindNews(articles.get(position)); }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position, final List<Object> payloads) {
        for (final Object object : payloads) {
            final Bundle bundle = (Bundle) object;
            this.articles.add(bundle.getInt("ID"), bundle.getParcelable(ConstantHolder.NEWS_DATA_KEY));
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    void updateDate(@Nonnull final List<Article> articles) {
        compositeDisposable.add(Single.fromCallable(() -> DiffUtil.calculateDiff(new ArticleDiffCallback(this.articles, articles)))
                .subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                    @Override
                    public void onSuccess(final DiffUtil.DiffResult diffResult) {
                        diffResult.dispatchUpdatesTo(NewsTimeLineAdapter.this);
                    }

                    @Override
                    public void onError(final Throwable throwable) {}
                }));
    }

    class TimeLineViewHolder extends RecyclerView.ViewHolder {
        final NewsTimelineItemBinding newsTimelineBinding;
        Article mNews;

        final View.OnClickListener newsDetailsClickAction = view -> presenter.showDetails(mNews.getArticleUrl());

        TimeLineViewHolder(final NewsTimelineItemBinding itemBinding) {
            super(itemBinding.getRoot());
            newsTimelineBinding = itemBinding;
        }

        void bindNews(final Article news) {
            mNews = news;
            newsTimelineBinding.setArticle(news);
            newsTimelineBinding.btnOpenInWbv
                    .setOnClickListener(newsDetailsClickAction);
            newsTimelineBinding.executePendingBindings();
        }
    }
}
