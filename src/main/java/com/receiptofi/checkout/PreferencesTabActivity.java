package com.receiptofi.checkout;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.receiptofi.checkout.fragments.BillingFragment;
import com.receiptofi.checkout.fragments.PrefFragment;
import com.receiptofi.checkout.fragments.ProfileFragment;
import com.receiptofi.checkout.utils.Constants;

/**
 * Created by PT on 4/9/15.
 */
public class PreferencesTabActivity extends FragmentActivity implements ActionBar.TabListener, DialogInterface.OnDismissListener{
    private static final String TAG = PreferencesTabActivity.class.getSimpleName();
    private CollectionPagerAdapter mCollectionPagerAdapter;
    private ViewPager mViewPager;
    private Fragment prefFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate Preference");
        setContentView(R.layout.preferences_activity);

        mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                }
        );

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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d(TAG, "  -----------    onDismiss    -----------");
//        if(prefFragment != null){
//            ((PrefFragment)prefFragment).updateHandler.sendEmptyMessageDelayed(PrefFragment.EXPENSE_TAG_UPDATED,
//                    Constants.EXPANSE_TAG_UPDATE_DELAY);
//        }
    }

    public class CollectionPagerAdapter extends FragmentPagerAdapter {
        /** Number of tabs. */
        final int NUM_ITEMS = 3;
        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
//            if (i == 0) {
////                return new ProfileFragment();
//            } else if (i == 1) {
//                prefFragment = new PrefFragment();
//                return prefFragment;
//            } else {
//                return new BillingFragment();
//            }
            return null;
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
}
