package com.darelbitsy.dbweather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.HourAdapter;
import com.darelbitsy.dbweather.helper.WeatherCallHelper;
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


    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;

    private WeatherApi mWeather;
    private ColorManager mColorPicker = new ColorManager();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mIsGpsPermissionOn;
    private String mCityName;

    @BindView(R2.id.activity_main) RelativeLayout mMainLayout;

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
    private WeatherCallHelper mCallHelper;
    private RelativeLayout.LayoutParams mParams;
    private String currentDayName;
    private DatabaseOperation mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidThreeTen.init(this);
        mDatabase = new DatabaseOperation(this);
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
        mCallHelper = new WeatherCallHelper(this, mDatabase);

        mParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        mWeather = new WeatherApi();
        //Configuring the google api client
        mLocationRequest = createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mIsGpsPermissionOn = false;

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else { mIsGpsPermissionOn = true; }

        mLongitude = mCallHelper.getLongitude();
        mLatitude = mCallHelper.getLatitude();


        if((mLatitude > 0) && (mLongitude > 0) ) {
            mCityName = getLocationName(mLatitude, mLongitude);
        }
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
            if("snow".equalsIgnoreCase(currently.getString("icon"))) {
                if(mMainLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                    mMainLayout.addView(new SnowFallView(MainActivity.this), mParams);
                }
            }
            else if("rain".equalsIgnoreCase(currently.getString("icon"))) {
                if(mMainLayout.findViewById(RainFallView.VIEW_ID) == null) {
                    mMainLayout.addView(new RainFallView(MainActivity.this), mParams);
                }
            } else {
                if(mMainLayout.findViewById(RainFallView.VIEW_ID) != null) {
                    mMainLayout.removeView(mMainLayout.findViewById(RainFallView.VIEW_ID));
                }
                if (mMainLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                    mMainLayout.removeView(mMainLayout.findViewById(SnowFallView.VIEW_ID));
                }
            }
            mWeekSummary.setText(current.getWeekSummary());
            return current;
        }

        private void updateDisplay() {
            if (mWeather.getDay().length > 0) { setupDayScrollView(); }
            if(mWeather.getHour().length > 0) { setupHourScrollView(); }
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
            scrollToFunc(mHandler, mScrollView, currentDayButton);

            mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getTemperature()));
            mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + " it will be");
            mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getCurrent().getIcon()));

            //Setting the location to the current location of the device because the api only provide the timezone as location
            mLocationLabel.setText(mCityName);
            Log.i(TAG, "the City Name: "+mCityName);

            mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getCurrent().getHumidity()));
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
            String json = jsonData.isEmpty() ? mDatabase.getJsonData() : jsonData;
            if(json != null && !json.isEmpty()) {
                try {
                    updateDisplay(json);
                    if(!jsonData.isEmpty()) {
                        mDatabase.setJsonData(jsonData);
                    }
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
        Log.i("LIFECYCLE", "OnResume METHOD");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if(mWeather.getCurrent() != null) {
            mDatabase.saveCurrentWeather(mWeather.getCurrent(), mCallHelper);
        }
        if (mWeather.getDay()[7] != null) {
            mDatabase.saveDailyWeather(mWeather.getDay());
        }
        if (mWeather.getHour()[47] != null) {
            mDatabase.saveHourlyWeather(mWeather.getHour());
        }
        Log.i("LIFECYCLE", "OnPause METHOD");
    }

    private void initializeField() {
        mWeather.setCurrent(mDatabase.getCurrentWeatherFromDatabase());
        mWeather.setDay(mDatabase.getDailyWeatherFromDatabase());
        mWeather.setHour(mDatabase.getHourlyWeatherFromDatabase());

        mLocationLabel.setText(mWeather.getCurrent().getCityName() == null ? getLocationName(mLatitude, mLongitude)
                : mWeather.getCurrent().getCityName());

        mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getTemperature()));
        mSummaryLabel.setText(mWeather.getCurrent().getSummary());
        mWeekSummary.setText(mWeather.getCurrent().getWeekSummary());

        mIconImageView.setImageResource(mWeather.getCurrent().getIconId());

        mHumidityValue.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getHumidity()));
        mPrecipValue.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getPrecipChance()));
        if(mWeather.getCurrent().getTime() > 0) {
            mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + ", it was");
        }

        setupDayScrollView();
        setupHourScrollView();
    }

    private void scrollToFunc(Handler handler, HorizontalScrollView scrollView, Button button) {
        handler.post(() -> {
            scrollView.scrollTo(button.getLeft(), button.getTop());
            scrollView.setSmoothScrollingEnabled(true);
            button.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = button;
        });
    }

    private void setupHourScrollView() {
        final RecyclerView hourlyForecastRecyclerView = (RecyclerView) findViewById(R.id.hourlyRecyclerView);

        if(mWeather.getHour()[47] != null) {
            HourAdapter hourAdapter = new HourAdapter(Arrays.copyOf(mWeather.getHour(),
                    mWeather.getHour().length,
                    Hour[].class));
            hourlyForecastRecyclerView.setAdapter(hourAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            hourlyForecastRecyclerView.setLayoutManager(layoutManager);
            hourlyForecastRecyclerView.setHasFixedSize(true);
        }
    }

    private void setupDayScrollView() {
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

        currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        final View.OnClickListener buttonListener = (view) -> {
            currentFocusedButton.setBackgroundColor(Color.parseColor("#30ffffff"));
            view.setBackgroundColor(Color.parseColor("#80ffffff"));
            currentFocusedButton = (Button) view;
            showWeatherByDay(((Button) view).getText().toString());
        };

        if(mWeather.getDay()[7] != null) {
            int count = 0;
            Button[] listOfButton = {mondayButton,
                    tuesdayButton,
                    wednesdayButton,
                    thursdayButton,
                    fridayButton,
                    saturdayButton,
                    sundayButton};

            for(Day day : mWeather.getDay()) {
                if(count < 7)  {
                    listOfButton[count].setOnClickListener(buttonListener);
                    if (count == 1) {
                        listOfButton[count].setText(getResources()
                                .getString(R.string.tomorrow_label));
                        count++;
                        continue;
                    }
                    if (currentDayName.equalsIgnoreCase(day.getDayOfTheWeek())) {
                        scrollToFunc(mHandler,
                                mScrollView,
                                listOfButton[count]);
                        currentDayButton = listOfButton[count];
                        listOfButton[count].setText(getResources().getString(R.string.today_label));
                        count++;
                        continue;
                    }
                    else {
                        listOfButton[count].setText(day.getDayOfTheWeek());
                        count++;
                        continue;
                    }
                }
                else { break; }
            }
        } else {
            HashMap<String, Integer> dayOfTheWeek = (HashMap) calendar.getDisplayNames(Calendar.DAY_OF_WEEK,
                    Calendar.LONG,
                    Locale.getDefault());

            for (String dayName : dayOfTheWeek.keySet()) {
                if(dayOfTheWeek.get(dayName) == Calendar.MONDAY) {
                    mondayButton.setText(dayName);
                    mondayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, mondayButton);
                        mondayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = mondayButton;
                    }
                }
                if(dayOfTheWeek.get(dayName) == Calendar.TUESDAY) {
                    tuesdayButton.setText(dayName);
                    tuesdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, tuesdayButton);
                        tuesdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = tuesdayButton;
                    }
                }
                if(dayOfTheWeek.get(dayName) == Calendar.WEDNESDAY) {
                    wednesdayButton.setText(dayName);
                    wednesdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, wednesdayButton);
                        wednesdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = wednesdayButton;
                    }
                }
                if(dayOfTheWeek.get(dayName) == Calendar.THURSDAY) {
                    thursdayButton.setText(dayName);
                    thursdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, thursdayButton);
                        thursdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = thursdayButton;
                    }
                }
                if(dayOfTheWeek.get(dayName) == Calendar.FRIDAY) {
                    fridayButton.setText(dayName);
                    fridayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, fridayButton);
                        fridayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = fridayButton;
                    }
                }
                if(dayOfTheWeek.get(dayName) == Calendar.SATURDAY) {
                    saturdayButton.setText(dayName);
                    saturdayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, saturdayButton);
                        saturdayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = saturdayButton;
                    }
                }
                if(dayOfTheWeek.get(dayName) == Calendar.SUNDAY) {
                    sundayButton.setText(dayName);
                    sundayButton.setOnClickListener(buttonListener);
                    if(dayName.equalsIgnoreCase(currentDayName)) {
                        scrollToFunc(mHandler, mScrollView, sundayButton);
                        sundayButton.setText(getResources().getString(R.string.today_label));
                        currentDayButton = sundayButton;
                    }
                }
            }
        }
    }

    private void showWeatherByDay(String dayName) {
        if(mWeather.getDay().length > 0) {
            for(Day day : mWeather.getDay()) {
                if (dayName.equalsIgnoreCase(getResources()
                        .getString(R.string.today_label))) {

                    mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mWeather.getCurrent().getTemperature()));
                    mTimeLabel.setText("At " + mWeather.getCurrent().getFormattedTime() + " it will be");
                    mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getCurrent().getHumidity()));
                    mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getCurrent().getPrecipChance()));
                    mSummaryLabel.setText(mWeather.getCurrent().getSummary());

                    Drawable drawable = ContextCompat.getDrawable(MainActivity.this, mWeather.getCurrent().getIconId());
                    mIconImageView.setImageDrawable(drawable);
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getCurrent().getIcon()));
                }

                if(dayName.equalsIgnoreCase(getResources().getString(R.string.tomorrow_label))) {
                    mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", mWeather.getDay()[1].getTemperatureMax()));
                    mTimeLabel.setText(dayName);
                    mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getDay()[1].getHumidity()));
                    mPrecipValue.setText(String.format(Locale.getDefault(), "%d%%", mWeather.getDay()[1].getPrecipChance()));
                    mSummaryLabel.setText(mWeather.getDay()[1].getSummary());

                    Drawable drawable = ContextCompat.getDrawable(MainActivity.this, mWeather.getDay()[1].getIconId());
                    mIconImageView.setImageDrawable(drawable);
                    mMainLayout.setBackgroundResource(mColorPicker.getBackgroundColor(mWeather.getDay()[1].getIcon()));
                }

                if(day.getDayOfTheWeek().equalsIgnoreCase(dayName)
                        && !day.getDayOfTheWeek().equalsIgnoreCase(currentDayName)) {
                    mTemperatureLabel.setText(String.format(Locale.getDefault(), "%d", day.getTemperatureMax()));
                    mTimeLabel.setText(dayName);
                    mHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", day.getHumidity()));
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
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                && (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            mIsGpsPermissionOn = true;
            mLocationRequest = createLocationRequest();
            getLocation();
        }
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((long)(1200) * 1000) // Seconds, in milliseconds
                .setFastestInterval(1000); // 1 Seconds, in milliseconds
    }
}
