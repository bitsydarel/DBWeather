package com.dbeginc.dbweather.ui.config.adapter;

import android.databinding.DataBindingUtil;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsListConfigurationItemBinding;

import java.util.Map;

import javax.annotation.Nonnull;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Darel Bitsy on 09/04/17.
 * News Source Configuration recycler View Adapter
 */

public class NewsConfigurationAdapter extends RecyclerView.Adapter<NewsConfigurationAdapter.NewsConfigViewHolder> {
    private final Map<String, Pair<Integer, Integer>> newsSources;
    private final String[] sourceNames;
    private final PublishSubject<Pair<String, Pair<Integer, Integer>>> eventPublisher;

    public NewsConfigurationAdapter(final Map<String, Pair<Integer, Integer>> newsSources,
                                    final PublishSubject<Pair<String, Pair<Integer, Integer>>> eventPublisher) {
        this.newsSources = newsSources;
        sourceNames = newsSources.keySet().toArray(new String[0]);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public NewsConfigViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new NewsConfigViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.news_list_configuration_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final NewsConfigViewHolder holder, final int position) {
        final String sourceName = sourceNames[position];
        holder.bindConfigItem(sourceName,newsSources.get(sourceName));
    }


    @Override
    public int getItemCount() {
        return newsSources.size();
    }

    class NewsConfigViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private NewsListConfigurationItemBinding itemBinding;

        NewsConfigViewHolder(final NewsListConfigurationItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
            this.itemBinding.newsCountPicker.setMinValue(1);
            this.itemBinding.newsCountPicker.setMaxValue(5);
            this.itemBinding.newsCountPicker.setEnabled(true);
            this.itemBinding.newsCountPicker.setWrapSelectorWheel(false);
        }

        void bindConfigItem(final String sourceName,
                            final Pair<Integer, Integer> countAndStatus) {

            itemBinding.newsSourceTitle.setText(sourceName);
            itemBinding.newsCountPicker.setValue(countAndStatus.first);
            itemBinding.newsSourceSwitch.setChecked(countAndStatus.second != 0);
            itemBinding.newsSourceSwitch.setOnCheckedChangeListener(this);
            itemBinding.executePendingBindings();
        }

        @Override
        public void onCheckedChanged(@Nonnull final CompoundButton buttonView, final boolean isChecked) {
            if (isChecked) {
                eventPublisher.onNext(new Pair<>(itemBinding.newsSourceTitle.getText().toString(),
                        new Pair<>(itemBinding.newsCountPicker.getValue(), 1)));

            } else {
                eventPublisher.onNext(new Pair<>(itemBinding.newsSourceTitle.getText().toString(),
                        new Pair<>(itemBinding.newsCountPicker.getValue(), 0)));
            }
        }
    }
}
