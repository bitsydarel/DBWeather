package com.darelbitsy.dbweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "07aadf598548d8bb35d6621d5e3b3c7b";
        double latitude = 37.8267;
        double longitude = -122.4233;
        String API = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if(isNetworkAvailable()) {

            OkHttpClient httpClient = new OkHttpClient();
            Request httpRequest = new Request.Builder()
                    .url(API)
                    .build();

            Call call = httpClient.newCall(httpRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);

                        if(response.isSuccessful()) { mCurrentWeather = getCurrentDetails(jsonData); }
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

        Log.d(TAG, "Main UI code is Running!");
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
