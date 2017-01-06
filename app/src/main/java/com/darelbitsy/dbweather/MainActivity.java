package com.darelbitsy.dbweather;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    private ColorManager mColorPicker = new ColorManager();

    @BindView(R.id.activity_main) RelativeLayout mMainLayout;

    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;

    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.degreeImageView) ImageView mDegreeImageView;

    @BindView(R.id.refreshImageView) ImageButton mRefreshButton;

    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude = -4.7485; //-4.7485,11.8523
        final double longitude = 11.8523;

        mRefreshButton.setOnClickListener((view) -> getWeather(latitude, longitude));
        getWeather(latitude, longitude);

        Log.d(TAG, "Main UI code is Running!");
    }

    private void getWeather(double latitude, double longitude) {
        mMainLayout.setBackgroundColor(mColorPicker.getColor());
        String apiKey = "07aadf598548d8bb35d6621d5e3b3c7b";
        String API = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if(isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient httpClient = new OkHttpClient();
            Request httpRequest = new Request.Builder()
                    .url(API)
                    .build();

            Call call = httpClient.newCall(httpRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> toggleRefresh());
                    alertUserAboutError();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> toggleRefresh());
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);

                        if(response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(() -> updateDisplay());
                        }
                        else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) { Log.e(TAG, "Exception caught: ", e); }
                    catch (JSONException e) { Log.e(TAG, "Exception caught: ", e); }

                }
            });

        }
        else {
            alertUserAboutNetworkError();
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mRefreshButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mLocationLabel.setText(mCurrentWeather.getTimeZone());
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());

        Drawable drawable = ContextCompat.getDrawable(this, mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);

        JSONObject weatherData = forecastData.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTimeZone(forecastData.getString("timezone"));
        currentWeather.setTime(weatherData.getLong("time"));
        currentWeather.setSummary(weatherData.getString("summary"));
        currentWeather.setIcon(weatherData.getString("icon"));
        currentWeather.setPrecipChance(weatherData.getDouble("precipProbability"));
        currentWeather.setTemperature(weatherData.getDouble("temperature"));
        currentWeather.setHumidity(weatherData.getDouble("humidity"));

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private void alertUserAboutNetworkError() {
        NetworkAlertDialogFragment dialog = new NetworkAlertDialogFragment();
        dialog.show(getFragmentManager(), "network_error_dialog");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) { isAvailable = true; }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
