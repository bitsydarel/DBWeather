package com.darelbitsy.dbweather.ui.addlocation;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.ActivityAddLocationBinding;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.geoname.GeoNameLocationInfoProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.ui.addlocation.adapter.LocationListAdapter;
import com.darelbitsy.dbweather.ui.main.WeatherActivity;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class AddLocationActivity extends AppCompatActivity {

    private LocationListAdapter mLocationListAdapter;
    private DatabaseOperation mDatabaseOperation;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final RxSchedulersProvider rxSchedulersProvider = RxSchedulersProvider.newInstance();

    @Inject
    List<GeoName> mListOfLocation;
    @Inject
    GeoNameLocationInfoProvider mLocationInfoProvider;
    @Inject
    AppDataProvider mAppDataProvider;

    private ActivityAddLocationBinding mAddLocationBinding;

    private void getUserQuery(final Intent intent) {
        if (intent != null &&
                Intent.ACTION_SEARCH.equals(intent.getAction())) {

            getUserQuery(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    private void getUserQuery(final String query) {
        if (!query.isEmpty()) {
            mCompositeDisposable.add(mLocationInfoProvider
                    .getLocation(query)
                    .subscribeOn(rxSchedulersProvider.getWeatherScheduler())
                    .observeOn(rxSchedulersProvider.getUIScheduler())
                    .subscribeWith(new GetLocationHelper()));
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBWeatherApplication.getComponent()
                .inject(this);
        mAddLocationBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_location);

        setSupportActionBar(mAddLocationBinding.addLocationToolbar.addLocationToolbarId);
        mDatabaseOperation = DatabaseOperation.getInstance(this);

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
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAddLocationBinding.addLocationToolbar.backToMainActivity.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), WeatherActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
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

            final DialogInterface.OnClickListener mAddLocationClick = (dialog, which) -> {
                mDatabaseOperation.addLocationToDatabase(location)
                        .subscribeOn(rxSchedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(rxSchedulersProvider.getUIScheduler())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Snackbar.make(mAddLocationBinding.getRoot(), getString(R.string.successfully_added_city), Snackbar.LENGTH_LONG);
                            }

                            @Override
                            public void onError(final Throwable throwable) {
                                Snackbar.make(mAddLocationBinding.getRoot(), getString(R.string.unsuccessfully_added_city), Snackbar.LENGTH_LONG);
                            }
                        });
                mAddLocationBinding.addLocationToolbar.backToMainActivity.callOnClick();
            };

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

    private class GetLocationHelper extends DisposableSingleObserver<List<GeoName>> {
        /**
         * Notifies the SingleObserver with a single item and that the Single has finished sending
         * push-based notifications.
         * <p>
         * The  Single will not call this method if it calls {@link #onError}.
         *
         * @param listOfLocations the item emitted by the Single, an list of locations
         */
        @Override
        public void onSuccess(final List<GeoName> listOfLocations) {
            if (mLocationListAdapter != null) { mLocationListAdapter.updateLocationList(listOfLocations); }
            else {
                mLocationListAdapter = new LocationListAdapter(listOfLocations, mCompositeDisposable, rxSchedulersProvider);
                mAddLocationBinding.locationRecyclerView.setAdapter(mLocationListAdapter);
            }
        }

        /**
         * Notifies the SingleObserver that the Single has experienced an error condition.
         * <p>
         * If the Single calls this method, it will not thereafter call #onSuccess.
         *
         * @param e the exception encountered by the Single
         */
        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in AddLocationActivity: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }
}
