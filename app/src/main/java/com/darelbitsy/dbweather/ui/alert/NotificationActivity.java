package com.darelbitsy.dbweather.ui.alert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.services.KillCheckerService;

/**
 * Created by Darel Bitsy on 01/02/17.
 */
public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_advice);
        String icon = getIntent().getStringExtra(ConstantHolder.NOTIF_ICON);
        String summary = getIntent().getStringExtra(ConstantHolder.NOTIF_SUMMARY);
        double temperature = getIntent().getDoubleExtra(ConstantHolder.NOTIF_TEMPERATURE, 0);

        startService(new Intent(getApplication(), KillCheckerService.class));

        ImageView iconWeather = (ImageView) findViewById(R.id.iconWeather);
        ImageButton closeButton = (ImageButton) findViewById(R.id.close_button);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        TextView descriptionLabel = (TextView) findViewById(R.id.description_label);
        TextView summaryWeather = (TextView) findViewById(R.id.summaryWeather);

        iconWeather.setImageResource(WeatherUtil.getIconId(icon));
        NotificationHelper notificationHelper = new NotificationHelper(this, icon, temperature);
        titleText.setText(notificationHelper.getTitleFromIcon());

        closeButton.setOnClickListener(view -> {
            new Handler().post(new AlarmConfigHelper(getApplicationContext())::setClothingNotificationAlarm);
            finish();
        });
        descriptionLabel.setText(notificationHelper.getDescription());
        summaryWeather.setText(summary);
    }
}
