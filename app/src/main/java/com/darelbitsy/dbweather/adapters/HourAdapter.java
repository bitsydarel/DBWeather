package com.darelbitsy.dbweather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Arrays;

/**
 * Created by Darel Bitsy on 12/01/17.
 */

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {
    private Hour[] mHours;

    public HourAdapter(Hour[] hours) {
        mHours = Arrays.copyOf(hours, hours.length);
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new HourViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.hourly_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return (mHours != null && mHours.length > 9) ? mHours.length : 0;
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

        void bindHour(Hour hour) {
            hourlyTimeLabel.setText(hour.getHour());
            hourlySummaryLabel.setText(hour.getSummary());
            hourlyTemperatureLabel.setText(hour.getTemperature() + "°");
            hourlyIconImageView.setImageResource(hour.getIconId(hour.getIcon()));
        }
    }
}
