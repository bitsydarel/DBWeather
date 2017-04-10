package com.darelbitsy.dbweather.adapters.listAdapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;

import java.util.Map;

/**
 * Created by Darel Bitsy on 09/04/17.
 */

public class NewsConfigurationAdapter extends RecyclerView.Adapter<NewsConfigurationAdapter.NewsConfigViewHolder> {
    private final Map<String, Pair<Integer, Integer>> newsSources;
    private final DatabaseOperation mDatabaseOperation;
    private final String[] sourceNames;

    public NewsConfigurationAdapter(final Map<String, Pair<Integer, Integer>> newsSources,
                                    final DatabaseOperation databaseOperation) {
        this.newsSources = newsSources;
        mDatabaseOperation = databaseOperation;
        sourceNames = newsSources.keySet().toArray(new String[0]);
    }

    @Override
    public NewsConfigViewHolder onCreateViewHolder(final ViewGroup parent,
                                                   final int viewType) {
        return new NewsConfigViewHolder(LayoutInflater.from(parent
                .getContext())
                .inflate(R.layout.news_list_configuration_item, parent, false));
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

    class NewsConfigViewHolder extends RecyclerView.ViewHolder {
        final TextView newsSourceTitle;
        final NumberPicker newsCount;
        final SwitchCompat onOffSwitch;
        final ConstraintLayout mainLayout;
        final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    mDatabaseOperation.saveNewsSourceConfiguration(newsSourceTitle.getText().toString(),
                            newsCount.getValue(), 1);

                } else {
                    mDatabaseOperation.saveNewsSourceConfiguration(newsSourceTitle.getText().toString(),
                            newsCount.getValue(), 0);
                }
            }
        };

        NewsConfigViewHolder(final View itemView) {
            super(itemView);
            mainLayout = (ConstraintLayout) itemView;
            newsSourceTitle = (TextView) itemView.findViewById(R.id.newsSourceTitle);
            newsCount = (NumberPicker) itemView.findViewById(R.id.news_count_picker);
            onOffSwitch = (SwitchCompat) itemView.findViewById(R.id.news_source_switch);
            newsCount.setMinValue(1);
            newsCount.setMaxValue(9);
            newsCount.setEnabled(true);
            newsCount.setWrapSelectorWheel(false);
        }

        void bindConfigItem(final String sourceName,
                            final Pair<Integer, Integer> countAndStatus) {

            newsSourceTitle.setText(sourceName);
            newsCount.setValue(countAndStatus.first);
            onOffSwitch.setChecked(countAndStatus.second != 0);
            onOffSwitch.setOnCheckedChangeListener(mCheckedChangeListener);
        }
    }
}
