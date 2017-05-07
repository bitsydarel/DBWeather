package com.darelbitsy.dbweather.models.datatypes.news;

import com.darelbitsy.dbweather.models.datatypes.mymemory.ResponseData;
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

    public void setResponseData(final ResponseData responseData) {
        this.responseData = responseData;
    }
}
