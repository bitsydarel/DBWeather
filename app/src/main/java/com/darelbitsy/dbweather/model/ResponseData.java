package com.darelbitsy.dbweather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public class ResponseData {
    @SerializedName("translatedText")
    @Expose
    private String translatedText;

    @SerializedName("match")
    @Expose
    private double match;

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(final String translatedText) {
        this.translatedText = translatedText;
    }

    public double getMatch() {
        return match;
    }

    public void setMatch(final double match) {
        this.match = match;
    }
}
