package com.dbeginc.dbweather.ui.notifications;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.ActivityNotifAdviceBinding;
import com.dbeginc.dbweather.ui.welcome.WelcomeActivity;
import com.dbeginc.dbweather.utils.helper.NotificationHelper;
import com.dbeginc.dbweather.utils.services.KillCheckerService;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIF_ICON;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIF_SUMMARY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIF_TEMPERATURE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

/**
 * Created by Darel Bitsy on 01/02/17.
 * Notification activity that show
 * User about notification
 */
public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityNotifAdviceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notif_advice);

        setSupportActionBar((Toolbar) binding.notificationToolbar.getRoot());

        final Bundle data = getIntent().getExtras();

        final String icon = data.getString(NOTIF_ICON);
        final String summary = data.getString(NOTIF_SUMMARY);
        final int temperature = data.getInt(NOTIF_TEMPERATURE);

        Log.i(TAG, String.format("ICON: %s SUMMARY: %s TEMPERATURE: %d", icon, summary, temperature));

        startService(new Intent(getApplication(), KillCheckerService.class));

        final NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext(), icon, temperature);

        binding.notificationIconWeather.setImageResource(WeatherUtil.getIconId(icon));
        binding.notificationToolbar.notificationTitleText.setText(notificationHelper.getTitleFromIcon());
        binding.notificationSummaryWeather.setText(summary);
        binding.notificationDescriptionLabel.setText(notificationHelper.getDescription());

        binding.notificationToolbar.notificationExpandButton.setOnClickListener(view -> {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });
        binding.notificationToolbar.notificationCloseButton.setOnClickListener(view -> finish());
    }
}
