package com.dbeginc.dbweather.ui.main.config.fragments.newssource;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.news.Sources;

/**
 * Created by darel on 12.06.17.
 * News Sources View
 */

public interface NewsSourceView {
    void loadSources(@NonNull final Sources sources);

    void notifyNewsSuccessfullySaved();

    void notifyErrorWhileSaved();
}
