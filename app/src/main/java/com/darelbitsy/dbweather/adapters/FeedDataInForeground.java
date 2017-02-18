package com.darelbitsy.dbweather.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.darelbitsy.dbweather.helper.WeatherCallHelper;
import com.darelbitsy.dbweather.receiver.SyncDataReceiver;
import com.darelbitsy.dbweather.ui.MainActivity;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Darel Bitsy on 03/02/17.
 */

public class FeedDataInForeground {
    private Context mContext;
    private DatabaseOperation mDatabase;
    private WeatherCallHelper mCallHelper;

    public FeedDataInForeground(Context context) {
        mContext = context;
        mDatabase = new DatabaseOperation(context);
        mCallHelper = new WeatherCallHelper(context, mDatabase);
    }

    public void performSync() {
        AndroidThreeTen.init(mContext);
        String json = mCallHelper.call().isEmpty() ? mDatabase.getJsonData() : mCallHelper.getJsonData();
        try {
            mDatabase.saveCurrentWeather(getCurrentWeather(json), mCallHelper);
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, e.getMessage());
        }
        try {
            mDatabase.saveHourlyWeather(getHourlyWeather(json));
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, e.getMessage());
        }
        try {
            mDatabase.saveDailyWeather(getDailyWeather(json));
        } catch (JSONException e) {
            Log.i(MainActivity.TAG, e.getMessage());
        }
        Log.i(MainActivity.TAG, json);
        FeedDataInForeground.setNextSync(mContext);
    }

    public static void setNextSync(Context context) {
        AndroidThreeTen.init(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent syncTaskIntent = new Intent(context, SyncDataReceiver.class);
        syncTaskIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                7127,
                new Intent(context, SyncDataReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 3600000, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    /**
     * parse json data to extract hourly weather
     * @param jsonData (json response from the api)
     * @return Return a Hour Arrays (weather by hours)
     * @throws JSONException
     */
    private Hour[] getHourlyWeather(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);
        JSONObject hourly = forecastData.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");
        String timeZone = forecastData.getString("timezone");

        Hour[] hours = new Hour[48];
        for (int i = 0; i < hours.length; i++) {
            JSONObject json = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setSummary(json.getString("summary"));
            hour.setTemperature(json.getDouble("temperature"));
            hour.setTimeZone(timeZone);
            hour.setTime(json.getLong("time"));
            hour.setIcon(json.getString("icon"));
            hour.setHumidity(json.getDouble("humidity"));
            hour.setPrecipChance(json.getDouble("precipProbability"));
            hours[i] = hour;
        }
        return hours;
    }

    /**
     * parse json data to extract daily weather
     * @param jsonData (json response from the api)
     * @return Return a Day Arrays (weather by days)
     * @throws JSONException
     */
    private Day[] getDailyWeather(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);
        JSONArray data = forecastData.getJSONObject("daily").getJSONArray("data");
        String timeZone = forecastData.getString("timezone");

        Day[] days = new Day[8];
        for (int i = 0; i < days.length; i++) {
            JSONObject json = data.getJSONObject(i);
            Day day = new Day();
            day.setSummary(json.getString("summary"));
            day.setTemperatureMax(json.getDouble("temperatureMax"));
            day.setTimeZone(timeZone);
            day.setTime(json.getLong("time"));
            day.setIcon(json.getString("icon"));
            day.setHumidity(json.getDouble("humidity"));
            day.setPrecipChance(json.getDouble("precipProbability"));

            days[i] = day;
        }
        return days;
    }

    /**
     * parse json data to extract current weather
     * @param jsonData (json response from the api)
     * @return Return a Current weather
     * @throws JSONException
     */
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
        current.setCityName(getLocationName(mCallHelper.getLatitude(), mCallHelper.getLongitude()));
        current.setWeekSummary(forecastData.getJSONObject("daily").getString("summary"));

        return current;
    }

    /**
     * Get Location name based on latitude and longitude
     * @param latitude
     * @param longitude
     * @return the location in format (City, Country)
     */
    private String getLocationName(double latitude, double longitude) {
        String cityInfoBuilder = "";
        try {
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                cityInfoBuilder = String.format(Locale.getDefault(), "%s, %s",
                        addresses.get(0).getLocality(),
                        addresses.get(0).getCountryName());
            }

        } catch (IOException e) {
            Log.i(MainActivity.TAG, "Error message: " + e);
        }

        return cityInfoBuilder;
    }
}
