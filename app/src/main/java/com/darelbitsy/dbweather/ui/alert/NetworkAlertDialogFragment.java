package com.darelbitsy.dbweather.ui.alert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class NetworkAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(R.string.error_title)
                .setMessage(R.string.network_unavailable_message)
                .setPositiveButton(R.string.error_positive_button_text, null);

        return alertBuilder.create();
    }
}
