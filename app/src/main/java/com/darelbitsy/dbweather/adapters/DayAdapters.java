package com.darelbitsy.dbweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.weather.Day;

/**
 * Created by Darel Bitsy on 09/01/17.
 */

public class DayAdapters extends BaseAdapter {
    private Context mContext;
    private Day[] mDays;
    private ColorManager mColorManager;
    private int mColor;

    public DayAdapters(Context context, Day[] days) {
        mContext = context;
        mDays = days;
        mColorManager = new ColorManager();
        mColor = mColorManager.getDrawableForParent()[1];
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.daily_list_item, null);

            holder = new ViewHolder();

            holder.dailyIconImageView = (ImageView) convertView.findViewById(R.id.dailyIconImageView);
            holder.dailytemperatureLabel = (TextView) convertView.findViewById(R.id.dailytemperatureLabel);
            holder.dayNameLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder);

        } else { holder = (ViewHolder) convertView.getTag(); }

        Day day = mDays[position];
        holder.dailyIconImageView.setImageResource(day.getIconId());
        holder.dailytemperatureLabel.setText(day.getTemperatureMax() + "");
        holder.dailytemperatureLabel.setTextColor(mColor);
        holder.dayNameLabel.setText(day.getDayOfTheWeek());

        return convertView;
    }

    private static class ViewHolder {
        ImageView dailyIconImageView;
        TextView dailytemperatureLabel;
        TextView dayNameLabel;
    }
}
