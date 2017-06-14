package com.dbeginc.dbweather.ui.main.config.adapters;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsSourceItemBinding;
import com.dbeginc.dbweather.models.datatypes.news.Source;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 13.06.17.
 */

public class NewsSourceAdapter extends RecyclerView.Adapter<NewsSourceAdapter.NewsSourceViewHolder> {

    private final List<Source> sources;
    private final PublishSubject<Source> itemTouchEvent;
    private static final Map<String, Integer> MAP_OF_COLOR = new ImmutableMap.Builder<String, Integer>()
            .put("business", Color.parseColor("#ffff4444"))
            .put( "entertainment", Color.parseColor("#ff0099cc"))
            .put("gaming", Color.YELLOW)
            .put("general", R.color.configTabColor)
            .put("music", R.color.newsTabColor)
            .put("politics", Color.parseColor("#3E2723"))
            .put("science-and-nature", Color.parseColor("#ff669900"))
            .put("sport", Color.parseColor("#E91E63"))
            .put("technology", Color.parseColor("#424242"))
            .build();

    public NewsSourceAdapter(@NonNull final List<Source> sources, PublishSubject<Source> itemTouchEvent) {
        this.sources = sources;
        this.itemTouchEvent = itemTouchEvent;
    }

    @Override
    public NewsSourceViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NewsSourceViewHolder(DataBindingUtil.inflate(inflater, R.layout.news_source_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewsSourceViewHolder newsSourceViewHolder, final int position) {
        newsSourceViewHolder.bindSource(sources.get(position));
    }

    @Override
    public int getItemCount() { return sources.size(); }

    class NewsSourceViewHolder extends RecyclerView.ViewHolder {
        private final NewsSourceItemBinding binding;

        NewsSourceViewHolder(final NewsSourceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.sourceSubscribed.setOnClickListener(v -> {
                binding.getSource().isSubscribed.set(!binding.getSource().isSubscribed.get());
                itemTouchEvent.onNext(binding.getSource());
            });
        }

        void bindSource(@NonNull final Source source) {
            binding.setSource(source);
            binding.sourceLogo.setBackgroundColor(MAP_OF_COLOR.get(source.getCategory()));
            binding.executePendingBindings();
        }
    }
}
