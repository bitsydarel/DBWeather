package com.darelbitsy.dbweather.helper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.receiver.AlarmWeatherReceiver;
import com.darelbitsy.dbweather.services.KillCheckerService;
import com.darelbitsy.dbweather.ui.MainActivity;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.darelbitsy.dbweather.helper.AlarmConfigHelper.MY_ACTION;
import static com.darelbitsy.dbweather.ui.MainActivity.IS_ALARM_ON;
import static com.darelbitsy.dbweather.ui.MainActivity.TAG;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Class GetWeather is a helper class
 * that get the json data from api
 * parse json data
 * fetch the data on the layout
 */

public class GetWeatherData extends AsyncTask<Object, Boolean, String> {
    private final WeatherApi mWeatherApi = new WeatherApi();
    private final Context mContext;
    private final DatabaseOperation mDatabase;
    private final WeatherCallHelper mCallHelper;
    private String mCityName;

    public GetWeatherData(Context context) {
        mContext = context;
        mDatabase = new DatabaseOperation(context);
        mCallHelper = new WeatherCallHelper(context, mDatabase);
        Double[] coordinates = mDatabase.getCoordinates();
        mCityName = getLocationName(coordinates[0], coordinates[1]);
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
        current.setCityName(mCityName);
        current.setWeekSummary(forecastData.getJSONObject("daily").getString("summary"));

        return current;
    }

    @Override
    protected String doInBackground(Object... params) {
        mCallHelper.call();
        if (!isAlarmSet()) {
            ExecutorService executorService;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                executorService = new ForkJoinPool();
                executorService.submit(new AlarmConfigHelper(mContext)::setClothingNotificationAlarm);
                Log.i(TAG, "Setted the alarm from MainActivity");
                executorService.submit(() -> FeedDataInForeground.setNextSync(mContext));
                Log.i(TAG, "Setted the hourly sync from MainActivity");
            } else {
                executorService = Executors.newCachedThreadPool();
                executorService.submit(new AlarmConfigHelper(mContext)::setClothingNotificationAlarm);
                Log.i(TAG, "Setted the alarm from MainActivity");
                executorService.submit(() -> FeedDataInForeground.setNextSync(mContext));
                Log.i(TAG, "Setted the hourly sync from MainActivity");
            }

            mContext.startService(new Intent(mContext, KillCheckerService.class));
            executorService.shutdown();

            mContext.getSharedPreferences(DatabaseOperation.PREFS_NAME, mContext.MODE_PRIVATE)
                    .edit()
                    .putBoolean(IS_ALARM_ON, true)
                    .apply();
        }
        return mCallHelper.getJsonData();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        String json = jsonData.isEmpty() ? mDatabase.getJsonData() : jsonData;
        if(json != null && !json.isEmpty()) {
            if(!jsonData.isEmpty()) { mDatabase.setJsonData(jsonData); }
            try {
                setWeatherData(json);
                if(!jsonData.isEmpty()) {
                    mDatabase.setJsonData(jsonData);
                }
            } catch (JSONException e) {
                Log.e(MainActivity.TAG, "Error : " + e.getMessage());
            }
        }
    }

    private boolean isAlarmSet() {
        int lastAlarm = mContext.getSharedPreferences(DatabaseOperation.PREFS_NAME, mContext.MODE_PRIVATE)
                .getInt(AlarmConfigHelper.LAST_NOTIFICATION_PENDING_INTENT_ID, 0);
        if (lastAlarm == 0 ) { return false; }

        Intent notificationLIntent = new Intent(mContext, AlarmWeatherReceiver.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            notificationLIntent.setFlags(0);
        } else {
            notificationLIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        notificationLIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        notificationLIntent.setAction(MY_ACTION);

        return PendingIntent.getBroadcast(mContext,
                lastAlarm,
                notificationLIntent,
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    private void setWeatherData(String jsonData) throws JSONException {
        Current current = getCurrentWeather(jsonData);
        Hour[] hours = getHourlyWeather(jsonData);
        Day[] days = getDailyWeather(jsonData);

        mWeatherApi.setCurrent(current);
        mWeatherApi.setHour(hours);
        mWeatherApi.setDay(days);

        mDatabase.saveCurrentWeather(current, mCallHelper);
        mDatabase.saveHourlyWeather(hours);
        mDatabase.saveDailyWeather(days);
    }

    public WeatherApi getWeatherApi() {
        return mWeatherApi;
    }

    /**
     * Get Location name based on latitude and longitude
     * @param latitude the latitude from location
     * @param longitude the longitude from location
     * @return the location in format (City, Country)
     */
    public String getLocationName(double latitude, double longitude) {
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
            Log.e(MainActivity.TAG, "Error message: " + e);
            cityInfoBuilder = getLocationWithGoogleMapApi(latitude, longitude);
        }

        return cityInfoBuilder;
    }

    public void setLatitude(double latitude) { mCallHelper.setLatitude(latitude); }
    public void setLongitude(double longitude) { mCallHelper.setLongitude(longitude); }

    private String getLocationWithGoogleMapApi(double latitude, double longitude) {
        String[] result = { mContext.getString(R.string.unknown_location) };
        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(String.format(Locale.ENGLISH, "https://maps.googleapi.com/maps/api/geocode/json?latlng=%f,%f",
                        latitude,
                        longitude))
                .build();

        Call apiCall = httpClient.newCall(httpRequest);
        apiCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(MainActivity.TAG, e.getMessage() + " this error happened during the call");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        result[0] = getCityAddress(new JSONObject(response.body().string()));
                    }
                } catch (IOException | JSONException e) {
                    Log.e(MainActivity.TAG, "Exception caught: ", e);
                }
            }
        });
        try {
            return apiCall.execute().body().string();
        } catch (IOException e) {
            Log.e(TAG, "Error : "+ e.getMessage());
        }

        return result[0];
    }

    private String getCityAddress(JSONObject jsonResult) {
        String resultFromArray = "";
        if (jsonResult.has("results")) {
            try {
                JSONArray jsonArray = jsonResult.getJSONArray("results");

                if (jsonArray.length() > 0) {
                    JSONArray components = jsonArray.getJSONObject(0).getJSONArray("address_components");

                    for (int i = 0; i < components.length(); i++) {
                        JSONArray types = components.getJSONObject(i).getJSONArray("types");

                        for (int j = 0; j < types.length(); j++) {
                            if ("locality".equals(types.getString(j))) {
                                resultFromArray = components.getJSONObject(i).getString("long_name");

                            } else if ("country".equals(types.getString(j))) {
                                return resultFromArray + ", " + components.getJSONObject(i).getString("long_name");
                            }
                        }
                    }
                }
            } catch (JSONException e) { Log.i(MainActivity.TAG, "Error " + e.getMessage()); }
        }
        return resultFromArray;
    }
}
