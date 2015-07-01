package com.receiptofi.checkout;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.fragments.BillingFragment;
import com.receiptofi.checkout.fragments.ExpenseTagFragment;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.fragments.NotificationFragment;
import com.receiptofi.checkout.fragments.SettingFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.model.ProfileModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.ProfileUtils;

import java.io.File;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by kevin on 6/4/15.
 */
public class MainMaterialDrawerActivity extends MaterialNavigationDrawer implements HomeFragment.OnFragmentInteractionListener, ExpenseTagFragment.OnFragmentInteractionListener {
    private Menu optionMenu;
    private SearchView searchView;
    private static final String TAG = MainMaterialDrawerActivity.class.getSimpleName();
    private ActionBar ab;
    public HomeFragment mHomeFragment;
    public NotificationFragment mNotificationFragment;
    public ExpenseTagFragment mExpenseTagFragment;
    public BillingFragment mBillingFragment;
    public SettingFragment mSettingFragment;
    private Context mContext;
    private SuperActivityToast uploadImageToast;

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
        mBillingFragment = new BillingFragment();
        mSettingFragment = new SettingFragment();

        ProfileModel profileModel = ProfileUtils.getProfile();
        String name = profileModel != null ? profileModel.getName() : "";
        String mail = profileModel != null ? profileModel.getMail() : UserUtils.getEmail();
        int backgroundImage = profileModel != null ? profileModel.getProfileBackgroundImage() : R.drawable.bamboo;
        /**
         * Check if the app already has a backgroundImage.
         */
        backgroundImage = BitmapFactory.decodeResource(getResources(), backgroundImage) != null ? backgroundImage : R.drawable.bamboo;
        MaterialAccount account = new MaterialAccount(this.getResources(), name, mail, R.drawable.ic_profile, backgroundImage);

        this.addAccount(account);
        this.addSection(
                newSection(
                        "Home",
                        new IconDrawable(mContext, Iconify.IconValue.fa_home)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        mHomeFragment));

        this.addSection(
                newSection(
                        "Notification",
                        new IconDrawable(mContext, Iconify.IconValue.fa_bell_o)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        mNotificationFragment));

        this.addSection(
                newSection(
                        "Tag Expenses",
                        new IconDrawable(mContext, Iconify.IconValue.fa_tags)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        mExpenseTagFragment));

        this.addSection(
                newSection(
                        "Billing History",
                        new IconDrawable(mContext, Iconify.IconValue.fa_history)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        mBillingFragment));

        this.addSection(
                newSection(
                        "Subscription",
                        new IconDrawable(mContext, Iconify.IconValue.fa_hand_o_up)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        mBillingFragment));

        this.addSection(
                newSection(
                        "Log Out",
                        new IconDrawable(mContext, Iconify.IconValue.fa_sign_out)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        new MaterialSectionListener() {
                            @Override
                            public void onClick(MaterialSection materialSection) {
                                logout();
                            }
                        }));

        // create bottom section
        this.addBottomSection(
                newSection(
                        "Settings",
                        new IconDrawable(mContext, Iconify.IconValue.fa_cogs)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        mSettingFragment));

        // Close the drawer menu.
        this.disableLearningPattern();

        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        optionMenu = menu;

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        /**
         * Replace the default menu search image.
         */
        Drawable mDraw = new IconDrawable(this, Iconify.IconValue.fa_search)
                .colorRes(R.color.white)
                .actionBarSize();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageDrawable(mDraw);

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setTextColor(Color.WHITE);
        searchAutoCompleteTextView.setHint("Search");
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        return super.onCreateOptionsMenu(menu);
    }

    private void logout() {
        KeyValueUtils.updateValuesForKeyWithBlank(API.key.XR_AUTH);
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IMAGE_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageGallery = data.getData();
            String imageAbsolutePath = AppUtils.getImageFileFromURI(this, imageGallery);

            if (null != imageAbsolutePath) {
                File pickerImage = new File(imageAbsolutePath);
                if ((pickerImage.length() / 1048576) >= 10) {
                    showErrorMsg("Image size of more than 10MB not supported.");
                } else {
                    startProgressToken();
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

                startProgressToken();
                ImageUpload.process(this, capturedImgFile);
            }
        } else if (resultCode == RESULT_CANCELED) {
            String capturedImgFile = AppUtils.getImageFilePath();
            if (null != capturedImgFile) {
                File capturedFile = new File(capturedImgFile);
                if (capturedFile.exists()) {
                    boolean deleteStatus = capturedFile.delete();
                    Log.d(TAG, "Cancelled, deleted=" + deleteStatus + " file=" + capturedImgFile);
                } else {
                    Log.d(TAG, "Cancelled, does not exists file=" + capturedImgFile);
                }
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
        SuperActivityToast superActivityToast = new SuperActivityToast(MainMaterialDrawerActivity.this);
        superActivityToast.setText(msg);
        superActivityToast.setDuration(SuperToast.Duration.SHORT);
        superActivityToast.setBackground(SuperToast.Background.BLUE);
        superActivityToast.setTextColor(Color.WHITE);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();
    }

    private void startProgressToken() {
        uploadImageToast = new SuperActivityToast(this, SuperToast.Type.PROGRESS);
        uploadImageToast.setText("Image Uploading!");
        uploadImageToast.setIndeterminate(true);
        uploadImageToast.setProgressIndeterminate(true);
        uploadImageToast.show();
    }

    public void stopProgressToken() {
        if (null != uploadImageToast && uploadImageToast.isShowing()) {
            uploadImageToast.dismiss();
        }
    }

}
