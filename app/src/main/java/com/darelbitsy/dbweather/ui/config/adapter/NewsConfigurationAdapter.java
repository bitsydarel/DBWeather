package com.darelbitsy.dbweather.ui.config.adapter;

import android.support.design.widget.Snackbar;
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
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;

import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;

/**
 * Created by Darel Bitsy on 09/04/17.
 */

public class NewsConfigurationAdapter extends RecyclerView.Adapter<NewsConfigurationAdapter.NewsConfigViewHolder> {
    private final Map<String, Pair<Integer, Integer>> newsSources;
    private final DatabaseOperation mDatabaseOperation;
    private final String[] sourceNames;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.newInstance();
    private final CompositeDisposable rxSubscription = new CompositeDisposable();
    private View viewGroup;

    public NewsConfigurationAdapter(final Map<String, Pair<Integer, Integer>> newsSources,
                                    final DatabaseOperation databaseOperation,
                                    final View viewGroup) {
        this.newsSources = newsSources;
        mDatabaseOperation = databaseOperation;
        sourceNames = newsSources.keySet().toArray(new String[0]);
        this.viewGroup = viewGroup;
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
        final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    rxSubscription.add(mDatabaseOperation.saveNewsSourceConfiguration(newsSourceTitle.getText().toString(),
                            newsCount.getValue(), 1)
                            .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                            .observeOn(schedulersProvider.getUIScheduler())
                            .subscribeWith(new SwitchObserver()));

                } else {
                    rxSubscription.add(mDatabaseOperation.saveNewsSourceConfiguration(newsSourceTitle.getText().toString(),
                            newsCount.getValue(), 0)
                            .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                            .observeOn(schedulersProvider.getUIScheduler())
                            .subscribeWith(new SwitchObserver()));
                }
            }
        };

        NewsConfigViewHolder(final View itemView) {
            super(itemView);
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

    private class SwitchObserver extends DisposableCompletableObserver {

        @Override
        public void onComplete() {
            Snackbar.make(viewGroup,
                    viewGroup.getContext().getString(R.string.successfully_saved_configuration), Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onError(final Throwable throwable) {
            Snackbar.make(viewGroup,
                    viewGroup.getContext().getString(R.string.unsuccessfully_saved_configuration), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public CompositeDisposable getRxSubscription() {
        return rxSubscription;
    }
}
