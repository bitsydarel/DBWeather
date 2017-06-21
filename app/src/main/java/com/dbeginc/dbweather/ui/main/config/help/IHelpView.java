package com.dbeginc.dbweather.ui.main.config.help;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

/**
 * Created by darel on 21.06.17.
 */

public interface IHelpView {
    void showError();

    void handleClick(@NonNull final Pair<Integer, Boolean> integerBooleanPair);
}
