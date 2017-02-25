package com.darelbitsy.dbweather.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.model.news.News;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 16/02/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>  {
    private ArrayList<News> mNewses;

    public NewsAdapter(ArrayList<News> newses) {
        mNewses = new ArrayList<>();
        mNewses.addAll(newses);
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.bindNews(mNewses.get(position));
    }

    @Override
    public int getItemCount() {
        return (mNewses != null  && mNewses.size() > 1) ? mNewses.size() : 0;
    }

    public void updateContent(ArrayList<News> newses) {
        mNewses.clear();
        mNewses.addAll(newses);
        notifyDataSetChanged();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout newsContainer;
        final TextView newsFrom;
        final TextView newsDescription;
        final TextView newsPublishedDate;
        String newsUrl;

        final View.OnClickListener newsOnClickListener = view -> {
                Intent browserIntent = new Intent()
                        .setAction(Intent.ACTION_VIEW)
                        .setData(Uri.parse(newsUrl))
                        .addCategory(Intent.CATEGORY_BROWSABLE);
                view.getContext().startActivity(browserIntent);
        };

        NewsViewHolder(View itemView) {
            super(itemView);
            newsContainer = (LinearLayout) itemView.findViewById(R.id.newsLinearLayout);
            newsFrom = (TextView) itemView.findViewById(R.id.newsFrom);
            newsDescription = (TextView) itemView.findViewById(R.id.newsDescription);
            newsPublishedDate = (TextView) itemView.findViewById(R.id.newsDate);
        }

        void bindNews(News news) {
            try {
                newsUrl = news.getArticleUrl();
                newsContainer.setOnClickListener(newsOnClickListener);
            } catch (URISyntaxException e) {
                newsUrl = "";
                Log.i(ConstantHolder.TAG, "Error: " + e.getMessage());
            }

            newsFrom.setText(String.format(Locale.getDefault(), itemView
                    .getContext()
                    .getString(R.string.news_from), news.getNewsSource()));

            newsDescription.setText(news.getNewsTitle());
            newsPublishedDate.setText(news.getPublishedAt());
        }
    }
}
