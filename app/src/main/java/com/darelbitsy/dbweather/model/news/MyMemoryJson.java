package com.darelbitsy.dbweather.model.news;

import com.darelbitsy.dbweather.model.ResponseData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Darel Bitsy on 22/02/17.
 */

public class MyMemoryJson {
    @SerializedName("responseData")
    @Expose
    private ResponseData responseData;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }
}
