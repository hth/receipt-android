package com.receiptofi.checkout;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.receiptofi.checkout.fragments.PrefFragment;
import com.receiptofi.checkout.fragments.ProfileFragment;

/**
 * Created by PT on 4/9/15.
 */
public class PreferencesTabActivity extends FragmentActivity implements ActionBar.TabListener {
    CollectionPagerAdapter mCollectionPagerAdapter;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_activity);

        mCollectionPagerAdapter = new CollectionPagerAdapter(
                getSupportFragmentManager());

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                    @Override

                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }

                });
        for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(mCollectionPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    public class CollectionPagerAdapter extends FragmentPagerAdapter {

        final int NUM_ITEMS = 3; // number of tabs

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0){
                return new ProfileFragment();
            } else if(i == 1){
                return new PrefFragment();
            } else {
                Fragment fragment = new TabFragment();
                Bundle args = new Bundle();
                args.putInt(TabFragment.ARG_OBJECT, i);
                fragment.setArguments(args);
                return fragment;
            }
        }

        @Override
        public int getCount() {

            return NUM_ITEMS;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabLabel = null;
            switch (position) {
                case 0:
                    tabLabel = getString(R.string.profile);
                    break;
                case 1:
                    tabLabel = getString(R.string.preferences);
                    break;
                case 2:
                    tabLabel = getString(R.string.billing);
                    break;

            }

            return tabLabel;

        }
    }

    public static class TabFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            int position = args.getInt(ARG_OBJECT);
            int tabLayout = 0;
            switch (position) {
                case 2:
                    tabLayout = R.layout.tab3;
                    break;
            }

            View rootView = inflater.inflate(tabLayout, container, false);
            return rootView;

        }

    }

}