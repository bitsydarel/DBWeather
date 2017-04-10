package com.darelbitsy.dbweather.helper.utility;

import android.app.Activity;

import com.darelbitsy.dbweather.ui.alert.AlertDialogFragment;
import com.darelbitsy.dbweather.ui.alert.NetworkAlertDialogFragment;

/**
 * Created by Darel Bitsy on 20/02/17.
 */

public class ErrorDialogUtil {

    private ErrorDialogUtil(){}

    public static void alertUserAboutNetworkError(final Activity activity) {
        final NetworkAlertDialogFragment dialog = new NetworkAlertDialogFragment();
        dialog.show(activity.getFragmentManager(), "network_error_dialog");
    }

    public static void alertUserAboutError(final Activity activity) {
        final AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(activity.getFragmentManager(), "error_dialog");
    }
}
