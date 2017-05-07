package com.darelbitsy.dbweather.ui.main.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 05/01/17.
 */
public class NetworkAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(R.string.error_title)
                .setMessage(R.string.network_unavailable_message)
                .setPositiveButton(android.R.string.ok, null);

        return alertBuilder.create();
    }
}
