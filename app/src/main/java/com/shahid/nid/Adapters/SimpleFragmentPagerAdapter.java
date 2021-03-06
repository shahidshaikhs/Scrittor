package com.shahid.nid.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shahid.nid.Fragments.CategoriesFragment;
import com.shahid.nid.Fragments.MainFragment;
import com.shahid.nid.Fragments.StarredFragment;


/**
 * Created by MJ on 8/22/2016.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    public SimpleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new CategoriesFragment();
        } else if (position == 1) {
            return new MainFragment();
        } else
            return new StarredFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
