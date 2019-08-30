package com.lbs.cheng.lbscampus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by cheng on 2019/2/10.
 */

public class FragmentAdapter extends FragmentPagerAdapter{
    private List<Fragment> fragments;
    private List<String> strings;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments , List<String> strings) {
        super(fm);
        this.fragments = fragments;
        this.strings = strings;
    }

    //返回Tab对应的Fragment
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    //返回Tab数目
    @Override
    public int getCount() {
        return strings.size();
    }

    //返回Tab文字
    @Override
    public CharSequence getPageTitle(int position) {
        return strings.get(position);
    }
}
