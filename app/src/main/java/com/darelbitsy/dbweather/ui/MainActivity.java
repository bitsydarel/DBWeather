package com.darelbitsy.dbweather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.alert.AlertDialogFragment;
import com.darelbitsy.dbweather.alert.NetworkAlertDialogFragment;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;
import com.darelbitsy.dbweather.widgets.SnowFallView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    public final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_SUMMARY = "KEY_SUMMARY";
    private static final String ICON_KEY = "ICON_KEY";
    private static final String HUMIDITY_KEY = "HUMIDITY_KEY";
    private static final String PRECIP_KEY = "PRECIP_KEY";
    private static final String LAST_KNOW_TEMPERATURE = "LAST_KNOW_TEMPERATURE";
    private static final String TIME_OF_LAST_KNOW_TEMP = "TIME_OF_LAST_KNOW_TEMP";
    private static final String LAST_KNOW_LONGITUDE = "LAST_KNOW_LONGITUDE";
    private static final String LAST_KNOW_LATITUDE = "LAST_KNOW_LATITUDE";
    private static final String LAST_KNOW_LOCATION = "LAST_KNOW_LOCATION";
    static final String PREFS_FILE = "com.darelbitsy.dbweather.preferences";
    public static final String DAILY_WEATHER = "DAILY_WEATHER";
    public static final String HOURLY_WEATHER = "HOURLY_WEATHER";
    public static final String CITYNAME = "LOCATION NAME";
    public static final String HOURLY_INFO = "HOURLY_INFO";

    private String[] mLangs = {"ar","az","be","bs","ca","cs","de","el","en","es",
            "et","fr","hr","hu","id","it","is","kw","nb","nl","pl","pt","ru",
            "sk","sl","sr","sv","tet","tr","uk","x-pig-latin","zh","zh-tw"};
    private List<String> supportedLang = Arrays.asList(mLangs);

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;

    private WeatherApi mWeather;
    private ColorManager mColorPicker = new ColorManager();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mIsGpsPermissionOn;
    private String mCityName;
    private String mHourlySummary;
    private String mHourlyIcon;

    private double mLatitude;
    private double mLongitude;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferenceEditor;

    //Defining all the Parent view needed
    @BindView(R2.id.activity_main) RelativeLayout mMainLayout;
    @BindView(R2.id.main_menu) ImageButton mMain_menu;

    @BindView(R2.id.hourlyButton) Button mHourlyButton;
    @BindView(R2.id.dailyButton) Button mDailyButton;

    //Defining TextView needed
    @BindView(R2.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R2.id.timeLabel) TextView mTimeLabel;
    @BindView(R2.id.humidityValue) TextView mHumidityValue;
    @BindView(R2.id.locationLabel) TextView mLocationLabel;
    @BindView(R2.id.precipValue) TextView mPrecipValue;
    @BindView(R2.id.summaryLabel) TextView mSummaryLabel;

    //Defining ImageView and ImageButton to manipulate
    @BindView(R2.id.iconImageView) ImageView mIconImageView;
    @BindView(R2.id.degreeImageView) ImageView mDegreeImageView;
    @BindView(R2.id.refreshImageView) ImageButton mRefreshButton;

    @BindView(R2.id.progressBar) ProgressBar mProgressBar;

    private SnowFallView mSnowFallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidThreeTen.init(this);

        mSnowFallView = new SnowFallView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        mMainLayout.addView(mSnowFallView, params);
        mWeather = new WeatherApi();
        mIsGpsPermissionOn = false;
        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mPreferenceEditor = mSharedPreferences.edit();
        mLongitude = getLongitude();
        mLatitude = getLatitude();

        //Configuring the google api client
        mLocationRequest = createLocationRequest();

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

        } else {
            mIsGpsPermissionOn = true;
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        if (mIsGpsPermissionOn) {
            mRefreshButton.setOnClickListener((view) -> getLocation());
        } else {
            mRefreshButton.setOnClickListener((view) -> getWeather(mLatitude, mLongitude));
        }

        mDailyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra(DAILY_WEATHER, mWeather.getDay());
            intent.putExtra(CITYNAME, mCityName);
            startActivity(intent);
        });

        mHourlyButton.setOnClickListener(view -> {
            String[] hourlyInfo = new String[2];
            hourlyInfo[0] = mHourlyIcon;
            hourlyInfo[1] = mHourlySummary;

            Intent intent = new Intent(this, HourlyForecastActivity.class);
            intent.putExtra(HOURLY_WEATHER, mWeather.getHour());
            intent.putExtra(HOURLY_INFO, hourlyInfo);
            startActivity(intent);
        });

        mMain_menu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                Intent intent = new Intent(MainActivity.this, AboutUs.class);
                startActivity(intent);
                return false;
            });
            popupMenu.show();
        });


        getWeather(mLatitude, mLongitude);
        mCityName = mSharedPreferences.getString(LAST_KNOW_LOCATION, getLocationName(mLatitude, mLongitude));
        Log.i(TAG, "City Name: "+mCityName);
        initializeField();
    }

    public void showPopup(View v) {

    }

    //Get the last know latitude or give a default value
    private double getLatitude() {
        return mSharedPreferences.contains(LAST_KNOW_LATITUDE)
                ? Double.longBitsToDouble(mSharedPreferences.getLong(LAST_KNOW_LATITUDE, 0))
                : -4.7485;
    }

    //Get the last know longitude or give a default value
    private double getLongitude() {
        return mSharedPreferences.contains(LAST_KNOW_LONGITUDE)
                ? Double.longBitsToDouble(mSharedPreferences.getLong(LAST_KNOW_LONGITUDE, 0))
                : 11.8523;
    }

    /*
    * The getWeather function
    * get the current weather with the forecast api
    */
    private void getWeather(double latitude, double longitude) {
        String API;

        String userLang = Locale.getDefault().getLanguage();

        //Checking if user device language is supported by the api if not english language will be used.
        String language = supportedLang.contains(userLang) ? ("?lang="+userLang ) : null;

        //Api Key
        String apiKey = "07aadf598548d8bb35d6621d5e3b3c7b";

        if(language == null) { API = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude + "?units=auto"; }
        else { API = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude + language +"&units=auto"; }

        if (isNetworkAvailable()) {
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

                        if (response.isSuccessful()) {
                            parseWeatherDetails(jsonData);
                            runOnUiThread(() -> updateDisplay());
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }

                }
            });

        } else {
            alertUserAboutNetworkError();
        }
    }

    /*
    *This function
    * hide the refresh button or show the refresh button
    */
    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mRefreshButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {
        int[] colors = mColorPicker.getDrawableForParent();

        mMainLayout.setBackgroundResource(colors[0]);
        mHourlyButton.setTextColor(colors[1]);
        mDailyButton.setTextColor(colors[1]);

        mTemperatureLabel.setText(mWeather.getCurrent().getTemperature() + "");
        mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + " it will be");

        //Setting the location to the current location of the device because the api only provide the timezone as location
        mLocationLabel.setText(mCityName);
        Log.i(TAG, "City Name: "+mCityName);

        mHumidityValue.setText(mWeather.getCurrent().getHumidity() + "%");
        mPrecipValue.setText(mWeather.getCurrent().getPrecipChance() + "%");
        mSummaryLabel.setText(mWeather.getCurrent().getSummary());

        Drawable drawable = ContextCompat.getDrawable(this, mWeather.getCurrent().getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private String getLocationName(double latitude, double longitude) {
        String cityInfoBuilder = "";
        try {

            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                cityInfoBuilder = (addresses.get(0)
                        .getLocality()
                        + ", "
                        + addresses.get(0)
                        .getCountryName());
            }

        } catch (IOException e) {
            Log.e(TAG, "Error message: " + e);
        }

        return cityInfoBuilder;
    }

    private void parseWeatherDetails(String jsonData) throws JSONException {

        mWeather.setCurrent(getCurrentWeather(jsonData));
        mWeather.setHour(getHourlyWeather(jsonData));
        mWeather.setDay(getDailyWeather(jsonData));
    }

    private Hour[] getHourlyWeather(String jsonData) throws JSONException {
        JSONObject forecastData = new JSONObject(jsonData);
        JSONObject hourly = forecastData.getJSONObject("hourly");
        mHourlySummary = hourly.getString("summary");
        mHourlyIcon = hourly.getString("icon");
        JSONArray data = hourly.getJSONArray("data");

        String timeZone = forecastData.getString("timezone");

        Hour[] hours = new Hour[data.length()];
        for (int i = 0; i < data.length(); i++) {
            JSONObject json = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setSummary(json.getString("summary"));
            hour.setTemperature(json.getDouble("temperature"));
            hour.setTimeZone(timeZone);
            hour.setTime(json.getLong("time"));
            hour.setIcon(json.getString("icon"));
            hour.setCityName(mCityName);
            hour.setHumidity(json.getDouble("humidity"));
            hour.setPrecipChance(json.getDouble("precipProbability"));
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
        for (int i = 0; i < data.length(); i++) {
            JSONObject json = data.getJSONObject(i);
            Day day = new Day();
            day.setSummary(json.getString("summary"));
            day.setTemperatureMax(json.getDouble("temperatureMax"));
            day.setTimeZone(timeZone);
            day.setTime(json.getLong("time"));
            day.setIcon(json.getString("icon"));
            day.setCityName(mCityName);
            day.setHumidity(json.getDouble("humidity"));
            day.setPrecipChance(json.getDouble("precipProbability"));

            days[i] = day;
        }

        return days;
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
        current.setCityName(mCityName);

        Log.i(TAG, "City Name: "+mCityName);

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
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mIsGpsPermissionOn) {
            if(LocationServices.FusedLocationApi
                    .getLocationAvailability(mGoogleApiClient)
                    .isLocationAvailable()) { getLocation(); }
        }
    }

    private void getLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            getWeather(location.getLatitude(), location.getLongitude());
            mCityName = getLocationName(location.getLatitude(), location.getLongitude());
            Log.i(TAG, "City Name: "+mCityName);
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
        getWeather(location.getLatitude(), location.getLongitude());
        mCityName = getLocationName(location.getLatitude(), location.getLongitude());
        Log.i(TAG, "City Name: "+mCityName);
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
        mPreferenceEditor.putString(LAST_KNOW_LOCATION, mCityName);
        mPreferenceEditor.putLong(LAST_KNOW_LATITUDE, Double.doubleToRawLongBits(mLatitude));
        mPreferenceEditor.putLong(LAST_KNOW_LONGITUDE, Double.doubleToRawLongBits(mLongitude));
        mPreferenceEditor.putString(LAST_KNOW_TEMPERATURE, mTemperatureLabel.getText().toString());
        mPreferenceEditor.putString(KEY_SUMMARY, mSummaryLabel.getText().toString());
        if(mWeather.getCurrent() != null) {mPreferenceEditor.putInt(ICON_KEY, mWeather.getCurrent().getIconId()); }
        mPreferenceEditor.putString(HUMIDITY_KEY, mHumidityValue.getText().toString());
        mPreferenceEditor.putString(PRECIP_KEY, mPrecipValue.getText().toString());
        mPreferenceEditor.putString(TIME_OF_LAST_KNOW_TEMP, mTimeLabel.getText().toString());
        mPreferenceEditor.apply();
    }

    private void initializeField() {
        mLocationLabel.setText(mSharedPreferences.getString(LAST_KNOW_LOCATION,
                getLocationName(mLatitude, mLongitude)));

        mTemperatureLabel.setText(mSharedPreferences.getString(LAST_KNOW_TEMPERATURE, "--"));

        mSummaryLabel.setText(mSharedPreferences.getString(KEY_SUMMARY,
                getResources().getString(R.string.default_weather_summary)));

        if (mSharedPreferences.contains(ICON_KEY)) { mIconImageView.setImageResource(mSharedPreferences.getInt(ICON_KEY, 0)); }

        mHumidityValue.setText(mSharedPreferences.getString(HUMIDITY_KEY, "--"));
        mPrecipValue.setText(mSharedPreferences.getString(PRECIP_KEY, "--"));
        mTimeLabel.setText(mSharedPreferences.getString(TIME_OF_LAST_KNOW_TEMP, "---"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // Checking if the user cancelled, the permission
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mIsGpsPermissionOn = true;
                    // Create the LocationRequest Object to
                    mLocationRequest = createLocationRequest();

                    mRefreshButton.setOnClickListener((view) -> getLocation());
                    getLocation();

                } else {}
            }
        }
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1200 * 1000) // Seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 Seconds, in milliseconds
    }
}
