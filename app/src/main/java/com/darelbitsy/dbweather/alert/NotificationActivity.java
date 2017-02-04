package com.darelbitsy.dbweather.alert;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.weather.Hour;

import static com.darelbitsy.dbweather.receiver.AlarmWeatherReceiver.NOTIF_HOUR;

/**
 * Created by Darel Bitsy on 01/02/17.
 */
public class NotificationActivity extends Activity {
    private AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Dialog_theme);
        Hour hour = getIntent().getParcelableExtra(NOTIF_HOUR);
        mDialog = new NotificationHelper(this, hour).setDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDialog.show();
    }
}
