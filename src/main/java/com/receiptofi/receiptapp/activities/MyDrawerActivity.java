package com.receiptofi.receiptapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ShoppingPlaceActivityF;
import com.receiptofi.receiptapp.fragments.BillingFragment;
import com.receiptofi.receiptapp.fragments.ExpenseTagFragment;
import com.receiptofi.receiptapp.fragments.HomeFragment1;
import com.receiptofi.receiptapp.fragments.NotificationFragment;
import com.receiptofi.receiptapp.fragments.SettingFragment;
import com.receiptofi.receiptapp.fragments.SubscriptionFragment;

public class MyDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ImageView iv_profile;
    private TextView tv_login, tv_name, tv_email;
    public NotificationFragment notificationFragment;
    public ExpenseTagFragment expenseTagFragment;
    public BillingFragment billingFragment;
    public SubscriptionFragment subscriptionFragment;
    public SettingFragment settingFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_drawer);

        HomeFragment1 scanfragment = HomeFragment1.newInstance("", "");
        notificationFragment = NotificationFragment.newInstance("", "");
        expenseTagFragment = ExpenseTagFragment.newInstance("", "");
        billingFragment = new BillingFragment();
        subscriptionFragment = new SubscriptionFragment();
        settingFragment = new SettingFragment();
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, scanfragment).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LinearLayout mParent = (LinearLayout) navigationView.getHeaderView(0);
        iv_profile = (ImageView) mParent.findViewById(R.id.iv_profile);
        tv_login = (TextView) mParent.findViewById(R.id.tv_login);
        tv_name = (TextView) mParent.findViewById(R.id.tv_name);
        tv_email = (TextView) mParent.findViewById(R.id.tv_email);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_home: {
                HomeFragment1 homeFragment1 = HomeFragment1.newInstance("", "");
                FragmentManager fragmentManager = getFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layout, homeFragment1).commit();
                break;
            }
            case R.id.nav_shopping_place:
                Intent sendIntent = new Intent(this, ShoppingPlaceActivityF.class);
                startActivity(sendIntent);
                break;
            case R.id.nav_notifications:
                addFragment(notificationFragment);
                break;
            case R.id.nav_tag_expense:
                addFragment(expenseTagFragment);
                break;
            case R.id.nav_billing_history:
                addFragment(billingFragment);
                break;

            case R.id.nav_app_subscription:
                addFragment(subscriptionFragment);
                break;
            case R.id.nav_app_setting:
                addFragment(settingFragment);
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addFragment( Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName()).commit();
    }
}
