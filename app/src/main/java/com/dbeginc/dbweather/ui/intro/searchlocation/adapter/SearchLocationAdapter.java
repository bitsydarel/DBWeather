package com.dbeginc.dbweather.ui.intro.searchlocation.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.SearchLocationItemBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 15.06.17.
 *
 * Search Location adapter
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.LocationViewHolder> {
    private final List<GeoName> locationList = new ArrayList<>();
    private final PublishSubject<GeoName> loadLocationEvent;

    public SearchLocationAdapter(@NonNull final List<GeoName> locationList, @NonNull final PublishSubject<GeoName> loadLocationEvent) {
        this.locationList.addAll(locationList);
        this.loadLocationEvent = loadLocationEvent;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new LocationViewHolder(DataBindingUtil.inflate(inflater, R.layout.search_location_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder locationViewHolder, final int position) {
        locationViewHolder.bindLocation(locationList.get(position));
    }

    @Override
    public int getItemCount() { return locationList.size(); }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final SearchLocationItemBinding binding;

        LocationViewHolder(final SearchLocationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> loadLocationEvent.onNext(binding.getLocation()));
        }

        void bindLocation(@NonNull final GeoName location) {
            binding.setLocation(location);
            binding.executePendingBindings();
        }
    }
}
