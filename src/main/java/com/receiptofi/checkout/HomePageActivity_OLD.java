package com.receiptofi.checkout;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.fragments.ReceiptListFragment;
import com.receiptofi.checkout.fragments.ViewReceiptPage;
import com.receiptofi.checkout.models.ReceiptModel;
import com.receiptofi.checkout.utils.AppUtils;

import java.io.File;

public class HomePageActivity_OLD extends ParentActivity {

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    Display display;
    Point size;
    TextView unprocessedDocumentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_old);
        AppUtils.setHomePageContext(this);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        unprocessedDocumentCount = (TextView) findViewById(R.id.unprocessDoucumentCount);
    }

    public void invokeMenu(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
       // this.overridePendingTransition(R.anim.left_slide_in, R.anim.right_side_out);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        addToBackStack(this);
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

        Intent g = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(g, RESULT_IMAGE_GALLERY);
    }

    public void invokeReceiptList(View view) {
        View container = findViewById(R.id.leftSidePane);
        startFragment(new ReceiptListFragment(), true, container.getId());
    }

    public void invokeDetailReceiptView(View view, ReceiptModel model) {
        View container = findViewById(R.id.leftSidePane);

        Bundle blobData = new Bundle();
        blobData.putString("date", model.date);
        blobData.putString("blobId", model.filesBlobId);
        blobData.putString("receiptId", model.id);
        blobData.putDouble("totalPrice", model.total);
        blobData.putString("receiptName", model.bizName);
        blobData.putDouble("ptax", model.ptax);

        ViewReceiptPage detailPage = new ViewReceiptPage();
        detailPage.setArguments(blobData);
        startFragment(detailPage, true, container.getId());
    }


    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void updateUnprocessedCount(final int count) {
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                unprocessedDocumentCount.setText("UNPROCESSED COUNT: " + count);
            }

        });
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

                // HTTPUtils.uploadImage(this,API.UPLOAD_IMAGE_API,captiredImgFile);
                ImageUpload.process(this, capturedImgFile);
            }
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        AppUtils.setHomePageContext(null);
    }

    public int getHeight() {
        return size.y;
    }

    public int getWidth() {
        return size.x;
    }

    public int getAspectRatio() {
        return size.y / size.x;
    }
}
