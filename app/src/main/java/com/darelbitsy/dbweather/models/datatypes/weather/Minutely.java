package com.darelbitsy.dbweather.models.datatypes.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class Minutely {
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("data")
    @Expose
    private List<MinutelyData> data = null;

    public Minutely() {}

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public List<MinutelyData> getData() {
        return data;
    }

    public void setData(final List<MinutelyData> data) {
        this.data = data;
    }
}
