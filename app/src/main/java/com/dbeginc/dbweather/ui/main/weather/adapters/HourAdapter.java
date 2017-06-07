package com.dbeginc.dbweather.ui.main.weather.adapters;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.HourlyListItemBinding;
import com.dbeginc.dbweather.models.datatypes.weather.HourlyData;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.INDEX;

/**
 * Created by Darel Bitsy on 12/01/17.
 * Hourly Weather Data RecyclerView Adapter
 */

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {
    private List<HourlyData> mHours;
    private CompositeDisposable compositeDisposable;
    private RecyclerView recyclerView;
    private final RxSchedulersProvider rxSchedulersProvider = RxSchedulersProvider.getInstance();

    public HourAdapter(final List<HourlyData> hours, final CompositeDisposable compositeDisposable) {
        mHours = new ArrayList<>(hours);
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public HourViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        return new HourViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.hourly_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final HourViewHolder holder, final int position) { holder.bindHour(mHours.get(position)); }

    @Override
    public void onBindViewHolder(final HourViewHolder holder, final int position, final List<Object> payloads) {
        if (payloads.isEmpty()) { onBindViewHolder(holder, position); }
        else {
            compositeDisposable.add(
                    Observable.fromArray(payloads.toArray())
                            .subscribeOn(rxSchedulersProvider.getComputationThread())
                            .flatMap(o -> Observable.just(((Bundle)o).getInt(INDEX)))
                            .repeatUntil(() -> !recyclerView.isComputingLayout())
                            .observeOn(rxSchedulersProvider.getUIScheduler())
                            .unsubscribeOn(rxSchedulersProvider.getComputationThread())
                            .subscribe(this::notifyNewData, Crashlytics::logException)
            );
        }
    }

    @Override
    public int getItemCount() {
        return (mHours != null && mHours.size() > 9) ? mHours.size() : 0;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    private void notifyNewData(final int position) {
        compositeDisposable.add(
                Observable.just(position)
                        .retryUntil(() -> !recyclerView.isComputingLayout())
                        .subscribeOn(rxSchedulersProvider.getComputationThread())
                        .observeOn(rxSchedulersProvider.getUIScheduler())
                        .unsubscribeOn(rxSchedulersProvider.getComputationThread())
                        .subscribe(this::notifyItemChanged, Crashlytics::logException)
        );
    }

    public synchronized void updateData(final List<HourlyData> data) {
        compositeDisposable.add(
                Single.fromCallable(() -> DiffUtil.calculateDiff(new HourDiffCallback(mHours, data)))
                        .subscribeOn(rxSchedulersProvider.getComputationThread())
                        .observeOn(rxSchedulersProvider.getUIScheduler())
                        .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                            @Override
                            public void onSuccess(final DiffUtil.DiffResult diffResult) {
                                mHours.clear();
                                mHours.addAll(data);
                                diffResult.dispatchUpdatesTo(HourAdapter.this);
                            }
                            @Override
                            public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                        })
        );
    }

    class HourViewHolder extends RecyclerView.ViewHolder {
        private final HourlyListItemBinding itemBinding;

        HourViewHolder(final HourlyListItemBinding hourlyListItemBinding) {
            super(hourlyListItemBinding.getRoot());
            itemBinding = hourlyListItemBinding;
        }

        void bindHour(final HourlyData hour) {
            itemBinding.setHourlyData(hour);
            itemBinding.executePendingBindings();
        }
    }
}
