package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.fragments.HomeFragment1;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.model.ProfileModel;
import com.receiptofi.receiptapp.service.gcm.RegistrationIntentService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.utils.db.ProfileUtils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ReceiptofiApplication receiptofiApplication;
    private HomeFragment1 homeFragment;
    private static final String TAG = HomeActivity.class.getSimpleName();

    //--- Bottom nav button
    public LinearLayout llbottom_nav_home,bottom_nav_receipts,bottom_nav_split,bottom_nav_shoping_list,bottom_nav_me;
    public  Button btn_home,btn_receipts,btn_shoppingList,btn_split,btn_me;
    private  Drawable drawablehomered,drawablereceiptred,drawablesplitred,drawableshoppinglistred,drawablemered,drawablehomeblue,drawablereceiptblue,drawablesplitblue,drawableshoppinglistblue,drawablemeblue;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReceiptofiApplication.homeActivityResumed();
        receiptofiApplication = (ReceiptofiApplication) getApplicationContext();
        AppUtils.setHomePageContext(this);

        homeFragment = HomeFragment1.newInstance("", "");

        ProfileModel profileModel = ProfileUtils.getProfile();
        String name = profileModel != null ? profileModel.getName() : "";
        String mail = profileModel != null ? profileModel.getMail() : UserUtils.getEmail();



        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, homeFragment,"Home1");
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
        receiptofiApplication.setCurrentActivity(this);

    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
        Log.d(TAG, "executing onDestroy");
        ReceiptofiApplication.homeActivityPaused();
        AppUtils.setHomePageContext(null);
        Log.d(TAG, "Done onDestroy!!");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_home_bottom_navigation,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_logout)
        {
            KeyValueUtils.updateValuesForKeyWithBlank(API.key.XR_AUTH);
            startActivity(new Intent(this, LaunchActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
         drawablehomered = HomeActivity.this.getResources().getDrawable(R.drawable.home_red);
         drawablereceiptred = HomeActivity.this.getResources().getDrawable(R.drawable.receipts_red);
         drawablesplitred = HomeActivity.this.getResources().getDrawable(R.drawable.split_red);
         drawableshoppinglistred = HomeActivity.this.getResources().getDrawable(R.drawable.shopping_list_red);
         drawablemered = HomeActivity.this.getResources().getDrawable(R.drawable.me_red);

         drawablehomeblue = HomeActivity.this.getResources().getDrawable(R.drawable.home_blue);
         drawablereceiptblue = HomeActivity.this.getResources().getDrawable(R.drawable.receipts_blue);
         drawablesplitblue = HomeActivity.this.getResources().getDrawable(R.drawable.split_blue);
         drawableshoppinglistblue = HomeActivity.this.getResources().getDrawable(R.drawable.shopping_list_blue);
         drawablemeblue = HomeActivity.this.getResources().getDrawable(R.drawable.me_blue);

        llbottom_nav_home = (LinearLayout)findViewById(R.id.bottom_nav_home);
        bottom_nav_receipts = (LinearLayout)findViewById(R.id.bottom_nav_receipts);
        bottom_nav_split = (LinearLayout)findViewById(R.id.bottom_nav_split);
        bottom_nav_shoping_list = (LinearLayout)findViewById(R.id.bottom_nav_shoping_list);
        bottom_nav_me = (LinearLayout)findViewById(R.id.bottom_nav_me);

        btn_home = (Button)findViewById(R.id.btn_home);
        btn_receipts = (Button)findViewById(R.id.btn_receipts);
        btn_shoppingList = (Button)findViewById(R.id.btn_shoppingList);
        btn_split = (Button)findViewById(R.id.btn_split);
        btn_me = (Button)findViewById(R.id.btn_me);

        btn_home.setOnClickListener(this);
        btn_receipts.setOnClickListener(this);
        btn_shoppingList.setOnClickListener(this);
        btn_split.setOnClickListener(this);
        btn_me.setOnClickListener(this);
        homebuttonClick();
    }

    private void clearReferences() {
        Activity currActivity = receiptofiApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            receiptofiApplication.setCurrentActivity(null);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                showToastMsg("Certain feature would not be available due to missing Google Play Service.",
                        SuperToast.Background.RED,
                        SuperToast.Duration.EXTRA_LONG);
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    public void showToastMsg(String msg, int backgroundColor, int duration) {
        SuperActivityToast superActivityToast = new SuperActivityToast(HomeActivity.this);
        superActivityToast.setText(msg);
        superActivityToast.setDuration(duration);
        superActivityToast.setBackground(backgroundColor);
        superActivityToast.setTextColor(Color.WHITE);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id)
        {
            case R.id.btn_home:
                homebuttonClick();
                break;
            case R.id.btn_me:
                btn_home.setCompoundDrawablesWithIntrinsicBounds(null,drawablehomeblue,null,null);
                btn_receipts.setCompoundDrawablesWithIntrinsicBounds(null,drawablereceiptblue,null,null);
                btn_split.setCompoundDrawablesWithIntrinsicBounds(null,drawablesplitblue,null,null);
                btn_shoppingList.setCompoundDrawablesWithIntrinsicBounds(null,drawableshoppinglistblue,null,null);
                btn_me.setCompoundDrawablesWithIntrinsicBounds(null,drawablemered,null,null);


                btn_home.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_receipts.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_split.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_shoppingList.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_me.setTextColor(HomeActivity.this.getResources().getColor(R.color.app_red));
                break;
            case R.id.btn_receipts:
                btn_home.setCompoundDrawablesWithIntrinsicBounds(null,drawablehomeblue,null,null);
                btn_receipts.setCompoundDrawablesWithIntrinsicBounds(null,drawablereceiptred,null,null);
                btn_split.setCompoundDrawablesWithIntrinsicBounds(null,drawablesplitblue,null,null);
                btn_shoppingList.setCompoundDrawablesWithIntrinsicBounds(null,drawableshoppinglistblue,null,null);
                btn_me.setCompoundDrawablesWithIntrinsicBounds(null,drawablemeblue,null,null);


                btn_home.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_receipts.setTextColor(HomeActivity.this.getResources().getColor(R.color.app_red));
                btn_split.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_shoppingList.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_me.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                break;
            case R.id.btn_shoppingList:
                btn_home.setCompoundDrawablesWithIntrinsicBounds(null,drawablehomeblue,null,null);
                btn_receipts.setCompoundDrawablesWithIntrinsicBounds(null,drawablereceiptblue,null,null);
                btn_split.setCompoundDrawablesWithIntrinsicBounds(null,drawablesplitblue,null,null);
                btn_shoppingList.setCompoundDrawablesWithIntrinsicBounds(null,drawableshoppinglistred,null,null);
                btn_me.setCompoundDrawablesWithIntrinsicBounds(null,drawablemeblue,null,null);


                btn_home.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_receipts.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_split.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_shoppingList.setTextColor(HomeActivity.this.getResources().getColor(R.color.app_red));
                btn_me.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                break;
            case R.id.btn_split:
                btn_home.setCompoundDrawablesWithIntrinsicBounds(null,drawablehomeblue,null,null);
                btn_receipts.setCompoundDrawablesWithIntrinsicBounds(null,drawablereceiptblue,null,null);
                btn_split.setCompoundDrawablesWithIntrinsicBounds(null,drawablesplitred,null,null);
                btn_shoppingList.setCompoundDrawablesWithIntrinsicBounds(null,drawableshoppinglistblue,null,null);
                btn_me.setCompoundDrawablesWithIntrinsicBounds(null,drawablemeblue,null,null);


                btn_home.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_receipts.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_split.setTextColor(HomeActivity.this.getResources().getColor(R.color.app_red));
                btn_shoppingList.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                btn_me.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
                break;
        }

    }


    //---- home button click----
    public void homebuttonClick()
    {
        btn_home.setCompoundDrawablesWithIntrinsicBounds(null,drawablehomered,null,null);
        btn_receipts.setCompoundDrawablesWithIntrinsicBounds(null,drawablereceiptblue,null,null);
        btn_split.setCompoundDrawablesWithIntrinsicBounds(null,drawablesplitblue,null,null);
        btn_shoppingList.setCompoundDrawablesWithIntrinsicBounds(null,drawableshoppinglistblue,null,null);
        btn_me.setCompoundDrawablesWithIntrinsicBounds(null,drawablemeblue,null,null);


        btn_home.setTextColor(HomeActivity.this.getResources().getColor(R.color.app_red));
        btn_receipts.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
        btn_split.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
        btn_shoppingList.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
        btn_me.setTextColor(HomeActivity.this.getResources().getColor(R.color.gray_dark));
    }
}
