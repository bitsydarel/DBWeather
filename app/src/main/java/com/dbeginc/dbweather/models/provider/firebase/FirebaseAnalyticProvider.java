package com.dbeginc.dbweather.models.provider.firebase;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Bitsy Darel on 05.05.17.
 * Firebase Analytic Provider
 */

public class FirebaseAnalyticProvider implements IAnalyticProvider {
    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticProvider(@NonNull final Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setMinimumSessionDuration(5000);
        firebaseAnalytics.setSessionTimeoutDuration(500);
        firebaseAnalytics.setUserProperty("Device", Build.MODEL);
    }

    @Override
    public void logEvent(@NonNull final String eventType, @NonNull final Bundle messageData) {
        firebaseAnalytics.logEvent(eventType, messageData);
    }
}
