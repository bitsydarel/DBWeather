package com.dbeginc.dbweather.ui.main.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.dbeginc.dbweather.models.datatypes.weather.HourlyData;

import java.util.List;

import javax.annotation.Nonnull;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.HOURLY_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.INDEX;

/**
 * Created by Bitsy Darel on 16.05.17.
 * HourlyData update handler
 */

public class HourDiffCallback extends DiffUtil.Callback {

    private final List<HourlyData> oldListOfHourlyData;
    private final List<HourlyData> newListOfHourlyData;

    HourDiffCallback(@Nonnull final List<HourlyData> oldList, @Nonnull final List<HourlyData> newList) {
        this.oldListOfHourlyData = oldList;
        this.newListOfHourlyData = newList;
    }

    @Override
    public int getOldListSize() { return oldListOfHourlyData.size(); }

    @Override
    public int getNewListSize() { return newListOfHourlyData.size(); }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldListOfHourlyData.get(oldItemPosition).getTime() ==
                newListOfHourlyData.get(newItemPosition).getTime();
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
        final HourlyData oldHourlyData = oldListOfHourlyData.get(oldItemPosition);
        final HourlyData newHourlyData = newListOfHourlyData.get(newItemPosition);

        return (oldHourlyData.getTemperature() == newHourlyData.getTemperature())
                && (oldHourlyData.getSummary().equalsIgnoreCase(newHourlyData.getSummary()));
    }

    @Nullable
    @Override
    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
        final Bundle bundle = new Bundle();

        bundle.putInt(INDEX, oldItemPosition);
        bundle.putParcelable(HOURLY_DATA_KEY, newListOfHourlyData.get(newItemPosition));

        return bundle;
    }
}
