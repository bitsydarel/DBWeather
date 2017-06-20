package com.dbeginc.dbweather.ui.intro.news;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.databinding.NewsIntroLayoutBinding;
import com.dbeginc.dbweather.ui.BaseFragment;

/**
 * Created by darel on 14.06.17.
 * News Intro page
 */

public class NewsIntroPage extends BaseFragment {

    private NewsIntroLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.news_intro_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.grantAccountButton.setOnClickListener(v -> permissionEvent.onNext(true));
        binding.deniedAccountButton.setOnClickListener(v -> permissionEvent.onNext(false));
    }
}
