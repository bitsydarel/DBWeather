package com.darelbitsy.dbweather.ui.alert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 05/01/17.
 * News notification Dialog
 */

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setPositiveButton(android.R.string.ok, null);

        return builder.create();
    }


}
