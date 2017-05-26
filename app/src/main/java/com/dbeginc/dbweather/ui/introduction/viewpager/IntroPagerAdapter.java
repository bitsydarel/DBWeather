package com.dbeginc.dbweather.ui.introduction.viewpager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dbeginc.dbweather.ui.introduction.PagePresenter;
import com.dbeginc.dbweather.ui.introduction.viewpager.fragment.IntroFragment;

/**
 * Created by Bitsy Darel on 01.05.17.
 * Intro App ViewPager
 */

public class IntroPagerAdapter extends FragmentStatePagerAdapter {

    private final PagePresenter pagePresenter;

    public IntroPagerAdapter(@NonNull final FragmentManager fm, @NonNull final PagePresenter pagePresenter) {
        super(fm);
        this.pagePresenter = pagePresenter;
    }

    @Override
    public Fragment getItem(final int position) {
        return IntroFragment.newInstance(pagePresenter.getPageLayout(position));
    }

    @Override
    public int getCount() {
        return pagePresenter.getPageCount();
    }
}
