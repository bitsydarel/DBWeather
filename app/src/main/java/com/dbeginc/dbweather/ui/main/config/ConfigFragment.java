package com.dbeginc.dbweather.ui.main.config;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;

/**
 * Created by darel on 28.05.17.
 * Configuration Activity
 */

public class ConfigFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewDataBinding inflate = DataBindingUtil.inflate(inflater, R.layout.config_tab_layout, container, false);
        return inflate.getRoot();
    }
}
