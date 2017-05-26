package com.dbeginc.dbweather.models.provider.firebase;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Bitsy Darel on 05.05.17.
 */

public interface IAnalyticProvider {
    void logEvent(final @NonNull String eventType, @NonNull final Bundle messageData);
}
