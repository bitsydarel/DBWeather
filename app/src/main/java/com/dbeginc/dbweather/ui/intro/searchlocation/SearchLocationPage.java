package com.dbeginc.dbweather.ui.intro.searchlocation;

import android.app.SearchManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.SearchLocationIntroLayoutBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.intro.searchlocation.adapter.SearchLocationAdapter;

import java.util.List;

import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;

/**
 * Created by darel on 14.06.17.
 *
 * Search Location
 */

public class SearchLocationPage extends BaseFragment implements SearchLocationView {

    private SearchLocationIntroLayoutBinding binding;
    private SearchLocationPresenter presenter;
    private final PublishSubject<GeoName> loadLocationEvent = PublishSubject.create();
    private final Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SearchLocationPresenter(this, mAppDataProvider, loadLocationEvent, voiceQuery);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_location_intro_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupLocationLookupFeature();
        presenter.subscribeToLocationClickEvent();
        presenter.subscribeToVoiceQuery();
        handler.post(() -> mAppDataProvider.setGpsPermissionStatus(false));
    }

    @Override
    public void onDestroyView() {
        presenter.clearState();
        super.onDestroyView();
    }

    @Override
    public void loadLocation(@NonNull final List<GeoName> locations) {
        final SearchLocationAdapter adapter = new SearchLocationAdapter(locations, loadLocationEvent);
        binding.locationList.setAdapter(adapter);
        binding.locationList.setLayoutManager(new LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false));
        binding.locationList.setHasFixedSize(true);
        binding.locationAnimation.setVisibility(View.GONE);
        binding.locationList.setVisibility(View.VISIBLE);
    }

    @Override
    public void setupLocationLookupFeature() {
        final SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        binding.searchLocationView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        binding.searchLocationView.setIconifiedByDefault(false);
        binding.searchLocationView.setSubmitButtonEnabled(true);
    }

    @Override
    public void onLocationSelected() {
        locationUpdateEvent.onNext(LOCATION_UPDATE);
    }

    @Override
    public void setQuery(@NonNull final String query) {
        binding.searchLocationView.setQuery(query, false);
    }
}
