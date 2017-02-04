package com.darelbitsy.dbweather.alert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Locale;

public class NotificationHelper {
    private AlertDialog.Builder mBuilder;
    private Hour mHour;
    private Context mContext;
    private AlertDialog mDialog;

    public NotificationHelper(Context context, Hour hour) {
        mBuilder = new AlertDialog.Builder(context);
        mHour = hour;
        mContext = context;
    }

    public AlertDialog setDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.activity_notif_advice, null);
        ImageView iconWeather = (ImageView) customView.findViewById(R.id.iconWeather);
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.close_button);
        TextView titleText = (TextView) customView.findViewById(R.id.title_text);
        TextView descriptionLabel = (TextView) customView.findViewById(R.id.description_label);
        TextView summaryWeather = (TextView) customView.findViewById(R.id.summaryWeather);


        iconWeather.setImageResource(mHour.getIconId());
        titleText.setText(getTitleFromIcon());
        closeButton.setOnClickListener(view -> {
            mDialog.dismiss();
            Activity x = (Activity) mContext;
            new Handler().post(new AlarmConfigHelper(mContext)::cancelClothingNotificationAlarm);
            x.finish();
        });
        descriptionLabel.setText(getDescription());
        summaryWeather.setText(mHour.getSummary());

        mBuilder.setView(customView);
        mDialog = mBuilder.create();
        return mDialog;
    }

    public String getDescription() {
        String description = "";
        switch (mHour.getIcon()) {
            case "clear-night":
                description = "";
                break;
            case "clear-day":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.clear_day_desc), mHour.getTemperature());
                break;
            case "snow":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.snow_desc), mHour.getTemperature());
                break;
            case "sleet":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.sleet_desc), mHour.getTemperature());
                break;
            case "wind":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.wind_desc), mHour.getTemperature());
                break;
            case "fog":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.fog_desc), mHour.getTemperature());
                break;
            case "cloudy":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.cloudy), mHour.getTemperature());
                break;
            case "partly-cloudy-day":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.partly_cloudy_day_desc), mHour.getTemperature());
                break;
            case "partly-cloudy-night":
                description = String.format(Locale.getDefault(), mContext.getString(R.string.partly_cloudy_night_desc), mHour.getTemperature());
                break;
            default:
                break;
        }
        return description;
    }

    public String getTitleFromIcon() {
        String title = mContext.getResources().getString(R.string.rain_title);
        switch (mHour.getIcon()) {
            case "clear-night":
                title = mContext.getString(R.string.clear_night_title);
                break;
            case "clear-day":
                title = mContext.getString(R.string.clear_day_title);
                break;
            case "snow":
                title = mContext.getString(R.string.snow_title);
                break;
            case "sleet":
                title = mContext.getString(R.string.sleet_title);
                break;
            case "wind":
                title = mContext.getString(R.string.wind_title);
                break;
            case "fog":
                title = mContext.getString(R.string.fog_title);
                break;
            case "cloudy":
                title = mContext.getString(R.string.cloudy_title);
                break;
            case "partly-cloudy-day":
                title = mContext.getString(R.string.partly_cloudy_day_title);
                break;
            case "partly-cloudy-night":
                title = mContext.getString(R.string.partly_cloudy_night_title);
                break;
            default:
                break;
        }
        return title;
    }
}
