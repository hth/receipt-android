package com.receiptofi.checkout;

import android.app.Activity;
import android.app.SearchManager;
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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.service.ChartService;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;
import com.receiptofi.checkout.views.FlowLayout;
import com.receiptofi.checkout.views.ToastBox;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends Activity implements OnChartValueSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final DateFormat DF_MMM = new SimpleDateFormat("MMM yyyy", Locale.US);
    public static final DateFormat DF_YYYY_MM = new SimpleDateFormat("yyyy MM", Locale.US);

    public static final int IMAGE_UPLOAD_SUCCESS = 0x2563;
    public static final int IMAGE_ALREADY_QUEUED = 0x2564;
    public static final int IMAGE_ADDED_TO_QUEUED = 0x2565;
    public static final int IMAGE_UPLOAD_FAILURE = 0x2566;
    public static final int UPDATE_UNPROCESSED_COUNT = 0x2567;
    public static final int UPDATE_MONTHLY_EXPENSE = 0x2568;
    public static final int UPDATE_EXP_BY_BIZ_CHART = 0x2569;
    public static final int GET_ALL_RECEIPTS = 0x2570;

    private SearchView searchView;

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    private TextView unprocessedDocumentCount;
    private String unprocessedValue;
    private TextView currentMonthExp;
    private String currentMonthExpValue;
    private Menu optionMenu;
    private PieChart mChart;
    private TextView emptyChart;
    private PieData expByBizData;
    private boolean expByBizAnimate = false;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
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
                case IMAGE_ADDED_TO_QUEUED:
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
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

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
            Log.e(TAG, "Exception" + e.getMessage(), e);
        }
        updateChartData();
        Log.d(TAG, "Done onCreate!!");
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
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.home_page);
            instantiateViews();
            bindValuesToViews();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.home_page);
            instantiateViews();
            bindValuesToViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                DeviceService.getNewUpdates(this);
                return true;
            case R.id.menu_notofication:
                launchNotifications();
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

    private void instantiateViews() {
        unprocessedDocumentCount = (TextView) findViewById(R.id.processing_info);
        currentMonthExp = (TextView) findViewById(R.id.current_amount);
        mChart = (PieChart) findViewById(R.id.pie_chart);
        emptyChart = (TextView) findViewById(R.id.empty_chart);
        setUpChartView();
    }

    private void bindValuesToViews() {
        if (!TextUtils.isEmpty(unprocessedValue)) {
            setUnprocessedCount();
        }
        if (!TextUtils.isEmpty(currentMonthExpValue)) {
            setMonthlyExpense();
        }
        if (expByBizData != null) {
            setUpChartData();
        }
    }

    private void setUpChartView() {
        // change the color of the center-hole
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(45f);

        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        mChart.setDescription("");
        // display percentage values
        mChart.setUsePercentValues(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        mChart.setCenterText(getString(R.string.chart_desc_short));
        mChart.setCenterTextSize(14f);
        mChart.setCenterTextColor(getResources().getColor(R.color.app_theme_txt_color));
        mChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
    }

    private void updateChartData() {
        expByBizData = ChartService.getPieData();
        if (expByBizData.getXVals().size() > 0) {
            if (mChart.getVisibility() == View.GONE) {
                mChart.setVisibility(View.VISIBLE);
                emptyChart.setVisibility(View.GONE);
            }
            if (expByBizAnimate) {
                mChart.animateXY(1500, 1500);
                // mChart.spin(2000, 0, 360);
                expByBizAnimate = false;
            }
            mChart.setData(expByBizData);

            // undo all highlights
            mChart.highlightValues(null);

            mChart.invalidate();
            addLegend();
        } else {
            mChart.setVisibility(View.GONE);
            emptyChart.setVisibility(View.VISIBLE);
        }
    }

    private void setUpChartData() {
        if (expByBizData.getXVals().size() > 0) {
            if (mChart.getVisibility() == View.GONE) {
                mChart.setVisibility(View.VISIBLE);
                emptyChart.setVisibility(View.GONE);
            }
            mChart.setData(expByBizData);

            // undo all highlights
            mChart.highlightValues(null);

            mChart.invalidate();
            addLegend();
        } else {
            mChart.setVisibility(View.GONE);
            emptyChart.setVisibility(View.VISIBLE);
        }
    }

    private void addLegend() {
        Legend legend = mChart.getLegend();
        int[] colorCodes = legend.getColors();
        String[] labelArr = legend.getLegendLabels();
        legend.setEnabled(false);

        FlowLayout ll = (FlowLayout) findViewById(R.id.legend_layout);
        ll.removeAllViews();
        for (int i = 0; i < colorCodes.length; i++) {

            LinearLayout legendLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.legend_item, null);

            View legendColor = legendLayout.getChildAt(0);
            legendColor.setBackgroundColor(colorCodes[i]);

            TextView legendText = (TextView) legendLayout.getChildAt(1);
            legendText.setText(labelArr[i]);

            ll.addView(legendLayout);
        }
    }

    /**
     * On Pie selection.
     *
     * @param entry
     * @param index
     * @param h
     */
    @Override
    public void onValueSelected(Entry entry, int index, Highlight h) {
        String bizName = expByBizData.getXVals().get(entry.getXIndex());
        Log.d(TAG, "bizName is: " + bizName);
        Intent intent = new Intent(this, FilterListActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_FILTER_TYPE, Constants.ReceiptFilter.FILTER_BY_BIZ_AND_MONTH.getValue());
        intent.putExtra(Constants.INTENT_EXTRA_BIZ_NAME, bizName);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected() {

    }

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
        startActivity(new Intent(this, ReceiptListActivity.class));
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
                    startAnimation();
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

                startAnimation();
                ImageUpload.process(this, capturedImgFile);
            }
        }

    }

    private void startAnimation() {
        if (null != optionMenu) {
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
        if (null != item.getActionView()) {
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

    private void launchNotifications() {
        startActivity(new Intent(this, NotificationActivity.class));
    }

    private void launchSettings() {
        startActivity(new Intent(this, PreferencesTabActivity.class));
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
        ToastBox.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
