package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.utils.AppUtils;

import java.io.File;

public class HomeActivity extends Activity {

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    public static final int IMAGE_UPLOAD_SUCCESS = 0x2564;
    public static final int IMAGE_ALREADY_QUEUED = 0x2565;
    public static final int IMAGE_UPLOAD_FAILURE = 0x2566;

    protected Handler uiThread = new Handler();
    private Menu optionMenu;
    // TODO: fix me
    TextView unprocessedDocumentCount;
    TextView currentAmount;


    public final Handler updateHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case IMAGE_UPLOAD_SUCCESS:
                    updateUnprocessedCount(msg.arg1);
                    showErrorMsg((String)msg.obj);
                    endAnimation();
                    break;
                case IMAGE_UPLOAD_FAILURE:
                    showErrorMsg((String)msg.obj);
                    endAnimation();
                    break;
                case IMAGE_ALREADY_QUEUED:
                    showErrorMsg((String)msg.obj);
                    endAnimation();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        //TODO needed for ImageUploaderService
        AppUtils.setHomePageContext(this);
        unprocessedDocumentCount = (TextView) findViewById(R.id.processing_info);
        currentAmount = (TextView) findViewById(R.id.current_amount);


    }

    @Override
    protected void onResume() {
        super.onResume();
        ReceiptofiApplication.homeActivityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ReceiptofiApplication.homeActivityPaused();
        if(optionMenu != null) {
            endAnimation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        optionMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // TODO call getUpdate
                return true;
            case R.id.menu_settings:
                launchSettings();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = AppUtils.createImageFile();
        // Continue only if the File was successfully created
        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, RESULT_IMAGE_CAPTURE);
        } else {
            showErrorMsg("some error occurred !!");
        }
    }

    public void chooseImage(View view) {
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(g, RESULT_IMAGE_GALLERY);
    }

    public void invokeReceiptList(View view) {
        View container = findViewById(R.id.leftSidePane);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {

            Uri imageGallery = data.getData();
            String imageAbsolutePath = AppUtils.getImageFileFromURI(this, imageGallery);

            if (imageAbsolutePath != null) {
                File pickerImage = new File(imageAbsolutePath);
                if ((pickerImage.length() / 1048576) >= 10) {
                    showErrorMsg("image size should be upto 10Mb");
                } else {
                    startAnimation();
                    ImageUpload.process(this, imageAbsolutePath);
                }

            }

        } else if (requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            String capturedImgFile = AppUtils.getImageFilePath();
            if (capturedImgFile != null) {

                File capturedFile = new File(capturedImgFile);
                Uri contentUri = Uri.fromFile(capturedFile);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                startAnimation();
                ImageUpload.process(this, capturedImgFile);
            }
        }

    }

    private void startAnimation(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView = (ImageView) inflater.inflate(R.layout.action_refresh, null);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotation);

        MenuItem item = optionMenu.findItem(R.id.menu_refresh);
        MenuItemCompat.setActionView(item, imageView);
    }

    private void endAnimation(){
        MenuItem item = optionMenu.findItem(R.id.menu_refresh);
         if(item.getActionView()!=null)
        {
            // Remove the animation.
            item.getActionView().clearAnimation();
            item.setActionView(null);
        }
    }

    // TODO: fix at start
    private void updateUnprocessedCount(final int count) {
        unprocessedDocumentCount.setText(String.format(getString(R.string.processing_info), count));
    }

    private void launchSettings(){
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void logout() {
        //KeyValue.removeValue(API.key.XR_AUTH);
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.setHomePageContext(null);
    }

    private void showErrorMsg(final String msg) {
         Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
