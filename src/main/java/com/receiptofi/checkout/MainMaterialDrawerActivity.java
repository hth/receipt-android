package com.receiptofi.checkout;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.fragments.BillingFragment;
import com.receiptofi.checkout.fragments.ExpenseTagFragment;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.fragments.NotificationFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by kevin on 6/4/15.
 */
public class MainMaterialDrawerActivity extends MaterialNavigationDrawer implements HomeFragment.OnFragmentInteractionListener, ExpenseTagFragment.OnFragmentInteractionListener{
    private Menu optionMenu;
    private SearchView searchView;
    private static final String TAG = MainMaterialDrawerActivity.class.getSimpleName();
    private ActionBar ab;
    public HomeFragment mHomeFragment;
    public NotificationFragment mNotificationFragment;
    public ExpenseTagFragment mExpenseTagFragment;
    public BillingFragment mBillingFragmentnew;
    private Context mContext;
    @Override
    public void init(Bundle savedInstanceState) {

        ReceiptofiApplication.homeActivityResumed();
        mContext = getApplicationContext();
        AppUtils.setHomePageContext(this);


        mHomeFragment = HomeFragment.newInstance("", "");
        mNotificationFragment = NotificationFragment.newInstance("", "");
        mExpenseTagFragment = ExpenseTagFragment.newInstance("", "");
        mBillingFragmentnew = new BillingFragment();

        String username = UserUtils.getEmail();

        MaterialAccount account = new MaterialAccount(this.getResources(),"NeoKree"," " + username, R.drawable.photo, R.drawable.bamboo);
        this.addAccount(account);
        this.addSection(newSection("Home", new IconDrawable(mContext, Iconify.IconValue.fa_home)
                .colorRes(R.color.white)
                .actionBarSize(), mHomeFragment));

        this.addSection(newSection("Notification", new IconDrawable(mContext, Iconify.IconValue.fa_bell_o)
                .colorRes(R.color.white)
                .actionBarSize(), mNotificationFragment));

        this.addSection(newSection("Tag Expenses", new IconDrawable(mContext, Iconify.IconValue.fa_tags)
                .colorRes(R.color.white)
                .actionBarSize(), mExpenseTagFragment));

        this.addSection(newSection("Billing History", new IconDrawable(mContext, Iconify.IconValue.fa_shopping_cart)
                .colorRes(R.color.white)
                .actionBarSize(), mBillingFragmentnew));

        this.addSection(newSection("Subscription", new IconDrawable(mContext, Iconify.IconValue.fa_shopping_cart)
                .colorRes(R.color.white)
                .actionBarSize(), mBillingFragmentnew));

        this.addSection(newSection("Log Out", new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {
                logout();
            }
        }));

        // create bottom section
        this.addBottomSection(newSection("Settings", R.drawable.ic_settings_black_24dp, new Intent(this, SettingsActivity.class)));

        // Close the drawer menu.
        this.disableLearningPattern();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "executing onResume");
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        Log.d(TAG, "Done onResume!!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "executing onDestroy");
        ReceiptofiApplication.homeActivityPaused();
        AppUtils.setHomePageContext(null);
        Log.d(TAG, "Done onDestroy!!");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_material_drawer_activity, menu);
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

    private void logout() {
        KeyValueUtils.updateValuesForKeyWithBlank(API.key.XR_AUTH);
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }

}
