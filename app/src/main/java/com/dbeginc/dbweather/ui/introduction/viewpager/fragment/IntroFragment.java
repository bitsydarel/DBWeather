package com.dbeginc.dbweather.ui.introduction.viewpager.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by darel on 01.05.17.
 * Intro Fragment representing
 * introduction page
 */

public class IntroFragment extends Fragment {

    private int fragmentLayout;

    private static final String INTRO_LAYOUT = "intro_layout";

    public static IntroFragment newInstance(@LayoutRes final int backgroundRes) {

        final IntroFragment fragment = new IntroFragment();

        final Bundle data = new Bundle();

        data.putInt(INTRO_LAYOUT, backgroundRes);

        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentLayout = getArguments().getInt(INTRO_LAYOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(fragmentLayout, container, false);
    }
}
