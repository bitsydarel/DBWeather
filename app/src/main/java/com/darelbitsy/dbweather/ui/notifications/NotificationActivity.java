package com.darelbitsy.dbweather.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.utils.services.KillCheckerService;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NOTIF_ICON;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NOTIF_SUMMARY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NOTIF_TEMPERATURE;

/**
 * Created by Darel Bitsy on 01/02/17.
 * Notification activity that show
 * User about notification
 */
public class NotificationActivity extends AppCompatActivity {
    //TODO Migrate from butterknife to databinding

    /*@BindView(R.id.notification_description_label)
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
    ImageButton notification_expand_button;*/

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_advice);

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

//        NotificationHelper notificationHelper = new NotificationHelper(this, icon, temperature);

        /*notificationIconWeather.setImageResource(WeatherUtil.getIconId(icon));
        notification_title_text.setText(notificationHelper.getTitleFromIcon());
        notificationSummaryWeather.setText(summary);
        notification_description_label.setText(notificationHelper.getDescription());

        notification_close_button.setOnClickListener(view -> {
            new Handler().post(new AlarmConfigHelper(getApplicationContext())::setClothingNotificationAlarm);
            finish();
        });

        notification_expand_button.setOnClickListener(view ->
            startActivity(new Intent(this, WelcomeActivity.class)));*/
    }
}
