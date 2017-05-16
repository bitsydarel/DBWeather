package com.darelbitsy.dbweather.ui.main.adapters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.NewsListItemBinding;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.provider.firebase.IAnalyticProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.ui.ArticleDiffCallback;
import com.darelbitsy.dbweather.ui.newsdetails.NewsDialogActivity;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.INDEX;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by Darel Bitsy on 16/02/17.
 * News Adapter for the news recycler view
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>  {
    private final List<Article> mNewses = new ArrayList<>();
    private final IAnalyticProvider analyticProvider;
    private final RxSchedulersProvider rxSchedulersProvider = RxSchedulersProvider.newInstance();
    private CompositeDisposable compositeDisposable;

    public NewsAdapter(final List<Article> newses, final IAnalyticProvider analyticProvider, final CompositeDisposable compositeDisposable) {
        mNewses.addAll(newses);
        this.compositeDisposable = compositeDisposable;
        this.analyticProvider = analyticProvider;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(final ViewGroup parent,
                                             final int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NewsViewHolder(DataBindingUtil.inflate(inflater, R.layout.news_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) { holder.bindNews(mNewses.get(position)); }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position, final List<Object> payloads) {
        for (final Object object : payloads) {
            final Bundle bundle = (Bundle) object;
            mNewses.add(bundle.getInt(INDEX), bundle.getParcelable(NEWS_DATA_KEY));
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return mNewses.size();
    }

    public void updateContent(final List<Article> newses) {
        compositeDisposable.add(Single.fromCallable(() -> DiffUtil.calculateDiff(new ArticleDiffCallback(mNewses, newses)))
                .subscribeOn(rxSchedulersProvider.getComputationThread())
                .observeOn(rxSchedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                    @Override
                    public void onSuccess(final DiffUtil.DiffResult diffResult) {
                        diffResult.dispatchUpdatesTo(NewsAdapter.this);
                    }
                    @Override
                    public void onError(final Throwable throwable) {}
                }));
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private final NewsListItemBinding newsContainer;
        Article mNews;

        final View.OnClickListener newsOnClickListener = view -> {

            final Intent newsActivity = new Intent(view.getContext(), NewsDialogActivity.class);
            newsActivity.putExtra(NEWS_DATA_KEY, mNews);
            analyticProvider.logEvent("NEWS_FEED_OPENED", new Bundle());
            view.getContext().startActivity(newsActivity);
        };

        NewsViewHolder(final NewsListItemBinding newsListItemBinding) {
            super(newsListItemBinding.getRoot());
            newsContainer = newsListItemBinding;
            newsContainer.newsLayout.setOnClickListener(newsOnClickListener);
        }

        void bindNews(final Article news) {
            mNews = news;
            newsContainer.setArticle(news);
            if (news.getAuthor().contains("sport")) { newsContainer.newsFrom.setBackgroundColor(Color.BLUE); }
            else { newsContainer.newsFrom.setBackgroundColor(Color.RED); }

            newsContainer.newsDescription.setText(news.getTitle());
        }
    }
}
