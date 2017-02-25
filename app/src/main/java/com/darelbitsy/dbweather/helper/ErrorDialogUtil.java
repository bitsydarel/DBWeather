package com.darelbitsy.dbweather.helper;

import android.app.Activity;

import com.darelbitsy.dbweather.ui.alert.AlertDialogFragment;
import com.darelbitsy.dbweather.ui.alert.NetworkAlertDialogFragment;

/**
 * Created by Darel Bitsy on 20/02/17.
 */

public class ErrorDialogUtil {
    public static void alertUserAboutNetworkError(Activity activity) {
        NetworkAlertDialogFragment dialog = new NetworkAlertDialogFragment();
        dialog.show(activity.getFragmentManager(), "network_error_dialog");
    }

    public static void alertUserAboutError(Activity activity) {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(activity.getFragmentManager(), "error_dialog");
    }
}
