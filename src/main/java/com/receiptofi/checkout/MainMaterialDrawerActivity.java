package com.receiptofi.checkout;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.fragments.BillingFragment;
import com.receiptofi.checkout.fragments.ExpenseTagFragment;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.fragments.NotificationFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.views.ToastBox;

import java.io.File;

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

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

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

        this.addSection(newSection("Subscription", new IconDrawable(mContext, Iconify.IconValue.fa_money)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_IMAGE_GALLERY && resultCode == RESULT_OK && null != data) {

            Uri imageGallery = data.getData();
            String imageAbsolutePath = AppUtils.getImageFileFromURI(this, imageGallery);

            if (null != imageAbsolutePath) {
                File pickerImage = new File(imageAbsolutePath);
                if ((pickerImage.length() / 1048576) >= 10) {
                    showErrorMsg("Image size of more than 10MB not supported.");
                } else {
//                    startAnimation();
                    ImageUpload.process(this, imageAbsolutePath);
                }
            }

        } else if (requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            String capturedImgFile = AppUtils.getImageFilePath();
            if (null != capturedImgFile) {

                File capturedFile = new File(capturedImgFile);
                Uri contentUri = Uri.fromFile(capturedFile);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

//                startAnimation();
                ImageUpload.process(this, capturedImgFile);
            }
        }

    }

    /**
     * Below three functions are the XML linear layout onclick handler.
     *
     * @param view
     */
    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = AppUtils.createImageFile();
        // Continue only if the File was successfully created
        if (null != photoFile) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, RESULT_IMAGE_CAPTURE);
        } else {
            showErrorMsg("We seemed to have encountered issue saving your image.");
        }
    }

    public void chooseImage(View view) {
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(g, RESULT_IMAGE_GALLERY);
    }

    public void invokeReceiptList(View view) {
        startActivity(new Intent(MainMaterialDrawerActivity.this, ReceiptListActivity.class));
    }


    private void showErrorMsg(String msg) {
        ToastBox.makeText(MainMaterialDrawerActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
