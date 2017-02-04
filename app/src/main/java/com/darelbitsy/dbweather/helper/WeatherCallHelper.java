package com.darelbitsy.dbweather.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.alert.AlertDialogFragment;
import com.darelbitsy.dbweather.alert.NetworkAlertDialogFragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Darel Bitsy on 19/01/17.
 */

public class WeatherCallHelper {
    private Context mContext;
    private List<String> supportedLang = Arrays.asList("ar","az","be","bs","ca","cs","de","el","en","es",
            "et","fr","hr","hu","id","it","is","kw","nb","nl","pl","pt","ru",
            "sk","sl","sr","sv","tet","tr","uk","x-pig-latin","zh","zh-tw");

    private static final String TAG = "WeatherApiCall";
    private DatabaseOperation mDatabase;
    private String mJsonData;

    private Double mLatitude, mLongitude;
    private Activity mActivity;
    private String userLang = Locale.getDefault().getLanguage();

    public WeatherCallHelper(Activity activity, DatabaseOperation database) {
        mActivity = activity;
        mDatabase = database;
        mLatitude = getLatitude();
        mLongitude = getLongitude();
        mJsonData = "";
        call();
    }

    public WeatherCallHelper(Context context, DatabaseOperation database) {
        mContext = context;
        mDatabase = database;
        mLatitude = getLatitude();
        mLongitude = getLongitude();
        mJsonData = "";
        call();
    }

    public String getJsonData() { return mJsonData; }
    private void setJsonData(String jsonData) {
        this.mJsonData = jsonData;
    }
    private String getUserLang() {
        String api;
        //Checking if user device language is supported by the api if not english language will be used.
        String language = supportedLang.contains(userLang) ? ("?lang="+userLang ) : null;
        //Api Key
        String apiKey = "07aadf598548d8bb35d6621d5e3b3c7b";
        if(language == null) {
            api = String.format(Locale.ENGLISH, "https://api.darksky.net/forecast/%s/%f,%f?units=auto", apiKey,
                    mLatitude,
                    mLongitude);
        } else {
            api = String.format(Locale.ENGLISH, "https://api.darksky.net/forecast/%s/%f,%f%s&units=auto" , apiKey,
                    mLatitude,
                    mLongitude,
                    language);
        }
        return api;
    }

    public void setLatitude(double latitude) { mLatitude = latitude; }
    public void setLongitude(double longitude) { mLongitude = longitude; }

    //Get the last know latitude or give a default value
    public double getLatitude() {
        if(mLatitude == null && mDatabase.getCoordinates()[0] == null) {
            return -4.8062;
        }
        return mLatitude == null ? mDatabase.getCoordinates()[0] : mLatitude;
    }

    //Get the last know longitude or give a default value
    public double getLongitude() {
        if(mLongitude == null && mDatabase.getCoordinates()[1] == null) {
            return -11.8319;
        }
        return mLongitude == null ? mDatabase.getCoordinates()[1] : mLongitude;
    }

    private boolean isNetworkAvailable() {
        NetworkInfo networkInfo;
        if (mActivity != null) {
            ConnectivityManager manager = (ConnectivityManager)
                    mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = manager.getActiveNetworkInfo();
        } else {
            ConnectivityManager manager = (ConnectivityManager)
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = manager.getActiveNetworkInfo();
        }
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutNetworkError() {
        NetworkAlertDialogFragment dialog = new NetworkAlertDialogFragment();
        dialog.show(mActivity.getFragmentManager(), "network_error_dialog");
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(mActivity.getFragmentManager(), "error_dialog");
    }

    public String call() {
            if (isNetworkAvailable()) {
                OkHttpClient httpClient = new OkHttpClient();
                Request httpRequest = new Request.Builder()
                        .url(getUserLang())
                        .build();

                Call apiCall = httpClient.newCall(httpRequest);
                apiCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        alertUserAboutError();
                        Log.i("dbweather", e.getMessage() + " this error happened during the call");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (response.isSuccessful()) {
                                setJsonData(response.body().string());
                                Log.v(TAG, mJsonData);
                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception caught: ", e);
                        }
                    }
                });
            } else {
                alertUserAboutNetworkError();
            }
        return mJsonData;
    }
}
