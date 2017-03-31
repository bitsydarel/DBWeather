package com.darelbitsy.dbweather.ui.alert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.helper.services.KillCheckerService;
import com.darelbitsy.dbweather.ui.WelcomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NOTIF_ICON;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NOTIF_SUMMARY;
import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.NOTIF_TEMPERATURE;

/**
 * Created by Darel Bitsy on 01/02/17.
 * Notification activity that show
 * User about notification
 */
public class NotificationActivity extends AppCompatActivity {
    @BindView(R.id.notification_description_label)
    TextView notification_description_label;

    @BindView(R.id.notificationSummaryWeather)
    TextView notificationSummaryWeather;

    @BindView(R.id.notification_title_text)
    TextView notification_title_text;

    @BindView(R.id.notificationIconWeather)
    ImageView notificationIconWeather;

    @BindView(R.id.notification_close_button)
    ImageButton notification_close_button;

    @BindView(R.id.notification_expand_button)
    ImageButton notification_expand_button;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_advice);
        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.notificationToolbar);
        setSupportActionBar(mToolbar);

        Bundle data = getIntent().getExtras();

        String icon = data.getString(NOTIF_ICON);
        String summary = data.getString(NOTIF_SUMMARY);
        int temperature = data.getInt(NOTIF_TEMPERATURE);

        Log.i("RECEIVER", "ICon: " + icon +
                " summary: " + summary +
                " temperature: " + temperature
        );
        startService(new Intent(getApplication(), KillCheckerService.class));

        NotificationHelper notificationHelper = new NotificationHelper(this, icon, temperature);

        notificationIconWeather.setImageResource(WeatherUtil.getIconId(icon));
        notification_title_text.setText(notificationHelper.getTitleFromIcon());
        notificationSummaryWeather.setText(summary);
        notification_description_label.setText(notificationHelper.getDescription());

        notification_close_button.setOnClickListener(view -> {
            new Handler().post(new AlarmConfigHelper(getApplicationContext())::setClothingNotificationAlarm);
            finish();
        });

        notification_expand_button.setOnClickListener(view ->
            startActivity(new Intent(this, WelcomeActivity.class)));
    }
}
