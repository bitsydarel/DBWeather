package com.darelbitsy.dbweather.adapters.listAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public HourAdapter(List<HourlyData> hours) {
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

    public void updateData(List<HourlyData> data) {
        mHours.clear();
        mHours.addAll(data);
        notifyDataSetChanged();
    }

    class HourViewHolder extends RecyclerView.ViewHolder {
        ImageView hourlyIconImage;

        TextView hourlyTime;
        TextView hourlySummary;
        TextView hourlyTemperature;
        TextView hourlyPrecipLabel;
        TextView hourlyPrecipLabelValue;
        TextView hourlyHumidity;
        TextView hourlyWindSpeed;

        ProgressBar hourlyPrecipProgressBar;
        ProgressBar hourlyHumidityProgressBar;
        ProgressBar hourlyWindSpeedProgressBar;

        HourViewHolder(View itemView) {
            super(itemView);
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

            if (mContext == null) { mContext = itemView.getContext(); }

        }

        void bindHour(HourlyData hour) {
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
                        "RAIN/SNOW"));
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
    }
}
