package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.common.ConnectionResult;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.fragments.HomeFragment1;
import com.receiptofi.receiptapp.fragments.ReceiptDetailFragment;
import com.receiptofi.receiptapp.fragments.ReceiptDetailImageForTabletDialogFragment;
import com.receiptofi.receiptapp.fragments.ReceiptDetailImageFragment;
import com.receiptofi.receiptapp.fragments.ReceiptListFragment;
import com.receiptofi.receiptapp.fragments.SettingFragment;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.model.ProfileModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.utils.db.ProfileUtils;

import org.apache.commons.lang3.text.WordUtils;

import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, ReceiptListFragment.OnReceiptSelectedListener {

    private ReceiptofiApplication receiptofiApplication;
    public HomeFragment1 homeFragment;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private String tagHome = "HOME";
    private String tagReceipt = "RECEIPT";
    private  String tagMe = "ME";
    private String  tagSplit = "SPLIT";
    private String tagShoppingList = "SHOPPINGLIST";
    private String tagReceiptDetail = "RECEIPTDETAIL";

    //--- Bottom nav button
    public LinearLayout llbottom_nav_home,bottom_nav_receipts,bottom_nav_split,bottom_nav_shoping_list,bottom_nav_me;
    public  Button btn_home,btn_receipts,btn_shoppingList,btn_split,btn_me;
    private  Drawable drawablehomered,drawablereceiptred,drawablesplitred,drawableshoppinglistred,drawablemered,drawablehomeblue,drawablereceiptblue,drawablesplitblue,drawableshoppinglistblue,drawablemeblue;

    private Toolbar toolbar;
    private MaterialAccount account;
    public static final int UPDATE_USER_INFO = 0x1061;
    private SuperActivityToast uploadImageToast;
    private int groupIndex = -1;
    private int childIndex = -1;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case UPDATE_USER_INFO:
                    ProfileModel profileModel = (ProfileModel) msg.obj;
                   // refreshAccount(profileModel);
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setHomePageContext(this);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReceiptofiApplication.homeActivityResumed();
        receiptofiApplication = (ReceiptofiApplication) getApplicationContext();

        homeFragment = HomeFragment1.newInstance("", "");

        ProfileModel profileModel = ProfileUtils.getProfile();
        String name = profileModel != null ? profileModel.getName() : "";
        String mail = profileModel != null ? profileModel.getMail() : UserUtils.getEmail();



        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HomeActivity ","Home Activity OnResume");
        init();
        receiptofiApplication.setCurrentActivity(this);
        replaceFragmentwithTag(homeFragment,tagHome);
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
                HomeFragment homeFragment = new HomeFragment();
                replaceFragmentwithTag(homeFragment,tagHome);
                homebuttonClick();
                break;
            case R.id.btn_me:
                SettingFragment  settingFragment = SettingFragment.getInstance();
                replaceFragmentwithTag(settingFragment,tagMe);
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
                ReceiptListFragment fragment = ReceiptListFragment.getInstance();
                replaceFragmentwithTag(fragment,tagReceipt);
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

    public void stopProgressToken() {
        if (null != uploadImageToast && uploadImageToast.isShowing()) {
            uploadImageToast.dismiss();
        }
    }

   /* *//**
     * Refreshes account profile on main menu.
     *
     * @param profileModel
     *//*
    private void refreshAccount(ProfileModel profileModel) {
        if (null != account) {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.BLACK)
                    .useFont(Typeface.DEFAULT)
                    .fontSize(30) *//* size in px *//*
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(WordUtils.initials(profileModel.getName()), Color.WHITE);

            account.setPhoto(drawableToText(textDrawable));
            account.setTitle(profileModel.getName());
            setUserEmail(profileModel.getMail());
        } else {
            setUsername(profileModel.getName());
            setUserEmail(profileModel.getMail());
        }
        *//** Works without this line but just to be safe. *//*
        notifyAccountDataChanged();
    }*/


    private void replaceFragmentwithTag(Fragment fragment,String tag)
    {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, fragment,tag);
        transaction.addToBackStack(null);
// Commit the transaction
        transaction.commit();
    }


    public void onReceiptSelected(int index, int position) {
        // The user selected the headline of an article from the HeadlinesFragment
        Log.d(TAG, "executing onReceiptSelected: index is: " + index + " position is: " + position);
        if (index < 0 || position < 0) {
            return;
        }

        groupIndex = index;
        childIndex = position;
        // Capture the article fragment from the activity layout
        ReceiptDetailFragment receiptDetailFragment = (ReceiptDetailFragment)
                getFragmentManager().findFragmentById(R.id.rdetail_fragment);

        if (receiptDetailFragment != null) {
            Log.d(TAG, "detail fragment already instantiated");
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            receiptDetailFragment.updateReceiptDetailView(index, position, false);

        } else {
            Log.d(TAG, "Instantiating new detail fragment");
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ReceiptDetailFragment newFragment = new ReceiptDetailFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.ARG_INDEX, index);
            args.putInt(Constants.ARG_POSITION, position);
            newFragment.setArguments(args);
            replaceFragmentwithTag(newFragment,tagReceiptDetail);
        }
        if (groupIndex > -1 && childIndex > -1) {
           // setDrawerView();
        }
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public void setGroupIndex(int groupPosition) {
        groupIndex = groupPosition;
    }

    public void setChildIndex(int childPosition) {
        childIndex = childPosition;
    }


    public void showReceiptDetailImageFragment(String url) {
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        if (AppUtils.isTablet(this)) {
            // Handle Table environment
            FragmentManager fm = getFragmentManager();
            ReceiptDetailImageForTabletDialogFragment detailImage = new ReceiptDetailImageForTabletDialogFragment();
            Bundle args_tablet = new Bundle();
            args_tablet.putString(Constants.ARG_IMAGE_URL, url);
            detailImage.setArguments(args_tablet);
            detailImage.show(fm, "fragment_detail_image");

        } else {
            // Create fragment and give it an argument for the selected article
            ReceiptDetailImageFragment newFragment = new ReceiptDetailImageFragment();
            Bundle args = new Bundle();
            args.putString(Constants.ARG_IMAGE_URL, url);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            // Handle normal phone environment
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }
    }
}
