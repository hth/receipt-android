package com.receiptofi.checkout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.andexert.library.RippleView;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.adapters.MenuListAdapter;
import com.receiptofi.checkout.fragments.BillingFragment;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.fragments.HomeFragment.OnFragmentInteractionListener;
import com.receiptofi.checkout.fragments.NotificationFragment;
import com.receiptofi.checkout.fragments.ProfileFragment;
import com.receiptofi.checkout.fragments.TagModifyFragment;
import com.receiptofi.checkout.service.DeviceService;


public class MainPageActivity extends FragmentActivity implements OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout mDrawerLayout_L;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;
    private static final String TAG = MainPageActivity.class.getSimpleName();
    public HomeFragment mHomeFragment;
    private Menu optionMenu;
    private SearchView searchView;
    private Context mContext;
//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        ReceiptofiApplication.homeActivityResumed();
        mContext = getApplicationContext();
        setupView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerLayout_L)) {
                mDrawerLayout.closeDrawer(mDrawerLayout_L);
            } else {
                mDrawerLayout.openDrawer(mDrawerLayout_L);
            }
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_refresh:
                DeviceService.getNewUpdates(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.home_page);
            //TODO: refresh main page.
//            instantiateViews();
//            bindValuesToViews();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.home_page);
            //TODO: refresh main page
//            instantiateViews();
//            bindValuesToViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_page, menu);
        menu.findItem(R.id.menu_search).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_search)
                        .colorRes(R.color.white)
                        .actionBarSize());

        menu.findItem(R.id.menu_refresh).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_refresh)
                        .colorRes(R.color.white)
                        .actionBarSize());
        optionMenu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        return true;
    }

    private void setupView() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        mDrawerLayout_L = (RelativeLayout) findViewById(R.id.navdrawer_RelativeLayout);

        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        mHomeFragment = HomeFragment.newInstance("", "");
        changeFragment(mHomeFragment);

        MenuListAdapter adapter = new MenuListAdapter(this);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                RippleView rippleView;
                switch (position) {
                    case 0:
                        mDrawerToggle.setAnimateEnabled(false);
                        drawerArrow.setProgress(1f);
                        rippleView = (RippleView)view.findViewById(R.id.ripple_View);
                        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                mDrawerLayout.closeDrawer(mDrawerLayout_L);
                                changeFragment(mHomeFragment);
                            }
                        });
                        break;
                    case 1:
                        mDrawerToggle.setAnimateEnabled(false);
                        drawerArrow.setProgress(0f);
                        rippleView = (RippleView)view.findViewById(R.id.ripple_View);
                        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                mDrawerLayout.closeDrawer(mDrawerLayout_L);
                                changeFragment(NotificationFragment.newInstance("", ""));
                            }
                        });
                        break;
                    case 2:
                        mDrawerToggle.setAnimateEnabled(true);
                        mDrawerToggle.syncState();
                        rippleView = (RippleView)view.findViewById(R.id.ripple_View);
                        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                mDrawerLayout.closeDrawer(mDrawerLayout_L);
                                changeFragment(TagModifyFragment.newInstance("", ""));
                            }
                        });

                        break;
                    case 3:
                        rippleView = (RippleView)view.findViewById(R.id.ripple_View);
                        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                changeFragment(new BillingFragment());
                                mDrawerLayout.closeDrawer(mDrawerLayout_L);
                            }
                        });
                        break;
                    case 4:
                        rippleView = (RippleView)view.findViewById(R.id.ripple_View);
                        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                mDrawerLayout.closeDrawer(mDrawerLayout_L);
                                changeFragment(new ProfileFragment());
                            }
                        });
                        break;
                }

            }
        });

    }

    private void changeFragment(Fragment targetFragment) {
        if (targetFragment == null)
            Log.i(TAG, "targetFragment is null");
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack("tag")
                .commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
//                Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
//                if (f != null) {
//                    String fragmentName = f.getClass().getName();
//                    if (fragmentName.equals(HomeFragment.class.getName())) {
//                        title.setText((CharSequence) "Home");
//                    } else if (fragmentName.equals(ImagesFragment.class.getName())) {
//                        title.setText((CharSequence) "Images");
//                    } else if (fragmentName.equals(ContactFragment.class.getName())) {
//                        title.setText((CharSequence) "Contact");
//                    } else if (fragmentName.equals(FenceCaliculateFragment.class.getName())) {
//                        title.setText((CharSequence) "Fence Calculator");
//                    } else if (fragmentName.equals(MonthlyGiveAwayFragment.class.getName())) {
//                        title.setText((CharSequence) "Monthly Give Away");
//                    } else if (fragmentName.equals(SocialFragment.class.getName())) {
//                        title.setText((CharSequence) "Social");
//                    } else if (fragmentName.equals(FenceCaliculateFragment.class.getName())) {
//                        title.setText((CharSequence) "Fence Calculator");
//                    } else if (fragmentName.equals(WebViewFragment.class.getName())) {
//                        if (((WebViewFragment) f).getCategory() == "PRODUCT") {
//                            title.setText((CharSequence) "Product");
//                        } else if (((WebViewFragment) f).getCategory() == "ORDER") {
//                            title.setText((CharSequence) "Order");
//                        } else if (((WebViewFragment) f).getCategory() == "QUOTE") {
//                            title.setText((CharSequence) "Quote");
//                        }
//                    }
            }

        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
