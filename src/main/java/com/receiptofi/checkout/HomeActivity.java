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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends Activity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final DateFormat DF = new SimpleDateFormat("MMM yyyy");

    public static final int IMAGE_UPLOAD_SUCCESS = 0x2564;
    public static final int IMAGE_ALREADY_QUEUED = 0x2565;
    public static final int IMAGE_UPLOAD_FAILURE = 0x2566;
    public static final int UPDATE_UNPROCESSED_COUNT = 0x2567;
    public static final int UPDATE_MONTHLY_EXPENSE = 0x2568;
    public static final int GET_ALL_RECEIPTS = 0x2569;

    public final Handler updateHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case IMAGE_UPLOAD_SUCCESS:
                    setUnprocessedCount(Integer.toString(msg.arg1));
                    showErrorMsg((String) msg.obj);
                    endAnimation();
                    break;
                case IMAGE_UPLOAD_FAILURE:
                    showErrorMsg((String) msg.obj);
                    endAnimation();
                    break;
                case IMAGE_ALREADY_QUEUED:
                    showErrorMsg((String) msg.obj);
                    endAnimation();
                    break;
                case UPDATE_UNPROCESSED_COUNT:
                    setUnprocessedCount((String) msg.obj);
                    break;
                case UPDATE_MONTHLY_EXPENSE:
                    setMonthlyExpense((String) msg.obj);
                    break;
            }
        }
    };
    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;
    protected Handler uiThread = new Handler();

    TextView unprocessedDocumentCount;
    TextView currentMonthExp;
    private Menu optionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

        //TODO needed for ImageUploaderService
        AppUtils.setHomePageContext(this);
        unprocessedDocumentCount = (TextView) findViewById(R.id.processing_info);
        currentMonthExp = (TextView) findViewById(R.id.current_amount);
        currentMonthExp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // show graph page
                Log.d(TAG, "executing showGraph");
                Intent i = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(i);
            }
        });

        TextView notification = (TextView) findViewById(R.id.processing_info);
        notification.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // show graph page
                Log.d(TAG, "executing showGraph");
                Intent i = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(i);
            }
        });

        setUnprocessedCount(KeyValueUtils.getValue(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT));

        try {
            setMonthlyExpense(MonthlyReportUtils.fetchMonthlyTotal(Integer.toString(Calendar.getInstance().get(Calendar.MONTH)),
                    Integer.toString(Calendar.getInstance().get(Calendar.YEAR) - 1)));
        }catch(Exception e){
            Log.d(TAG, "Exception" + e.getMessage());
            e.printStackTrace();
        }

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
        if (optionMenu != null) {
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
        startActivity(new Intent(this, ReceiptListActivity.class));
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

    private void startAnimation() {
        if(optionMenu != null) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView imageView = (ImageView) inflater.inflate(R.layout.action_refresh, null);

            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
            rotation.setRepeatCount(Animation.INFINITE);
            imageView.startAnimation(rotation);

            MenuItem item = optionMenu.findItem(R.id.menu_refresh);
            MenuItemCompat.setActionView(item, imageView);
        }
    }

    private void endAnimation() {
        MenuItem item = optionMenu.findItem(R.id.menu_refresh);
        if (item.getActionView() != null) {
            // Remove the animation.
            item.getActionView().clearAnimation();
            item.setActionView(null);
        }
    }

    private void setUnprocessedCount(String count) {
        Log.d(TAG, "executing setUnprocessedCount");
        unprocessedDocumentCount.setText(getString(R.string.processing_info, count));
    }

    private void setMonthlyExpense(String amount) {
        Log.d(TAG, "executing setMonthlyExpense");
        currentMonthExp.setText(getString(R.string.monthly_amount, DF.format(new Date()), amount));
    }

    private void launchSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void logout() {
        KeyValueUtils.updateValuesForKeyWithBlank(API.key.XR_AUTH);
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.setHomePageContext(null);
    }

    private void showErrorMsg(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
