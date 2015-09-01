package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.fragments.BillingFragment;
import com.receiptofi.receiptapp.fragments.ExpenseTagFragment;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.fragments.NotificationFragment;
import com.receiptofi.receiptapp.fragments.SettingFragment;
import com.receiptofi.receiptapp.fragments.SubscriptionFragment;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.model.ProfileModel;
import com.receiptofi.receiptapp.service.gcm.RegistrationIntentService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.utils.db.ProfileUtils;

import org.apache.commons.lang3.text.WordUtils;

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

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private SearchView searchView;
    public HomeFragment homeFragment;
    public NotificationFragment notificationFragment;
    public ExpenseTagFragment expenseTagFragment;
    public BillingFragment billingFragment;
    public SubscriptionFragment subscriptionFragment;
    public SettingFragment settingFragment;
    private SuperActivityToast uploadImageToast;
    private ReceiptofiApplication receiptofiApplication;
    private MaterialAccount account;

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    public static final int UPDATE_USER_INFO = 0x1061;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case UPDATE_USER_INFO:
                    ProfileModel profileModel = (ProfileModel) msg.obj;
                    refreshAccount(profileModel);
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    @Override
    public void init(Bundle savedInstanceState) {

        ReceiptofiApplication.homeActivityResumed();
        receiptofiApplication = (ReceiptofiApplication) getApplicationContext();

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

        if (TextUtils.isEmpty(name)) {
            account = new MaterialAccount(
                    this.getResources(),
                    name,
                    mail,
                    R.drawable.ic_profile,
                    drawableToBitmap(new ColorDrawable(getResources().getColor(R.color.gray_darker))));
        } else {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.BLACK)
                    .useFont(Typeface.DEFAULT)
                    .fontSize(30) /* size in px */
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(WordUtils.initials(name), Color.WHITE);

            account = new MaterialAccount(
                    this.getResources(),
                    name,
                    mail,
                    drawableToText(textDrawable),
                    drawableToBitmap(new ColorDrawable(getResources().getColor(R.color.gray_darker))));
        }

        setUsernameTextColor(Color.WHITE);
        setUserEmailTextColor(Color.WHITE);

        this.addAccount(account);
        this.addSection(
                newSection(
                        "Home",
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_home)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        homeFragment));

        this.addSection(
                newSection(
                        "Notification",
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_bell_o)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        notificationFragment));

        this.addSection(
                newSection(
                        "Tag Expenses",
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_tags)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        expenseTagFragment));

        this.addSection(
                newSection(
                        "Billing History",
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_history)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        billingFragment));

        this.addSection(
                newSection(
                        "Subscription",
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_hand_o_up)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        subscriptionFragment));

        this.addSection(
                newSection(
                        "Log Out",
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_sign_out)
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
                        new IconDrawable(receiptofiApplication, Iconify.IconValue.fa_cogs)
                                .colorRes(R.color.white)
                                .actionBarSize(),
                        settingFragment));

        // Close the drawer menu.
        this.disableLearningPattern();

        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
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
        receiptofiApplication.setCurrentActivity(this);
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
                    showToastMsg("Image size of more than 10MB not supported.", SuperToast.Background.RED);
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
            showToastMsg("We seemed to have encountered issue saving your image.", SuperToast.Background.RED);
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

    public void showToastMsg(String msg, int backgroundColor) {
        SuperActivityToast superActivityToast = new SuperActivityToast(MainMaterialDrawerActivity.this);
        superActivityToast.setText(msg);
        superActivityToast.setDuration(SuperToast.Duration.SHORT);
        superActivityToast.setBackground(backgroundColor);
        superActivityToast.setTextColor(Color.WHITE);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();
    }

    private void startProgressToken() {
        uploadImageToast = new SuperActivityToast(this, SuperToast.Type.PROGRESS);
        uploadImageToast.setText("Image Uploading.");
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
        Activity currActivity = receiptofiApplication.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            receiptofiApplication.setCurrentActivity(null);
    }

    /**
     * Change the color to any one you like, or set the last parameter of MaterialAccount as NUll,
     * then use default color.
     *
     * @param drawable
     * @return
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
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

    /**
     * Set the profile image with initials.
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToText(TextDrawable drawable) {
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Refreshes account profile on main menu.
     *
     * @param profileModel
     */
    private void refreshAccount(ProfileModel profileModel) {
        if (null != account) {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.BLACK)
                    .useFont(Typeface.DEFAULT)
                    .fontSize(30) /* size in px */
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
        /** Works without this line but just to be safe. */
        notifyAccountDataChanged();
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
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
