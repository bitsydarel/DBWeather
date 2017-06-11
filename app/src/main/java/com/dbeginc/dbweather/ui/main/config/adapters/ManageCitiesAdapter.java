package com.dbeginc.dbweather.ui.main.config.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.LocationItemsBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 11.06.17.
 */

public class ManageCitiesAdapter extends RecyclerView.Adapter<ManageCitiesAdapter.LocationViewHolder> {
    private final List<GeoName> locationList = new ArrayList<>();
    private final PublishSubject<GeoName> removeLocationEvent;

    public ManageCitiesAdapter(@NonNull final List<GeoName> locationList, @NonNull final PublishSubject<GeoName> removeLocationEvent) {
        this.locationList.addAll(locationList);
        this.removeLocationEvent = removeLocationEvent;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LocationViewHolder(DataBindingUtil.inflate(inflater, R.layout.location_items, parent, false));
    }

    @Override
    public void onBindViewHolder(LocationViewHolder locationViewHolder, int position) {
        locationViewHolder.bindLocation(locationList.get(position));
    }

    @Override
    public int getItemCount() { return locationList.size(); }

    public void removeItemAtPosition(final int position) {
        removeLocationEvent.onNext(locationList.get(position));
        locationList.remove(position);
        notifyItemRemoved(position);
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final LocationItemsBinding binding;

        LocationViewHolder(final LocationItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindLocation(@NonNull final GeoName location) {
            binding.setLocation(location);
        }
    }
}
