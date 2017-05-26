package com.dbeginc.dbweather.ui.addlocation;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ActivityAddLocationBinding;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.geoname.GeoNameLocationInfoProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.ui.addlocation.adapter.LocationListAdapter;
import com.dbeginc.dbweather.ui.main.WeatherActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

public class AddLocationActivity extends AppCompatActivity implements IAddLocationView {

    private LocationListAdapter mLocationListAdapter;
    private final RxSchedulersProvider rxSchedulersProvider = RxSchedulersProvider.getInstance();

    @Inject
    List<GeoName> mListOfLocation;
    @Inject
    GeoNameLocationInfoProvider mLocationInfoProvider;
    @Inject
    AppDataProvider mAppDataProvider;

    private ActivityAddLocationBinding mAddLocationBinding;
    private Intent mIntent;
    private AddLocationPresenter presenter;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private void getUserQuery(final Intent intent) {
        if (intent != null &&
                Intent.ACTION_SEARCH.equals(intent.getAction())) {

            getUserQuery(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    private void getUserQuery(final String query) {
        if (!query.isEmpty()) { presenter.getLocations(query); }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWeatherApplication.getComponent()
                .inject(this);
        mAddLocationBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_location);
        presenter = new AddLocationPresenter(mAppDataProvider, mLocationInfoProvider, mCompositeDisposable, this);

        setSupportActionBar(mAddLocationBinding.addLocationToolbar.addLocationToolbarId);

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mAddLocationBinding.searchLocationView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mAddLocationBinding.searchLocationView.setIconifiedByDefault(false);
        mAddLocationBinding.searchLocationView.setSubmitButtonEnabled(true);
        mAddLocationBinding.searchLocationView.setOnSuggestionListener(new SuggestionListener());

        mAddLocationBinding.locationRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));

        getUserQuery(getIntent());
        mIntent = new Intent(getApplicationContext(), WeatherActivity.class);
        presenter.getWeather();
        presenter.getNews();
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAddLocationBinding.addLocationToolbar.backToMainActivity.setOnClickListener(v -> closeView());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.clearData();
    }

    /**
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent received by the edit text
     */
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        getUserQuery(intent);
    }

    @Override
    public void saveWeatherForHomeButton(@Nonnull final WeatherData weatherData) { mIntent.putExtra(WEATHER_INFO_KEY, weatherData); }

    @Override
    public void saveNewsForHomeButton(@Nonnull final List<Article> articles) {
        mIntent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
    }

    @Override
    public void closeView() {
        startActivity(mIntent);
        finish();
    }

    @Override
    public Context getContext() { return getApplicationContext(); }

    @Override
    public void onLocationAdded() {
        Snackbar.make(mAddLocationBinding.getRoot(), getString(R.string.successfully_added_city), Snackbar.LENGTH_LONG).show();
        mAddLocationBinding.addLocationToolbar.backToMainActivity.callOnClick();
    }

    @Override
    public void onLocationNotAdded() {
        Snackbar.make(mAddLocationBinding.getRoot(), getString(R.string.unsuccessfully_added_city), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void displayFoundedLocation(final List<GeoName> locationList) {
        if (mLocationListAdapter != null) { mLocationListAdapter.updateLocationList(locationList); }
        else {
            mLocationListAdapter = new LocationListAdapter(locationList, mCompositeDisposable, rxSchedulersProvider);
            mAddLocationBinding.locationRecyclerView.setAdapter(mLocationListAdapter);
        }
    }

    public class SuggestionListener implements SearchView.OnSuggestionListener {
        @Override
        public boolean onSuggestionSelect(final int position) {
            return true;
        }

        @Override
        public boolean onSuggestionClick(final int position) {
            final GeoName location = mListOfLocation.get(position);

            final DialogInterface.OnClickListener mCancelLocationClick =
                    (dialog, which) -> dialog.cancel();

            final DialogInterface.OnClickListener mAddLocationClick = (dialog, which) -> presenter.addLocationToDatabase(location);

            new AlertDialog.Builder(AddLocationActivity.this)
                    .setMessage(String.format(Locale.getDefault(),
                            AddLocationActivity.this
                                    .getApplicationContext()
                                    .getString(R.string.alert_add_location_text),
                            location.getName()))
                    .setNegativeButton(android.R.string.cancel, mCancelLocationClick)
                    .setPositiveButton(android.R.string.yes, mAddLocationClick)
                    .create()
                    .show();
            return true;
        }
    }
}
