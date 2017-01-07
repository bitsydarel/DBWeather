package com.darelbitsy.dbweather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
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

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = MainActivity.class.getSimpleName();
    //private Current mCurrent;

    private WeatherApi mWeather;

    private ColorManager mColorPicker = new ColorManager();

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

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
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Configuring the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Check if the user has already granted the permission if not, ask it
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude = -4.7485; //-4.7485,11.8523
        final double longitude = 11.8523;

        mRefreshButton.setOnClickListener((view) -> getWeather(latitude, longitude));
        getWeather(latitude, longitude);
    }

    /*
    * The getWeather function
    * get the current weather with the forecast api
    */
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
                            mWeather = parseWeatherDetails(jsonData);
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

    /*
    *This function
    * hide the refresh button or show the refresh button
    */
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
        mTemperatureLabel.setText(mWeather.getCurrent().getTemperature() + "");
        mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + " it will be");
        mLocationLabel.setText(mWeather.getCurrent().getTimeZone());
        mHumidityValue.setText(mWeather.getCurrent().getHumidity() + "");
        mPrecipValue.setText(mWeather.getCurrent().getPrecipChance() + "%");
        mSummaryLabel.setText(mWeather.getCurrent().getSummary());

        Drawable drawable = ContextCompat.getDrawable(this, mWeather.getCurrent().getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private WeatherApi parseWeatherDetails(String jsonData) throws JSONException{
        WeatherApi weather = new WeatherApi();

        weather.setCurrent(getCurrentWeather(jsonData));
        weather.setHour(getHourlyWeather(jsonData));
        weather.setDay(getDailyWeather(jsonData));

        return weather;
    }

    private Hour[] getHourlyWeather(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);
        JSONObject hourly = forecastData.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        String timeZone = forecastData.getString("timezone");

        Hour[] hours = new Hour[data.length()];
        for(int i = 0; i < data.length(); i++) {
            JSONObject json = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setSummary(json.getString("summary"));
            hour.setTemperature(json.getDouble("temperature"));
            hour.setTimeZone(timeZone);
            hour.setTime(json.getLong("time"));
            hour.setIcon(json.getString("icon"));
            hours[i] = hour;
        }

        return hours;
    }

    private Day[] getDailyWeather(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);
        JSONObject daily = forecastData.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        String timeZone = forecastData.getString("timezone");

        Day[] days = new Day[data.length()];
        for(int i = 0; i < data.length(); i++) {
            JSONObject json = data.getJSONObject(i);

            Day day = new Day();
            day.setSummary(json.getString("summary"));
            day.setTemperatureMax(json.getDouble("temperature"));
            day.setTimeZone(timeZone);
            day.setTime(json.getLong("time"));
            day.setIcon(json.getString("icon"));

            days[i] = day;
        }

        return new Day[0];
    }

    private Current getCurrentWeather(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);
        JSONObject currently = forecastData.getJSONObject("currently");

        Current current = new Current();
        current.setTimeZone(forecastData.getString("timezone"));
        current.setTime(currently.getLong("time"));
        current.setSummary(currently.getString("summary"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setHumidity(currently.getDouble("humidity"));

        Log.d(TAG, current.getFormattedTime());

        return current;
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    private void getLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            getWeather(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /* trying to send a Intent to start a google play services activity that can resolve the error
        * if occure
        */
        if (connectionResult.hasResolution()) {
            try {
                // starting a activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, " Following error occure: ", e);
            }
        } else {
            // if no resolution found , display a dialog to the user with error.
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        getWeather(currentLatitude, currentLongitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // Checking if the user cancelled, the permission
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Create the LocationRequest Object to
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(1200 * 1000) // Seconds, in milliseconds
                            .setFastestInterval(1 * 1000); // 1 Seconds, in milliseconds

                    mRefreshButton.setOnClickListener((view) -> getLocation());
                    getLocation();

                } else {}
            }
        }
    }
}
