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
import com.darelbitsy.dbweather.news.News;
import com.darelbitsy.dbweather.ui.MainActivity;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 16/02/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>  {
    private News[] mNewses;

    public NewsAdapter(News[] newses) { mNewses = Arrays.copyOf(newses, newses.length); }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.bindNews(mNewses[position]);
    }

    @Override
    public int getItemCount() {
        return (mNewses != null  && mNewses.length > 1) ? mNewses.length : 0;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout newsContainer;
        final TextView newsFrom;
        final TextView newsDescription;
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
        }

        void bindNews(News news) {
            try {
                newsUrl = news.getArticleUrl();
                newsContainer.setOnClickListener(newsOnClickListener);
            } catch (URISyntaxException e) {
                newsUrl = "";
                Log.i(MainActivity.TAG, "Error: " + e.getMessage());
            }

            newsFrom.setText(String.format(Locale.getDefault(), itemView
                    .getContext()
                    .getString(R.string.news_from), news.getNewsSource()));

            newsDescription.setText(news.getNewsTitle());
        }
    }
}
