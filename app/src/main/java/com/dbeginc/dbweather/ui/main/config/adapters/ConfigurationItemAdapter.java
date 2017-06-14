package com.dbeginc.dbweather.ui.main.config.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ConfigurationItemBinding;
import com.dbeginc.dbweather.ui.main.config.ConfigurationItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 11.06.17.
 */

public class ConfigurationItemAdapter extends RecyclerView.Adapter<ConfigurationItemAdapter.ConfigItemViewHolder> {

    private final List<ConfigurationItem> configurationItemList = new ArrayList<>();
    private final PublishSubject<Pair<Integer, Boolean>> configClickEvent;

    public ConfigurationItemAdapter(List<ConfigurationItem> configurationItemList, @NonNull final PublishSubject<Pair<Integer, Boolean>> configClickEvent) {
        this.configurationItemList.addAll(configurationItemList);
        this.configClickEvent = configClickEvent;
    }

    @Override
    public ConfigItemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ConfigItemViewHolder(DataBindingUtil.inflate(inflater, R.layout.configuration_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ConfigItemViewHolder configItemViewHolder, int position) {
        configItemViewHolder.bindConfigurationItem(configurationItemList.get(position));
    }

    @Override
    public int getItemCount() { return configurationItemList.size(); }

    class ConfigItemViewHolder extends RecyclerView.ViewHolder {
        private final ConfigurationItemBinding binding;

        ConfigItemViewHolder(ConfigurationItemBinding configurationItem) {
            super(configurationItem.getRoot());
            binding = configurationItem;
            binding.getRoot().setOnClickListener(v -> configClickEvent.onNext(new Pair<>(binding.getConfiguration().id.get(), false)));
            binding.configurationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> configClickEvent.onNext(new Pair<>(binding.getConfiguration().id.get(), isChecked)));
        }

        void bindConfigurationItem(@NonNull final ConfigurationItem configurationItem) {
            binding.setConfiguration(configurationItem);
            binding.configurationSwitch.setChecked(configurationItem.isChecked.get());
            binding.executePendingBindings();
        }
    }
}
