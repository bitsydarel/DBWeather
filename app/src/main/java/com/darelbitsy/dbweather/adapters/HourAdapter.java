package com.darelbitsy.dbweather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.HourlyData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darel Bitsy on 12/01/17.
 */

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {
    private List<HourlyData> mHours;

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

    class HourViewHolder extends RecyclerView.ViewHolder {
        TextView hourlyTimeLabel;
        TextView hourlySummaryLabel;
        TextView hourlyTemperatureLabel;
        ImageView hourlyIconImageView;

        HourViewHolder(View itemView) {
            super(itemView);
            hourlyTimeLabel = (TextView) itemView.findViewById(R.id.hourlyTimeLabel);
            hourlySummaryLabel = (TextView) itemView.findViewById(R.id.hourlySummaryLabel);
            hourlyTemperatureLabel = (TextView) itemView.findViewById(R.id.hourlyTemperatureLabel);
            hourlyIconImageView = (ImageView) itemView.findViewById(R.id.hourlyIconImageView);
        }

        void bindHour(HourlyData hour) {
            hourlyTimeLabel.setText(WeatherUtil.getHour(hour.getTime(), null));
            hourlySummaryLabel.setText(hour.getSummary());
            hourlyTemperatureLabel.setText(WeatherUtil.getTemperatureInInt(hour.getTemperature()) + "°");
            hourlyIconImageView.setImageResource(WeatherUtil.getIconId(hour.getIcon()));
        }
    }
}
