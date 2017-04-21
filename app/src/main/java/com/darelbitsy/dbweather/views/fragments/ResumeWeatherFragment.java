package com.darelbitsy.dbweather.views.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.views.adapters.listAdapter.HourAdapter;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.weather.Hourly;
import com.jakewharton.threetenabp.AndroidThreeTen;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Darel Bitsy on 11/02/17.
 */

public class ResumeWeatherFragment extends Fragment {
    private Hourly mHourly;
    private String mWeekSummary;
    private String cityName;
    private View mCurrentView;

    @BindView(R.id.resumeToolbar)
    Toolbar resumeToolbar;

    @BindView(R.id.hourlyRecyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.resumeCityName)
    TextView mResumeCityName;

    @BindView(R.id.resumeDaySummary)
    Button resumeDaySummary;

    @BindView(R.id.resumeWeekSummary)
    Button resumeWeekSummary;

    @BindView(R.id.resumeSummary)
    TextView resumeSummary;

    private HourAdapter mAdaptater;
    private DatabaseOperation mDatabase;

    public static ResumeWeatherFragment newInstance(Hourly hourly, String weekSummary, String cityName) {
        ResumeWeatherFragment resumeWeatherFragment = new ResumeWeatherFragment();

        Bundle args = new Bundle();
        args.putParcelable(ConstantHolder.HOURLY_WEATHER_KEY, hourly);
        args.putString(ConstantHolder.WEEK_SUMMARY, weekSummary);
        args.putString(ConstantHolder.CITY_NAME_KEY, cityName);

        resumeWeatherFragment.setArguments(args);
        return resumeWeatherFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mDatabase = DatabaseOperation.newInstance(getActivity());

        mHourly = args.getParcelable(ConstantHolder.HOURLY_WEATHER_KEY);
        mWeekSummary = args.getString(ConstantHolder.WEEK_SUMMARY);
        cityName = args.getString(ConstantHolder.CITY_NAME_KEY);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.resume_weather_fragment, container, false);
        ButterKnife.bind(this, mCurrentView);
        AndroidThreeTen.init(getActivity());

        if (mHourly != null) {
            mAdaptater = new HourAdapter(mHourly.getData());
            mRecyclerView.setAdapter(mAdaptater);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL,
                    false));
        }

        mResumeCityName.setText(cityName);
        resumeDaySummary.setOnClickListener(view -> {
            resumeSummary.setText(mHourly.getSummary());
            resumeDaySummary.setTextColor(Color.parseColor("#80ffffff"));
            resumeWeekSummary.setTextColor(Color.parseColor("#30ffffff"));

        });
        resumeWeekSummary.setOnClickListener(view -> {
            resumeSummary.setText(mWeekSummary);
            resumeWeekSummary.setTextColor(Color.parseColor("#80ffffff"));
            resumeDaySummary.setTextColor(Color.parseColor("#30ffffff"));
        });

        return mCurrentView;
    }

    public void updateResumeFragment(final Hourly hourly,final String weekSummary,final String cityName) {
        mHourly = hourly;
        mWeekSummary = weekSummary;
        this.cityName = cityName;
        if (mRecyclerView != null) { ((HourAdapter) mRecyclerView.getAdapter()).updateData(mHourly.getData()); }
    }
}
