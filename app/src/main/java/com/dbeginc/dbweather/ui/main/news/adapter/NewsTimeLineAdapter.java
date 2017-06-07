package com.dbeginc.dbweather.ui.main.news.adapter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsListItemBinding;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.ui.main.news.adapter.ArticleDiffCallback.OLD_INDEX;

/**
 * Created by Bitsy Darel on 14.05.17.
 * News TimeLine RecyclerView Adapter
 */

public class NewsTimeLineAdapter extends RecyclerView.Adapter<NewsTimeLineAdapter.TimeLineViewHolder> {

    private final PublishSubject<String> eventDispatcher;
    private final CompositeDisposable compositeDisposable;
    private RecyclerView recyclerView;
    private final List<Article> articles;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

    public NewsTimeLineAdapter(@Nonnull final List<Article> articles, @Nonnull final PublishSubject<String> eventDispatcher,
                        @Nonnull final CompositeDisposable compositeDisposable) {

        this.articles = new ArrayList<>(articles);
        this.eventDispatcher = eventDispatcher;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TimeLineViewHolder(DataBindingUtil.inflate(inflater, R.layout.news_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) { holder.bindNews(articles.get(position)); }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position, final List<Object> payloads) {
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
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void updateDate(@Nonnull final List<Article> articles) {

        compositeDisposable.add(
                Single.fromCallable(() -> DiffUtil.calculateDiff(new ArticleDiffCallback(this.articles, articles)))
                        .subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getComputationThread())
                        .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                            @Override
                            public void onSuccess(final DiffUtil.DiffResult diffResult) {
                                NewsTimeLineAdapter.this.articles.clear();
                                NewsTimeLineAdapter.this.articles.addAll(articles);
                                diffResult.dispatchUpdatesTo(NewsTimeLineAdapter.this);
                            }
                            @Override
                            public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                        })
        );
    }

    private void notifyNewData(final int position) {
        compositeDisposable.add(
                Observable.just(position)
                        .retryUntil(() -> !recyclerView.isComputingLayout())
                        .subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getComputationThread())
                        .subscribe(this::notifyItemChanged,Crashlytics::logException)
        );
    }

    class TimeLineViewHolder extends RecyclerView.ViewHolder {
        private final NewsListItemBinding newsTimelineBinding;
        private String articleUrl;

        final View.OnClickListener newsDetailsClickAction = v -> eventDispatcher.onNext(articleUrl);

        TimeLineViewHolder(final NewsListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            newsTimelineBinding = itemBinding;
            newsTimelineBinding.ibExpandNews.setOnClickListener(newsDetailsClickAction);
        }

        void bindNews(final Article news) {
            newsTimelineBinding.setArticle(news);
            articleUrl = news.getArticleUrl();
            newsTimelineBinding.executePendingBindings();
        }
    }
}
