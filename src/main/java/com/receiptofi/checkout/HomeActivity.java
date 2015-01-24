package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.service.ChartService;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends Activity implements OnChartValueSelectedListener{

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final DateFormat DF_MMM = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
    public static final DateFormat DF_YYYY_MM = new SimpleDateFormat("yyyy MM", Locale.ENGLISH);

    public static final int IMAGE_UPLOAD_SUCCESS = 0x2564;
    public static final int IMAGE_ALREADY_QUEUED = 0x2565;
    public static final int IMAGE_UPLOAD_FAILURE = 0x2566;
    public static final int UPDATE_UNPROCESSED_COUNT = 0x2567;
    public static final int UPDATE_MONTHLY_EXPENSE = 0x2568;
    public static final int UPDATE_EXP_BY_BIZ_CHART = 0x2569;
    public static final int GET_ALL_RECEIPTS = 0x2570;

    public final Handler updateHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case IMAGE_UPLOAD_SUCCESS:
                    unprocessedValue = Integer.toString(msg.arg1);
                    setUnprocessedCount();
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
                    unprocessedValue = (String) msg.obj;
                    setUnprocessedCount();
                    break;
                case UPDATE_MONTHLY_EXPENSE:
                    currentMonthExpValue = (String) msg.obj;
                    setMonthlyExpense();
                    break;
                case UPDATE_EXP_BY_BIZ_CHART:
                    expByBizAnimate = true;
                    updateChartData();
                    break;
            }
        }
    };
    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;
    protected Handler uiThread = new Handler();

    private TextView unprocessedDocumentCount;
    private String unprocessedValue;
    private TextView currentMonthExp;
    private String currentMonthExpValue;
    private Menu optionMenu;
    private PieChart mChart;
    private PieData expByBizData;
    private boolean expByBizAnimate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "executing onCreate");
        ReceiptofiApplication.homeActivityResumed();
        setContentView(R.layout.home_page);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

        //TODO needed for ImageUploaderService
        AppUtils.setHomePageContext(this);
        instantiateViews();

        // OnCreate we need to set values from Database and these will be updated later once
        // update from server is received.
        unprocessedValue = KeyValueUtils.getValue(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT);
        setUnprocessedCount();
        try {
            String[] monthDay = DF_YYYY_MM.format(new Date()).split(" ");
            currentMonthExpValue = MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]);
            setMonthlyExpense();
        } catch (Exception e) {
            Log.d(TAG, "Exception" + e.getMessage());
            e.printStackTrace();
        }
        updateChartData();
        Log.d(TAG, "Done onCreate!!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "executing onResume");
        Log.d(TAG, "Done onResume!!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "executing onPause");
        if (optionMenu != null) {
            endAnimation();
        }
        Log.d(TAG, "Done onPause!!");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.home_page);
            instantiateViews();
            bindValuestoViews();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.home_page);
            instantiateViews();
            bindValuestoViews();
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

    private void instantiateViews(){
        unprocessedDocumentCount = (TextView) findViewById(R.id.processing_info);
        currentMonthExp = (TextView) findViewById(R.id.current_amount);
        mChart = (PieChart) findViewById(R.id.pie_chart);
        setUpChartView();
    }
    private void bindValuestoViews(){
        if(!TextUtils.isEmpty(unprocessedValue)) {
            setUnprocessedCount();
        }
        if(!TextUtils.isEmpty(currentMonthExpValue)) {
            setMonthlyExpense();
        }
        if(expByBizData != null) {
            setUpChartData();
        }
    }

    private void setUpChartView(){
        // change the color of the center-hole
        mChart.setHoleColor(getResources().getColor(R.color.hole_color));

        // Causing java.lang.RuntimeException: native typeface cannot be made
        mChart.setValueTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf"));
        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf"));

        mChart.setHoleRadius(45f);

        mChart.setDescription(getString(R.string.chart_desc));
        mChart.setDescriptionTextSize(16f);

        mChart.setDrawYValues(true);
        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        mChart.setRotationAngle(0);

        // draws the corresponding description value into the slice
        mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // display percentage values
        mChart.setUsePercentValues(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

        mChart.setCenterText(getString(R.string.chart_desc_short));
        mChart.setCenterTextSize(12f);

        /*Legend l = mChart.getLegend();
        if(l != null){
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(5f);
        }
        */
    }

    private void updateChartData(){
        if(expByBizAnimate) {
            mChart.animateXY(1500, 1500);
            // mChart.spin(2000, 0, 360);
            expByBizAnimate = false;
        }

        expByBizData = ChartService.getPieData();
        mChart.setData(expByBizData);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private void setUpChartData(){
        mChart.setData(expByBizData);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry entry, int i) {
        showErrorMsg("VAL SELECTED" +
                "Value: " + entry.getVal() + ", xIndex: " + entry.getXIndex());
    }

    @Override
    public void onNothingSelected() {

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
        if (optionMenu != null) {
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

    private void setUnprocessedCount() {
        Log.d(TAG, "executing setUnprocessedCount");
        unprocessedDocumentCount.setText(getString(R.string.processing_info, unprocessedValue));
    }

    private void setMonthlyExpense() {
        Log.d(TAG, "executing setMonthlyExpense");
        currentMonthExp.setText(getString(R.string.monthly_amount, DF_MMM.format(new Date()), currentMonthExpValue));
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
        Log.d(TAG, "executing onDestroy");
        ReceiptofiApplication.homeActivityPaused();
        AppUtils.setHomePageContext(null);
        Log.d(TAG, "Done onDestroy!!");
    }

    private void showErrorMsg(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
