package com.darelbitsy.dbweather.ui.main.adapters;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.HourlyListItemBinding;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.HOURLY_DATA_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.INDEX;

/**
 * Created by Darel Bitsy on 12/01/17.
 * Hourly Weather Data RecyclerView Adapter
 */

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {
    private List<HourlyData> mHours;
    private CompositeDisposable compositeDisposable;
    private final RxSchedulersProvider rxSchedulersProvider = RxSchedulersProvider.newInstance();

    public HourAdapter(final List<HourlyData> hours, final CompositeDisposable compositeDisposable) {
        mHours = new ArrayList<>(hours);
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public HourViewHolder onCreateViewHolder(final ViewGroup parent,
                                             final int viewType) {

        return new HourViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.hourly_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final HourViewHolder holder, final int position) { holder.bindHour(mHours.get(position)); }

    @Override
    public void onBindViewHolder(final HourViewHolder holder, final int position, final List<Object> payloads) {
        for (final Object object : payloads) {
            final Bundle bundle = (Bundle) object;
            mHours.add(bundle.getInt(INDEX), bundle.getParcelable(HOURLY_DATA_KEY));
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return (mHours != null && mHours.size() > 9) ? mHours.size() : 0;
    }

    public void updateData(final List<HourlyData> data) {
        compositeDisposable.add(Single.fromCallable(() -> DiffUtil.calculateDiff(new HourDiffCallback(mHours, data)))
                .subscribeOn(rxSchedulersProvider.getComputationThread())
                .observeOn(rxSchedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<DiffUtil.DiffResult>() {
                    @Override
                    public void onSuccess(final DiffUtil.DiffResult diffResult) {
                        diffResult.dispatchUpdatesTo(HourAdapter.this);
                    }
                    @Override
                    public void onError(final Throwable throwable) {}
                }));
    }

    class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HourlyListItemBinding itemBinding;

        HourViewHolder(final HourlyListItemBinding hourlyListItemBinding) {
            super(hourlyListItemBinding.getRoot());
            itemBinding = hourlyListItemBinding;
            itemBinding.hourlyLayout.setOnClickListener(this);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                itemBinding.hourlyPrecipProgressBar.getProgressDrawable()
                        .setColorFilter(itemBinding.hourlyPrecipProgressBar.getResources().getColor(R.color.colorPrimaryDark),
                        PorterDuff.Mode.MULTIPLY);

                itemBinding.hourlyHumidityProgressBar.getProgressDrawable()
                        .setColorFilter(itemBinding.hourlyHumidityProgressBar.getResources().getColor(R.color.colorPrimaryDark),
                        android.graphics.PorterDuff.Mode.MULTIPLY);

                itemBinding.hourlyWindSpeedProgressBar.getProgressDrawable()
                        .setColorFilter(itemBinding.hourlyWindSpeedProgressBar.getResources().getColor(R.color.colorPrimaryDark),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            itemBinding.hourlyInfoDetails.setVisibility(View.GONE);
        }

        void bindHour(final HourlyData hour) { itemBinding.setHourlyData(hour); }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(final View v) {
            if (itemBinding.hourlyInfoDetails.getVisibility() == View.GONE) {
                itemBinding.hourlyInfoDetails.setVisibility(View.VISIBLE);
                itemBinding.hourlyBorderLine.setVisibility(View.GONE);
                v.setBackgroundColor(Color.WHITE);

            } else {
                itemBinding.hourlyInfoDetails.setVisibility(View.GONE);
                v.setBackgroundColor(Color.parseColor("#DCDCDC"));
                itemBinding.hourlyBorderLine.setVisibility(View.VISIBLE);
            }
        }
    }
}
