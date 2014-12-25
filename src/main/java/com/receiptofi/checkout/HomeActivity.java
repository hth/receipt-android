package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.view.Menu;
import android.view.MenuInflater;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.utils.AppUtils;

import java.io.File;

public class HomeActivity extends Activity {

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    protected Handler uiThread = new Handler();
    TextView unprocessedDocumentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        //TODO needed for ImageUploaderService
        AppUtils.setHomePageContext(this);
        unprocessedDocumentCount = (TextView) findViewById(R.id.processing_info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
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

                ImageUpload.process(this, capturedImgFile);
            }
        }

    }

    // TODO: fix at start
    public void updateUnprocessedCount(final int count) {
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                unprocessedDocumentCount.setText(String.format(getString(R.string.processing_info), count));
            }

        });
    }

    private void launchSettings(){
        startActivity(new Intent(this, SettingsPage.class));
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.setHomePageContext(null);
    }

    public void showErrorMsg(final String msg) {
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

        });
    }
}
