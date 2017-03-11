package com.darelbitsy.dbweather.ui.alert;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 11/03/17.
 * Dialog fragment that show description
 * about the news the user clicked
 */

public class NewsDialogFragment extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
    }
}
