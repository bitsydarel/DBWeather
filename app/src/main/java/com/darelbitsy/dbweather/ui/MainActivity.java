package com.darelbitsy.dbweather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.helper.WeatherCallHelper;
import com.darelbitsy.dbweather.helper.OnSwipeTouchListener;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;
import com.darelbitsy.dbweather.widgets.RainFallView;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_SUMMARY = "KEY_SUMMARY";
    private static final String ICON_KEY = "ICON_KEY";
    private static final String HUMIDITY_KEY = "HUMIDITY_KEY";
    private static final String PRECIP_KEY = "PRECIP_KEY";
    private static final String LAST_KNOW_TEMPERATURE = "LAST_KNOW_TEMPERATURE";
    private static final String TIME_OF_LAST_KNOW_TEMP = "TIME_OF_LAST_KNOW_TEMP";
    public static final String LAST_KNOW_LONGITUDE = "LAST_KNOW_LONGITUDE";
    public static final String LAST_KNOW_LATITUDE = "LAST_KNOW_LATITUDE";
    private static final String LAST_KNOW_LOCATION = "LAST_KNOW_LOCATION";
    static final String PREFS_FILE = "com.darelbitsy.dbweather.preferences";
    public static final String DAILY_WEATHER = "DAILY_WEATHER";
    public static final String HOURLY_WEATHER = "HOURLY_WEATHER";
    public static final String CITYNAME = "LOCATION NAME";
    public static final String HOURLY_INFO = "HOURLY_INFO";

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;

    private WeatherApi mWeather;
    private ColorManager mColorPicker = new ColorManager();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mIsGpsPermissionOn;
    private String mCityName;
    private String mHourlySummary;
    private String mHourlyIcon;
    private Hour[] mHours;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferenceEditor;
    @BindView(R2.id.activity_main) RelativeLayout mMainLayout;
    @BindView(R2.id.main_menu) ImageButton mMain_menu;

    //Defining TextView needed
    @BindView(R2.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R2.id.timeLabel) TextView mTimeLabel;
    @BindView(R2.id.humidityValue) TextView mHumidityValue;
    @BindView(R2.id.locationLabel) TextView mLocationLabel;
    @BindView(R2.id.precipValue) TextView mPrecipValue;
    @BindView(R2.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R2.id.weekSummary) TextView mWeekSummary;

    //Defining ImageView and ImageButton to manipulate
    @BindView(R2.id.iconImageView) ImageView mIconImageView;
    @BindView(R2.id.degreeImageView) ImageView mDegreeImageView;

    private Button currentFocusedButton;
    private HorizontalScrollView mScrollView;
    private Handler mHandler;
    private Button currentDayButton;

    private double mLongitude;
    private double mLatitude;
    private String[] mHourlyInfo = new String[2];
    private WeatherCallHelper mCallHelper;
    private RelativeLayout.LayoutParams mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidThreeTen.init(this);

        Log.i("LIFECYCLE", "OnCreate METHOD");
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#ff0099cc"),
                Color.parseColor("#ff33b5e5"),
                Color.parseColor("#ff99cc00"),
                Color.parseColor("#ff669900"));

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            new GetWeather().execute();
            refreshLayout.setRefreshing(false);
        });
        mCallHelper = new WeatherCallHelper(this);

        mParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mMainLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if(mHourlyIcon != null && !mHourlyIcon.isEmpty()) { mHourlyInfo[0] = mHourlyIcon; }
                if(mHourlySummary != null && !mHourlySummary.isEmpty()) { mHourlyInfo[1] = mHourlySummary; }

                Intent intent = new Intent(MainActivity.this, HourlyForecastActivity.class);
                intent.putExtra(HOURLY_WEATHER, mWeather.getHour().length > 0 ? mWeather.getHour() : mHours);
                intent.putExtra(HOURLY_INFO, mHourlyInfo);
                startActivity(intent);
                finish();
            }
        });

        mWeather = new WeatherApi();
        mIsGpsPermissionOn = false;
        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mPreferenceEditor = mSharedPreferences.edit();
        mLongitude = mCallHelper.getLongitude();
        mLatitude = mCallHelper.getLatitude();

        mCityName = mSharedPreferences.contains(LAST_KNOW_LATITUDE)
                ? mSharedPreferences.getString(LAST_KNOW_LOCATION, getLocationName(mLatitude, mLongitude))
                : getLocationName(mLatitude, mLongitude);

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

        mCityName = mSharedPreferences.getString(LAST_KNOW_LOCATION, getLocationName(mLatitude, mLongitude));
        Log.i(TAG, "the City Name: "+mCityName);
        initializeField();
    }

    /*
    * The GetWeather function
    * get the current weather with the forecast api
    */
    private class GetWeather extends AsyncTask<Object, Boolean, String> {
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
            mWeekSummary.setText(daily.getString("summary"));

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
            if("snow".equalsIgnoreCase(currently.getString("icon")))
                { mMainLayout.addView(new SnowFallView(MainActivity.this), mParams); }
            else if("rain".equalsIgnoreCase(currently.getString("icon")))
                { mMainLayout.addView(new RainFallView(MainActivity.this), mParams); }

            return current;
        }

        private void updateDisplay() {
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
            scrollToFunc(mHandler, mScrollView, currentDayButton);

            mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getTemperature()));
            mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + " it will be");
            mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getCurrent().getIcon()));

            //Setting the location to the current location of the device because the api only provide the timezone as location
            mLocationLabel.setText(mCityName);
            Log.i(TAG, "the City Name: "+mCityName);

            mHumidityValue.setText(String.format(Locale.getDefault(), "%.2f%%", mWeather.getCurrent().getHumidity()));
            mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getCurrent().getPrecipChance()));
            mSummaryLabel.setText(mWeather.getCurrent().getSummary());

            Drawable drawable = ContextCompat.getDrawable(MainActivity.this, mWeather.getCurrent().getIconId());
            mIconImageView.setImageDrawable(drawable);
        }

        private void updateDisplay(String jsonData) throws JSONException {
            mWeather.setCurrent(getCurrentWeather(jsonData));
            mWeather.setDay(getDailyWeather(jsonData));
            mWeather.setHour(getHourlyWeather(jsonData));
            updateDisplay();
        }

        @Override
        protected String doInBackground(Object[] params) {
            mCallHelper.call();
            return mCallHelper.getJsonData();
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if(!jsonData.isEmpty()) {
                try {
                    updateDisplay(jsonData);
                } catch (JSONException e) {
                    Log.e(TAG, "Exception: "+ e);
                }
            }
        }
    }

    private String getLocationName(double latitude, double longitude) {
        String cityInfoBuilder = "";
        try {
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                cityInfoBuilder = String.format(Locale.getDefault(), "%s, %s",
                        addresses.get(0).getLocality(),
                        addresses.get(0).getCountryName());
            }

        } catch (IOException e) {
            Log.e(TAG, "Error message: " + e);
        }

        return cityInfoBuilder;
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
            mCallHelper.setLatitude(location.getLatitude());
            mCallHelper.setLongitude(location.getLongitude());
            runOnUiThread(() -> new GetWeather().execute());
            mCityName = getLocationName(location.getLatitude(), location.getLongitude());
            Log.i(TAG, "City Name: "+mCityName);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Not needed for now
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
        mCallHelper.setLatitude(location.getLatitude());
        mCallHelper.setLongitude(location.getLongitude());
        runOnUiThread(() -> new GetWeather().execute());

        mCityName = getLocationName(location.getLatitude(), location.getLongitude());
        Log.i(TAG, "City Name: "+mCityName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        runOnUiThread(() -> new GetWeather().execute());
        Log.i("LIFECYCLE", "OnResume METHOD");
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

        Log.i("LIFECYCLE", "OnPause METHOD");
    }

    private void initializeField() {
        mLocationLabel.setText(mSharedPreferences.getString(LAST_KNOW_LOCATION,
                getLocationName(mLatitude, mLongitude)));

        mTemperatureLabel.setText(mSharedPreferences.getString(LAST_KNOW_TEMPERATURE, "--"));

        mSummaryLabel.setText(mSharedPreferences.getString(KEY_SUMMARY,
                getResources().getString(R.string.default_weather_summary)));

        if (mSharedPreferences.contains(ICON_KEY)) {
            mIconImageView.setImageResource(mSharedPreferences.getInt(ICON_KEY, 0));
        }

        mHumidityValue.setText(mSharedPreferences.getString(HUMIDITY_KEY, "--"));
        mPrecipValue.setText(mSharedPreferences.getString(PRECIP_KEY, "--"));
        mTimeLabel.setText(mSharedPreferences.getString(TIME_OF_LAST_KNOW_TEMP, "---"));

        setupScrollView();

        Intent intentFromHourly = getIntent();
        Parcelable[] parcelables = intentFromHourly.getParcelableArrayExtra(MainActivity.HOURLY_WEATHER);

        if(parcelables != null) {
            Log.i("HOUR", "" + parcelables.length);
            mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);
            mHourlyInfo[0] = intentFromHourly.getStringArrayExtra(MainActivity.HOURLY_INFO)[0];
            mHourlyInfo[1] = intentFromHourly.getStringArrayExtra(MainActivity.HOURLY_INFO)[1];
        }
    }

    private void scrollToFunc(Handler handler, HorizontalScrollView scrollView, Button button) {
        handler.post(() -> {
            scrollView.scrollTo(button.getLeft(), button.getTop());
            scrollView.setSmoothScrollingEnabled(true);
            button.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = button;
        });
    }

    private void setupScrollView() {
        mScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        final Button mondayButton = (Button) findViewById(R.id.monday);
        final Button tuesdayButton = (Button) findViewById(R.id.tuesday);
        final Button wednesdayButton = (Button) findViewById(R.id.wednesday);
        final Button thursdayButton = (Button) findViewById(R.id.thursday);
        final Button fridayButton = (Button) findViewById(R.id.friday);
        final Button saturdayButton = (Button) findViewById(R.id.saturday);
        final Button sundayButton = (Button) findViewById(R.id.sunday);

        mHandler = new Handler();
        Calendar calendar = Calendar.getInstance();
        HashMap<String, Integer> dayOfTheWeek = (HashMap) calendar.getDisplayNames(Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.getDefault());

        String currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        final View.OnClickListener buttonListener = (view) -> {
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
            view.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = (Button) view;
            showWeatherByDay(((Button) view).getText().toString());
        };

        for (String dayName : dayOfTheWeek.keySet()) {
            if(dayOfTheWeek.get(dayName) == Calendar.MONDAY) {
                mondayButton.setText(dayName);
                mondayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, mondayButton);
                    mondayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = mondayButton;
                }
            }
            if(dayOfTheWeek.get(dayName) == Calendar.TUESDAY) {
                tuesdayButton.setText(dayName);
                tuesdayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, tuesdayButton);
                    tuesdayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = tuesdayButton;
                }
            }
            if(dayOfTheWeek.get(dayName) == Calendar.WEDNESDAY) {
                wednesdayButton.setText(dayName);
                wednesdayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, wednesdayButton);
                    wednesdayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = wednesdayButton;
                }
            }

            if(dayOfTheWeek.get(dayName) == Calendar.THURSDAY) {
                thursdayButton.setText(dayName);
                thursdayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, thursdayButton);
                    thursdayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = thursdayButton;
                }
            }
            if(dayOfTheWeek.get(dayName) == Calendar.FRIDAY) {
                fridayButton.setText(dayName);
                fridayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, fridayButton);
                    fridayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = fridayButton;
                }
            }
            if(dayOfTheWeek.get(dayName) == Calendar.SATURDAY) {
                saturdayButton.setText(dayName);
                saturdayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, saturdayButton);
                    saturdayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = saturdayButton;
                }
            }
            if(dayOfTheWeek.get(dayName) == Calendar.SUNDAY) {
                sundayButton.setText(dayName);
                sundayButton.setOnClickListener(buttonListener);
                if(dayName.equalsIgnoreCase(currentDay)) {
                    scrollToFunc(mHandler, mScrollView, sundayButton);
                    sundayButton.setText(getResources().getString(R.string.today_lobel));
                    currentDayButton = sundayButton;
                }
            }
        }

    }

    private void showWeatherByDay(String dayName) {
        if(mWeather.getDay().length > 0) {
            for(Day day : mWeather.getDay()) {
                if(day.getDayOfTheWeek().equalsIgnoreCase(dayName)) {
                    mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", day.getTemperatureMax()));
                    mTimeLabel.setText(dayName);
                    mHumidityValue.setText(String.format(Locale.getDefault(), "%.2f%%", day.getHumidity()));
                    mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", day.getPrecipChance()));
                    mSummaryLabel.setText(day.getSummary());

                    Drawable drawable = ContextCompat.getDrawable(MainActivity.this, day.getIconId());
                    mIconImageView.setImageDrawable(drawable);
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(day.getIcon()));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Checking if the user cancelled, the permission
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mIsGpsPermissionOn = true;
                    mLocationRequest = createLocationRequest();
                    getLocation();
                }
            }
        }
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((long)(1200) * 1000) // Seconds, in milliseconds
                .setFastestInterval(1000); // 1 Seconds, in milliseconds
    }
}
