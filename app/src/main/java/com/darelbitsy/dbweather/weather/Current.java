package com.darelbitsy.dbweather.weather;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class Current extends WeatherData {
    private String mWeekSummary;

    public String getFormattedTime() {
        final DateTimeFormatter format =
                DateTimeFormatter.ofPattern("h:mm a");

        return Instant.ofEpochSecond(getTime())
                .atZone(ZoneId.of(getTimeZone()))
                .format(format);
    }
    public void setWeekSummary(String weekSummary) { mWeekSummary = weekSummary; }

    public String getWeekSummary() { return mWeekSummary != null ? mWeekSummary : "--"; }
}
