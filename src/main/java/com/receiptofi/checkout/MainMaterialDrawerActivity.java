package com.receiptofi.checkout;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import com.receiptofi.checkout.fragments.SubscriptionFragment;
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
 * User: kevin
 * Date: 6/4/15 6:47 PM
 */
public class MainMaterialDrawerActivity extends MaterialNavigationDrawer implements HomeFragment.OnFragmentInteractionListener, ExpenseTagFragment.OnFragmentInteractionListener {
    private static final String TAG = MainMaterialDrawerActivity.class.getSimpleName();

    private SearchView searchView;
    public HomeFragment homeFragment;
    public NotificationFragment notificationFragment;
    public ExpenseTagFragment expenseTagFragment;
    public BillingFragment billingFragment;
    public SubscriptionFragment subscriptionFragment;
    public SettingFragment settingFragment;
    private SuperActivityToast uploadImageToast;
    protected ReceiptofiApplication myApp;

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    @Override
    public void init(Bundle savedInstanceState) {

        ReceiptofiApplication.homeActivityResumed();
        myApp = (ReceiptofiApplication) getApplicationContext();

        AppUtils.setHomePageContext(this);

        homeFragment = HomeFragment.newInstance("", "");
        notificationFragment = NotificationFragment.newInstance("", "");
        expenseTagFragment = ExpenseTagFragment.newInstance("", "");
        billingFragment = new BillingFragment();
        subscriptionFragment = new SubscriptionFragment();
        settingFragment = new SettingFragment();

        ProfileModel profileModel = ProfileUtils.getProfile();
        String name = profileModel != null ? profileModel.getName() : "";
        String mail = profileModel != null ? profileModel.getMail() : UserUtils.getEmail();

        // You can change the color to any one you like, or set the last parameter of MaterialAccount as NUll, then use default color.
        ColorDrawable drawable = new ColorDrawable(R.color.green);

        MaterialAccount account = new MaterialAccount(this.getResources(), name, mail, R.drawable.ic_profile, drawableToBitmap(drawable));

        setUsernameTextColor(Color.BLACK);
        setUserEmailTextColor(Color.BLACK);

        this.addAccount(account);
        this.addSection(
                newSection(
                        "Home",
                        new IconDrawable(myApp, Iconify.IconValue.fa_home)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        homeFragment));

        this.addSection(
                newSection(
                        "Notification",
                        new IconDrawable(myApp, Iconify.IconValue.fa_bell_o)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        notificationFragment));

        this.addSection(
                newSection(
                        "Tag Expenses",
                        new IconDrawable(myApp, Iconify.IconValue.fa_tags)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        expenseTagFragment));

        this.addSection(
                newSection(
                        "Billing History",
                        new IconDrawable(myApp, Iconify.IconValue.fa_history)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        billingFragment));

        this.addSection(
                newSection(
                        "Subscription",
                        new IconDrawable(myApp, Iconify.IconValue.fa_hand_o_up)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        subscriptionFragment));

        this.addSection(
                newSection(
                        "Log Out",
                        new IconDrawable(myApp, Iconify.IconValue.fa_sign_out)
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
                        new IconDrawable(myApp, Iconify.IconValue.fa_cogs)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        settingFragment));

        // Close the drawer menu.
        this.disableLearningPattern();

        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myApp.setCurrentActivity(this);
        Log.d(TAG, "executing onResume");
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        Log.d(TAG, "Done onResume!!");
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_material_drawer_activity, menu);

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
     * @param view - Button from XML function.
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

    public SubscriptionFragment getSubscriptionFragment() {
        return subscriptionFragment;
    }

    public void showErrorMsg(String msg) {
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
        uploadImageToast.setText("Image Uploading");
        uploadImageToast.setIndeterminate(true);
        uploadImageToast.setProgressIndeterminate(true);
        uploadImageToast.show();
    }

    public void stopProgressToken() {
        if (null != uploadImageToast && uploadImageToast.isShowing()) {
            uploadImageToast.dismiss();
        }
    }

    private void clearReferences() {
        Activity currActivity = myApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            myApp.setCurrentActivity(null);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
