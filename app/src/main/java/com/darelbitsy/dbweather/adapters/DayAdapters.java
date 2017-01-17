package com.darelbitsy.dbweather.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.ui.DayWeatherData;
import com.darelbitsy.dbweather.weather.Day;

import java.util.Arrays;

/**
 * Created by Darel Bitsy on 13/01/17.
 */

public class DayAdapters extends RecyclerView.Adapter<DayAdapters.DayViewHolder> {
    public static final String DAY_WEATHER = "the_day";
    private Day[] mDays;
    private Context mContext;

    public DayAdapters(Day[] days, Context context) {
        mDays = Arrays.copyOf(days, days.length);
        mContext = context;
    }

    public class DayViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        Day theDay;
        ImageView dailyIconImageView;
        TextView dailytemperatureLabel;
        TextView dayNameLabel;

        public DayViewHolder(View itemView) {
            super(itemView);
            dailyIconImageView = (ImageView) itemView.findViewById(R.id.dailyIconImageView);
            dailytemperatureLabel = (TextView) itemView.findViewById(R.id.dailytemperatureLabel);
            dayNameLabel = (TextView) itemView.findViewById(R.id.dayNameLabelText);
            itemView.setOnClickListener(this);
        }

        public void bindDay(Day day, boolean startPosition) {
            theDay = day;
            dailyIconImageView.setImageResource(day.getIconId());
            dailytemperatureLabel.setText(day.getTemperatureMax() + "");
            if(startPosition) { dayNameLabel.setText(R.string.today_lobel); }
            else { dayNameLabel.setText(day.getDayOfTheWeek()); }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DayWeatherData.class);
            intent.putExtra(DAY_WEATHER, theDay);
            mContext.startActivity(intent);
        }
    }

    @Override
    public DayAdapters.DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Returning a DayViewHolder with the view passed in
        return new DayViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DayAdapters.DayViewHolder holder, int position) {
        if(position == 0) { holder.bindDay(mDays[position], true); }
        else { holder.bindDay(mDays[position], false); }
    }

    @Override
    public int getItemCount() {
        return (mDays != null && mDays.length > 0) ? mDays.length: 0;
    }
}
