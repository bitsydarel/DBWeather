package com.dbeginc.dbweather.ui.introduction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ActivityIntroductionBinding;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.animation.ForegroundToBackgroundTransformer;
import com.dbeginc.dbweather.ui.introduction.viewpager.IntroPagerAdapter;
import com.dbeginc.dbweather.ui.main.DBWeatherActivity;
import com.dbeginc.dbweather.utils.services.LocationTracker;

import static com.dbeginc.dbweather.ui.introduction.IntroPresenter.ACCOUNT_PAGE;
import static com.dbeginc.dbweather.ui.introduction.IntroPresenter.LAST_PAGE_INDEX;
import static com.dbeginc.dbweather.ui.introduction.IntroPresenter.LOCATION_PAGE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.UPDATE_REQUEST;

public class IntroductionActivity extends BaseActivity implements IntroductionView, ViewPager.OnPageChangeListener {

    private ActivityIntroductionBinding mLayoutBinding;
    private IntroPresenter presenter;
    private String currentPermissionName;
    private BroadcastReceiver mLocationBroadcast;
    private Intent mActivityData;
    private final PagePresenter colorManager = new PagePresenter();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_introduction);
        presenter = new IntroPresenter(this, mAppDataProvider);

        mLayoutBinding.vpgIntroId
                .setPagingEnabled(presenter.isPagingEnable());
        mLayoutBinding.vpgIntroId
                .setAdapter(new IntroPagerAdapter(getSupportFragmentManager(), colorManager));
        mLayoutBinding.vpgIntroId
                .setPageTransformer(false, new ForegroundToBackgroundTransformer());

        mLayoutBinding.pageIndicator
                .setViewPager(mLayoutBinding.vpgIntroId);
        mLayoutBinding.vpgIntroId
                .addOnPageChangeListener(this);

        mLayoutBinding.btnNext
                .setOnClickListener(view -> goToNextPage());
        mLayoutBinding.btnPrecedent
                .setOnClickListener(view -> goToPrecedentPage());
        mLayoutBinding.getRoot()
                .setBackgroundColor(colorManager.getBackgroundColor(0));

        mActivityData = new Intent(getApplicationContext(), DBWeatherActivity.class);

        mLocationBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) { receiveBroadcast(intent.getAction()); }
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { startService(new Intent(getApplicationContext(), LocationTracker.class)); }

        presenter.getNews();
        presenter.initiateLiveSourcesTable();
    }

    /**
     * Called when the current {@link Window} of the activity gains or loses
     * focus.  This is the best indicator of whether this activity is visible
     * to the user.  The default implementation clears the key tracking
     * state, so should always be called.
     * <p>
     * <p>Note that this provides information about global focus state, which
     * is managed independently of activity lifecycles.  As such, while focus
     * changes will generally have some relation to lifecycle changes (an
     * activity that is stopped will not generally get window focus), you
     * should not rely on any particular order between the callbacks here and
     * those in the other lifecycle methods such as {@link #onResume}.
     * <p>
     * <p>As a general rule, however, a resumed activity will have window
     * focus...  unless it has displayed other dialogs or popups that take
     * input focus, in which case the activity itself will not have focus
     * when the other windows have it.  Likewise, the system may display
     * system-level windows (such as the status bar notification panel or
     * a system alert) which will temporarily take window input focus without
     * pausing the foreground activity.
     *
     * @param hasFocus Whether the window of this activity has focus.
     * @see #hasWindowFocus()
     * @see #onResume
     * @see View#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    private void receiveBroadcast(@NonNull final String action) {
        if (action.equalsIgnoreCase(LOCATION_UPDATE)) { presenter.getWeather(); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE);
        intentFilter.addAction(UPDATE_REQUEST);
        registerReceiver(mLocationBroadcast, intentFilter);
        hideBackButtonOnFirstPage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationBroadcast != null) { unregisterReceiver(mLocationBroadcast); }
    }

    @Override
    public void goToNextPage() {
        if (presenter.handleLastPage()) { closeView(); }
        else if (presenter.canMoveToNext(mLayoutBinding.vpgIntroId.getCurrentItem())) {
            mLayoutBinding.vpgIntroId
                    .setCurrentItem(mLayoutBinding.vpgIntroId.getCurrentItem() + 1, true);
        }
    }

    @Override
    public void goToPrecedentPage() {
        mLayoutBinding.vpgIntroId
                .setCurrentItem(mLayoutBinding.vpgIntroId.getCurrentItem() - 1, true);
    }

    @Override
    public void askLocationPermission() {
        super.askLocationPermIfNeeded();
    }

    @Override
    public void askAccountPermission() {
        super.askAccountInfoPermIfNeeded();
    }

    @Override
    public Pair<String, Boolean> shouldAllowSwipingForward(final int position) { return presenter.shouldAllowPaging(position); }

    @Override
    public void retryPermissionRequest(@NonNull final String permissionName) {
        mLayoutBinding.vpgIntroId
                .setPagingEnabled(false);

        final Snackbar reAsk = Snackbar.make(mLayoutBinding.vpgIntroId, R.string.request_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.ask_again, view -> presenter.askPermission(permissionName));

        reAsk.setActionTextColor(Color.GREEN);
        reAsk.show();
    }

    @Override
    public void closeView() {
        startActivity(getViewData());
        finish();
    }

    @Override
    public Intent getViewData() { return mActivityData; }

    @Override
    public Context getContext() { return getApplicationContext(); }

    @Override
    public void showWeatherErrorMessage() {

    }

    @Override
    public void showNewsErrorMessage() {

    }

    @Override
    public void handleLastPage() {
        hideBackButtonOnFirstPage();

        if (presenter.handleLastPage()) {
            mLayoutBinding.btnNext.setImageResource(R.drawable.btn_done_icon);
            mLayoutBinding.btnNext.setVisibility(View.VISIBLE);
            mLayoutBinding.btnNext.setEnabled(true);

        } else {
            if (mLayoutBinding.vpgIntroId.getCurrentItem() == LAST_PAGE_INDEX) {
                mLayoutBinding.btnNext.setVisibility(View.INVISIBLE);
                mLayoutBinding.btnNext.setEnabled(false);

            } else {
                if (mLayoutBinding.btnNext.getVisibility() == View.INVISIBLE) {
                    mLayoutBinding.btnNext.setImageResource(R.drawable.next_button_icon);
                    mLayoutBinding.btnNext.setVisibility(View.VISIBLE);
                    mLayoutBinding.btnNext.setEnabled(true);
                }
            }
        }
    }

    private void hideBackButtonOnFirstPage() {
        if (mLayoutBinding.vpgIntroId.getCurrentItem() == 0) {
            mLayoutBinding.btnPrecedent.setVisibility(View.INVISIBLE);
            mLayoutBinding.btnPrecedent.setEnabled(false);

        } else {
            mLayoutBinding.btnPrecedent.setVisibility(View.VISIBLE);
            mLayoutBinding.btnPrecedent.setEnabled(true);
        }
    }

    @Override
    public void allowSwiping(final boolean shouldAllow) { mLayoutBinding.vpgIntroId.setPagingEnabled(shouldAllow); }

    /**
     * Empty, because is not needed here
     * Go to super is implementation to read the documentation
     */
    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {}

    @Override
    public void onPageSelected(final int position) {
        final Pair<String, Boolean> allowSwipingForward = shouldAllowSwipingForward(position);

        handleLastPage();
        mLayoutBinding.getRoot()
                .setBackgroundColor(colorManager.getBackgroundColor(position));
        mLayoutBinding.vpgIntroId
                .setPagingEnabled(allowSwipingForward.second);

        currentPermissionName = allowSwipingForward.first;
    }

    /**
     * Empty, because is not needed here
     * Go to super is implementation to read the documentation
     */
    @Override
    public void onPageScrollStateChanged(final int state) {}

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) && !currentPermissionName.equalsIgnoreCase(ACCOUNT_PAGE)) {

            presenter.shouldRetryPermissionRequest(currentPermissionName);
        } else {

            if (LOCATION_PAGE.equalsIgnoreCase(currentPermissionName)) {
                startService(new Intent(getApplicationContext(), LocationTracker.class));
            }

            presenter.setPagingStatus(true);
            presenter.afterPermissionGranted(mLayoutBinding.vpgIntroId.getCurrentItem());
        }

    }
}
