package com.dbeginc.dbweather.ui.introduction;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

/**
 * Created by darel on 01.05.17.
 * Interface representing
 * the Introduction View
 * and his available methods
 */

public interface IntroductionView {

    void goToNextPage();

    void goToPrecedentPage();

    void askLocationPermission();

    void askAccountPermission();

    Pair<String, Boolean> shouldAllowSwipingForward(final int position);

    void retryPermissionRequest(@NonNull final String permissionName);

    void closeView();

    Intent getViewData();

    Context getContext();

    void showWeatherErrorMessage();

    void showNewsErrorMessage();

    void handleLastPage();

    void allowSwiping(final boolean shouldAllow);
}
