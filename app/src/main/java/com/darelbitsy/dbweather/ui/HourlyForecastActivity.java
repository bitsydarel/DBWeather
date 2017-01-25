package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.HourAdapter;
import com.darelbitsy.dbweather.helper.OnSwipeTouchListener;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Arrays;

public class HourlyForecastActivity extends Activity {
    private Hour[] mHours;
    private String[] mHourlyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        RelativeLayout hourlyLayout = (RelativeLayout) findViewById(R.id.activity_hourly_forecast);
        //setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        RecyclerView hourlyForecastRecyclerView = (RecyclerView) findViewById(R.id.hourlyRecyclerView);
        TextView hourlySummary = (TextView) findViewById(R.id.hourlySummary);
        TextView checkEmpty = (TextView) findViewById(R.id.checkEmpty);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_WEATHER);
        mHourlyInfo = intent.getStringArrayExtra(MainActivity.HOURLY_INFO);


        if(parcelables == null || parcelables.length == 0) {
            checkEmpty.setVisibility(View.VISIBLE);
            hourlyForecastRecyclerView.setVisibility(View.GONE);

        } else {
            Log.i("HOUR", "" + parcelables.length);
            mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);
            ImageView hourlyIcon = (ImageView) findViewById(R.id.hourlyIcon);
            hourlyIcon.setImageResource(mHours[0].getIconId(mHourlyInfo[0]));
            hourlySummary.setText(mHourlyInfo[1]);

            HourAdapter adapter = new HourAdapter(mHours);
            hourlyForecastRecyclerView.setAdapter(adapter);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            hourlyForecastRecyclerView.setLayoutManager(layoutManager);
            hourlyForecastRecyclerView.setHasFixedSize(true);
        }

        hourlyLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(HourlyForecastActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.HOURLY_WEATHER, mHours);
                intent.putExtra(MainActivity.HOURLY_INFO, mHourlyInfo);
                finish();
                startActivity(intent);
            }
        });

        Log.i("LIFECYCLE", "OnCreate METHOD in HourlyActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LIFECYCLE", "OnPause METHOD in HourlyActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LIFECYCLE", "OnStop METHOD in HourlyActivity");
    }
}
