package com.darelbitsy.dbweather.alert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.services.KillCheckerService;
import com.darelbitsy.dbweather.ui.MainActivity;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Locale;

import static com.darelbitsy.dbweather.receiver.AlarmWeatherReceiver.NOTIF_HOUR;

/**
 * Created by Darel Bitsy on 01/02/17.
 */
public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_advice);
        Hour hour = getIntent().getParcelableExtra(NOTIF_HOUR);

        startService(new Intent(getApplication(), KillCheckerService.class));
        ImageView iconWeather = (ImageView) findViewById(R.id.iconWeather);
        ImageButton closeButton = (ImageButton) findViewById(R.id.close_button);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        TextView descriptionLabel = (TextView) findViewById(R.id.description_label);
        TextView summaryWeather = (TextView) findViewById(R.id.summaryWeather);
        iconWeather.setImageResource(hour.getIconId());
        NotificationHelper notificationHelper = new NotificationHelper(this, hour);
        titleText.setText(notificationHelper.getTitleFromIcon());

        closeButton.setOnClickListener(view -> {
            new Handler().post(new AlarmConfigHelper(getApplicationContext())::cancelClothingNotificationAlarm);
            finish();
        });
        descriptionLabel.setText(notificationHelper.getDescription());
        summaryWeather.setText(hour.getSummary());
    }
}
