package com.darelbitsy.dbweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "07aadf598548d8bb35d6621d5e3b3c7b";
        double latitude = 37.8267;
        double longitude = -122.4233;
        String API = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(API)
                .build();

        Call call = httpClient.newCall(httpRequest);

    }
}
