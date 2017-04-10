package com.darelbitsy.dbweather.adapters.listAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.HourlyData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 12/01/17.
 */

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {
    private List<HourlyData> mHours;
    private Context mContext;

    public HourAdapter(final List<HourlyData> hours) {
        mHours = new ArrayList<>(hours);
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new HourViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.hourly_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHours.get(position));
    }

    @Override
    public int getItemCount() {
        return (mHours != null && mHours.size() > 9) ? mHours.size() : 0;
    }

    public void updateData(final List<HourlyData> data) {
        mHours.clear();
        mHours.addAll(data);
        notifyDataSetChanged();
    }

    class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ConstraintLayout mainLayout;
        final LinearLayout hourlyBorderLine;
        final ImageView hourlyIconImage;

        final TextView hourlyTime;
        final TextView hourlySummary;
        final TextView hourlyTemperature;
        final TextView hourlyPrecipLabel;
        final TextView hourlyPrecipLabelValue;
        final TextView hourlyHumidity;
        final TextView hourlyWindSpeed;

        final ProgressBar hourlyPrecipProgressBar;
        final ProgressBar hourlyHumidityProgressBar;
        final ProgressBar hourlyWindSpeedProgressBar;

        HourViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            if (mContext == null) { mContext = itemView.getContext(); }

            hourlyIconImage = (ImageView) itemView.findViewById(R.id.hourlyImage);

            hourlyTime = (TextView) itemView.findViewById(R.id.hourlyTime);
            hourlySummary = (TextView) itemView.findViewById(R.id.hourlySummary);
            hourlyTemperature = (TextView) itemView.findViewById(R.id.hourlyTemperatureValue);
            hourlyPrecipLabel = (TextView) itemView.findViewById(R.id.hourlyPrecipLabel);
            hourlyPrecipLabelValue = (TextView) itemView.findViewById(R.id.hourlyPrecipLabelValue);
            hourlyHumidity = (TextView) itemView.findViewById(R.id.hourlyHumidityValue);
            hourlyWindSpeed = (TextView) itemView.findViewById(R.id.hourlyWindSpeedValue);

            hourlyPrecipProgressBar = (ProgressBar) itemView.findViewById(R.id.hourlyPrecipProgressBar);
            hourlyHumidityProgressBar = (ProgressBar) itemView.findViewById(R.id.hourlyHumidityProgressBar);
            hourlyWindSpeedProgressBar = (ProgressBar) itemView.findViewById(R.id.hourlyWindSpeedProgressBar);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                hourlyPrecipProgressBar.getProgressDrawable()
                        .setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark),
                        PorterDuff.Mode.MULTIPLY);

                hourlyHumidityProgressBar.getProgressDrawable()
                        .setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark),
                        android.graphics.PorterDuff.Mode.MULTIPLY);

                hourlyWindSpeedProgressBar.getProgressDrawable()
                        .setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            mainLayout = (ConstraintLayout) itemView.findViewById(R.id.hourlyInfoDetails);
            mainLayout.setVisibility(View.GONE);
            hourlyBorderLine = (LinearLayout) itemView.findViewById(R.id.hourlyBorderLine);
        }

        void bindHour(final HourlyData hour) {
            hourlyIconImage.setImageResource(WeatherUtil.getIconId(hour.getIcon()));

            hourlyTime.setText(String.format(Locale.getDefault(),
                    mContext.getString(R.string.hourly_time),
                    WeatherUtil.getHour(hour.getTime(), null)));

            hourlySummary.setText(hour.getSummary());

            hourlyTemperature.setText(String.format(Locale.getDefault(),
                    mContext.getString(R.string.hourly_apparentTemperature_value),
                    WeatherUtil.getTemperatureInInt(hour.getTemperature())));

            if (hour.getPrecipType() != null) {
                hourlyPrecipLabel.setText(String.format(Locale.getDefault(),
                        mContext.getString(R.string.hourly_precipeChanceTypeLabel),
                        hour.getPrecipType()));
            } else {
                hourlyPrecipLabel.setText(String.format(Locale.getDefault(),
                        mContext.getString(R.string.hourly_precipeChanceTypeLabel),
                        "Rain/Snow"));
            }

            hourlyPrecipLabelValue.setText(String.format(Locale.getDefault(),
                    mContext.getString(R.string.hourly_precipitation_value),
                    WeatherUtil.getPrecipPourcentage(hour.getPrecipProbability())));

            hourlyHumidity.setText(String.format(Locale.getDefault(),
                    mContext.getString(R.string.hourly_humidity_value),
                    WeatherUtil.getHumidityPourcentage(hour.getHumidity())));

            hourlyWindSpeed.setText(String.format(Locale.getDefault(),
                    mContext.getString(R.string.hourly_windspeed_value),
                    WeatherUtil.getWindSpeedMeterPerHour(hour.getWindSpeed())));

            hourlyPrecipProgressBar.setProgress(WeatherUtil.getPrecipPourcentage(hour.getPrecipProbability()));
            hourlyHumidityProgressBar.setProgress(WeatherUtil.getHumidityPourcentage(hour.getHumidity()));
            hourlyWindSpeedProgressBar.setProgress(WeatherUtil.getWindSpeedMeterPerHour(hour.getWindSpeed()));
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(final View v) {
            if (mainLayout.getVisibility() == View.GONE) {
                mainLayout.setVisibility(View.VISIBLE);
                hourlyBorderLine.setVisibility(View.GONE);
                v.setBackgroundColor(Color.WHITE);

            } else {
                mainLayout.setVisibility(View.GONE);
                v.setBackgroundColor(Color.parseColor("#DCDCDC"));
                hourlyBorderLine.setVisibility(View.VISIBLE);
            }
        }
    }
}
