package com.dbeginc.dbweather.ui.main.config.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ManageCitiesLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.config.adapters.ManageCitiesAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_KEY;

/**
 * Created by darel on 10.06.17.
 * Manage user cities
 */

public class ManageCitiesFragment extends BaseFragment implements ILocationManagerView {

    private ManageCitiesPresenter presenter;
    private ManageCitiesLayoutBinding binding;
    private final Handler handler = new Handler();
    private ManageCitiesAdapter manageCitiesAdapter;
    private final PublishSubject<GeoName> removeLocationEvent = PublishSubject.create();
    private ItemTouchHelper itemTouchHelper;

    public static synchronized ManageCitiesFragment newInstance(@NonNull final List<GeoName> locationList) {
        final ManageCitiesFragment fragment = new ManageCitiesFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LOCATION_KEY, (ArrayList<? extends Parcelable>) locationList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ManageCitiesPresenter(mAppDataProvider, removeLocationEvent);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            manageCitiesAdapter = new ManageCitiesAdapter(arguments.getParcelableArrayList(LOCATION_KEY), removeLocationEvent);
        }
        itemTouchHelper = new ItemTouchHelper(new SwipeToRemove(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.manage_cities_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.subscribeToRemoveLocationEvent();
        handler.post(this::setupLayout);
    }

    @Override
    public void onDestroyView() {
        presenter.clearState();
        super.onDestroyView();
    }

    private void setupLayout() {
        binding.manageLocations.setAdapter(manageCitiesAdapter);
        binding.manageLocations.setLayoutManager(new LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false));
        binding.manageLocations.setHasFixedSize(true);
        itemTouchHelper.attachToRecyclerView(binding.manageLocations);
    }

    @Override
    public void removeLocation(@NonNull final GeoName location) {
        presenter.removeLocation(location);
    }

    private class SwipeToRemove extends ItemTouchHelper.SimpleCallback {

        public SwipeToRemove(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            manageCitiesAdapter.removeItemAtPosition(viewHolder.getAdapterPosition());
        }
    }
}
