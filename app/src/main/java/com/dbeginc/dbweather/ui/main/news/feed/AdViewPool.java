package com.dbeginc.dbweather.ui.main.news.feed;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darel on 20.06.17.
 * Ad View Pool
 */

public class AdViewPool {
    @LayoutRes
    private final int adViewLayout;
    private final LayoutInflater inflater;
    private ViewGroup parent;
    private final List<View> adViewList = new ArrayList<>();

    public AdViewPool(@NonNull final LayoutInflater inflater, @LayoutRes final int layout, @Nullable final ViewGroup parent) {
        this.inflater = inflater;
        adViewLayout = layout;
        this.parent = parent;
        addToPool();
    }


    private void addToPool() {
        final View adLayout = inflater.inflate(adViewLayout, parent, false);
        addBackToPool(adLayout);
    }

    public void addBackToPool(View adLayout) {
        adViewList.add(adLayout);
        // load the ad so that it will be ready when it is retrieved
        loadAd(adLayout);
    }

    private void loadAd(@NonNull final View view) {
        final NativeExpressAdView nativeAd = (NativeExpressAdView) view;
        nativeAd.loadAd(createAdRequest());
    }

    /**
     * @return An adview from the pool.
     */
    public View getAdView() {
        final int index = adViewList.size() - 1;
        final View view = adViewList.get(index);

        adViewList.remove(index);

        if (adViewList.isEmpty()) { addToPool(); }

        return view;
    }

    public boolean isAdViewPoolEmpty() {
        return adViewList.isEmpty();
    }

    private AdRequest createAdRequest() {
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .addTestDevice("C20BB1C5369BFDFD4992ED89CD62F271")
                .build();
    }
}
