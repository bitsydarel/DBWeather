package com.dbeginc.dbweather.ui.main.news.feed.adapter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.dbeginc.dbweather.ui.main.news.feed.AdViewPool;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.OLD_INDEX;


/**
 * Created by Bitsy Darel on 14.05.17.
 * News TimeLine RecyclerView Adapter
 */

public class NewFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final PublishSubject<String> eventDispatcher;
    private final CompositeDisposable compositeDisposable;
    private AdViewPool adViewPool;
    private RecyclerView recyclerView;
    private final List<Object> articles = new ArrayList<>();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private static final int NEWS_FEED_ITEM_TYPE = 0;
    private static final int AD_VIEW_TYPE = 1;

    public NewFeedAdapter(@Nonnull final List<Article> articles, @Nonnull final PublishSubject<String> eventDispatcher,
                          @Nonnull final CompositeDisposable compositeDisposable) {

        this.articles.addAll(convertToAdList(articles));
        this.eventDispatcher = eventDispatcher;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case AD_VIEW_TYPE:
                if (adViewPool == null) {
                    adViewPool = new AdViewPool(inflater, R.layout.news_ad_item, parent);
                    if (adViewPool.isAdViewPoolEmpty()) {
                        adViewPool.addBackToPool(inflater.inflate(R.layout.news_ad_item, parent, false));
                    }
                }
                return new AdViewViewHolder(adViewPool.getAdView());
            case NEWS_FEED_ITEM_TYPE:
            default:
                return new TimeLineViewHolder(DataBindingUtil.inflate(inflater, R.layout.news_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == NEWS_FEED_ITEM_TYPE) {
            final TimeLineViewHolder viewHolder = (TimeLineViewHolder) holder;
            viewHolder.bindNews((Article) articles.get(position));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position, final List<Object> payloads) {
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
    public int getItemViewType(final int position) {
        if (position == 0) { return NEWS_FEED_ITEM_TYPE; }
        else {
            return (position % 3 == 0) ? AD_VIEW_TYPE : NEWS_FEED_ITEM_TYPE;
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

    class AdViewViewHolder extends RecyclerView.ViewHolder {
        AdViewViewHolder(View itemView) {
            super(itemView);
        }
    }

    private List<Article> convertToAdList(@NonNull final List<Article> articles) {
        final List<Article> newList = new ArrayList<>(articles);
        final int size = newList.size();
        for (int i = 0; i < size; i++) {
            if (i != 0 && i % 3 == 0) {
                newList.add(i, null);
            }
        }
        return newList;
    }

    public void updateDate(@Nonnull final List<Article> articles) {

        compositeDisposable.add(
                Single.create((SingleOnSubscribe<List<Article>>) e -> {
                    try {
                        final List<Article> newList = convertToAdList(articles);
                        if (!e.isDisposed()) {
                            e.onSuccess(newList);
                        }
                    } catch (Exception ex) { if (!e.isDisposed()) { e.onError(ex); } }

                }).map(articles1 -> DiffUtil.calculateDiff(new ArticleDiffCallback(NewFeedAdapter.this.articles, articles1)))
                        .subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getComputationThread())
                        .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                            @Override
                            public void onSuccess(final DiffUtil.DiffResult diffResult) {
                                NewFeedAdapter.this.articles.clear();
                                NewFeedAdapter.this.articles.addAll(articles);
                                diffResult.dispatchUpdatesTo(NewFeedAdapter.this);
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
}
