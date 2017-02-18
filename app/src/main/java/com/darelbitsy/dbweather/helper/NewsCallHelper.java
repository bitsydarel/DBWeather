package com.darelbitsy.dbweather.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.alert.AlertDialogFragment;
import com.darelbitsy.dbweather.alert.NetworkAlertDialogFragment;
import com.darelbitsy.dbweather.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Darel Bitsy on 16/02/17.
 */

public class NewsCallHelper {
    private Activity mActivity;
    private Context mContext;
    private DatabaseOperation mDatabase;
    private final String urlRequest
            = "https://newsapi.org/v1/articles?source=%s&sortBy=%s&apiKey=e6e9d4a3f7f24a7a8d16f496df95126f";
    private final String[] listOfSources = {
            "bbc-news",
            "cnn",
            "bbc-sport",
            "google-news",
            "hacker-news",
            "sky-sports-news"
    };
    private List<String> mNewsList;

    public NewsCallHelper(Activity activity, DatabaseOperation database) {
        mActivity = activity;
        mDatabase = database;
        mNewsList = new LinkedList<>();
        call();
    }

    public NewsCallHelper(Context context, DatabaseOperation database) {
        mContext = context;
        mDatabase = database;
        mNewsList = new LinkedList<>();
        call();
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

    void call() {
        if (isNetworkAvailable()) {
            OkHttpClient httpClient = new OkHttpClient();

            for (String news : getNewsLinks()) {
                Request httpRequest = new Request.Builder()
                        .url(news)
                        .build();

                Call apiCall = httpClient.newCall(httpRequest);
                apiCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        alertUserAboutError();
                        Log.i(MainActivity.TAG, e.getMessage() + " this error happened during the call");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (response.isSuccessful()) {
                                setJsonData(response.body().string());
                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException e) {
                            Log.e(MainActivity.TAG, "Exception caught: ", e);
                        }
                    }
                });
            }
        } else {
            alertUserAboutNetworkError();
        }
    }

    private List<String> getNewsLinks() {
        List<String> urls = new ArrayList<>();
        for (String source : listOfSources) {
            if ("sky-sports-news".equalsIgnoreCase(source)) {
                urls.add(String.format(Locale.ENGLISH, urlRequest, source, "latest"));
            } else {
                urls.add(String.format(Locale.ENGLISH, urlRequest, source, "top"));
            }
        }
        return urls;
    }

    private void setJsonData(String jsonData) {
        mNewsList.add(jsonData);
    }

    List<String> getNewsFromJson() {
        return mNewsList;
    }
}
