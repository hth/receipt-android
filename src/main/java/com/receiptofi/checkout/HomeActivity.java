package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private PieChart mChart;

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
        mChart = (PieChart) findViewById(R.id.pie_chart);
        setUpChart();
        prepareChartData();

        setUnprocessedCount(KeyValueUtils.getValue(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT));

        try {
            String[] monthDay = DF_YYYY_MM.format(new Date()).split(" ");
            setMonthlyExpense(MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]));
        } catch (Exception e) {
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

    private void setUpChart(){
        // change the color of the center-hole
        mChart.setHoleColor(R.color.hole_color);

        // Causing java.lang.RuntimeException: native typeface cannot be made
        mChart.setValueTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf"));
        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf"));

        mChart.setHoleRadius(55f);

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

        // TODO
        //setData(3, 100);

        mChart.animateXY(1500, 1500);
        // mChart.spin(2000, 0, 360);


        /*Legend l = mChart.getLegend();
        if(l != null){
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(5f);
        }
        */
    }

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    private void prepareChartData(){
        // TODO
        float mult = 100;
        int count = 3;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet set1 = new PieDataSet(yVals1, "Exp/Business");
        set1.setSliceSpace(3f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);



        colors.add(ColorTemplate.getHoloBlue());

        set1.setColors(colors);

        PieData data = new PieData(xVals, set1);
        mChart.setData(data);

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

    private void setUnprocessedCount(String count) {
        Log.d(TAG, "executing setUnprocessedCount");
        unprocessedDocumentCount.setText(getString(R.string.processing_info, count));
    }

    private void setMonthlyExpense(String amount) {
        Log.d(TAG, "executing setMonthlyExpense");
        currentMonthExp.setText(getString(R.string.monthly_amount, DF_MMM.format(new Date()), amount));
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
