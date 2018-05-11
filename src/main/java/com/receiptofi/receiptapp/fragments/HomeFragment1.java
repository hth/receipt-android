package com.receiptofi.receiptapp.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.receiptofi.receiptapp.HomeActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.service.ChartService;
import com.receiptofi.receiptapp.service.DeviceService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.utils.db.MonthlyReportUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment1 extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    private static  final String TAG = HomeFragment1.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    public static final int IMAGE_UPLOAD_SUCCESS = 0x2563;
    public static final int IMAGE_ALREADY_QUEUED = 0x2564;
    public static final int IMAGE_ADDED_TO_QUEUED = 0x2565;
    public static final int IMAGE_UPLOAD_FAILURE = 0x2566;
    public static final int UPDATE_UNPROCESSED_COUNT = 0x2567;
    public static final int UPDATE_MONTHLY_EXPENSE = 0x2568;
    public static final int UPDATE_EXP_BY_BIZ_CHART = 0x2569;
    public static final int GET_ALL_RECEIPTS = 0x2570;
    private SuperActivityToast uploadImageToast;
    private String currentMonthExpValue;
    private String unprocessedValue;
    private static final DateFormat DF_MMM = new SimpleDateFormat("MMM yyyy", Locale.US);
    public static final DateFormat DF_YYYY_MM = new SimpleDateFormat("yyyy MM", Locale.US);
    private boolean expByBizAnimate = false;

    private TextView currentMonthExp;
    //UI variables

private  LinearLayout llTakePhoto,llChoosePhoto;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected PtrFrameLayout mPtrFrameLayout;
    private View view;
    private TextView unprocessedDocumentCount;
    private PieChart mChart;
    private PieData expByBizData;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_UPLOAD_SUCCESS:
                    unprocessedValue = Integer.toString(msg.arg1);
                    setUnprocessedCount();
                    showMessage((String) msg.obj, SuperToast.Background.BLUE);
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).stopProgressToken();
                    }
                    break;
                case IMAGE_UPLOAD_FAILURE:
                    showMessage((String) msg.obj, SuperToast.Background.RED);
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).stopProgressToken();
                    }
                    break;
                case IMAGE_ADDED_TO_QUEUED:
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).stopProgressToken();
                    }
                    showMessage((String) msg.obj, SuperToast.Background.BLUE);
                    break;
                case IMAGE_ALREADY_QUEUED:
                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).stopProgressToken();
                    }
                    showMessage((String) msg.obj, SuperToast.Background.GRAY);
                    break;
                case UPDATE_UNPROCESSED_COUNT:
                    unprocessedValue = (String) msg.obj;
                    setUnprocessedCount();
                    if (mPtrFrameLayout != null) {
                        mPtrFrameLayout.refreshComplete();
                    }
                    break;
                case UPDATE_MONTHLY_EXPENSE:
                   currentMonthExpValue = (String) msg.obj;
                    setMonthlyExpense();
                    break;
                case UPDATE_EXP_BY_BIZ_CHART:
                      expByBizAnimate = true;
                    // We must add below condition to avoid getActivity be null exception.
                    if (isAdded()) {
                        updateChartData();
                    }
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + msg.what);
            }
            return true;
        }
    });



    public HomeFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment1 newInstance(String param1, String param2) {
        HomeFragment1 fragment = new HomeFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_home1,container,false);
        llTakePhoto = (LinearLayout)view.findViewById(R.id.ll_take_photo);
        llChoosePhoto = (LinearLayout)view.findViewById(R.id.ll_choose_photo);

        /** Setup Material design pull to refresh. */
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.material_style_ptr_frame);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        // Todo : header is not added
       // mPtrFrameLayout.setHeaderView(header);
       // mPtrFrameLayout.addPtrUIHandler(header);
      mPtrFrameLayout.setPtrHandler(new PtrHandler() {
          @Override
          public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
              return false;
          }

          @Override
          public void onRefreshBegin(final PtrFrameLayout frame) {
              /** Pull down refresh gets latest updates. */
              String getAllComplete = KeyValueUtils.getValue(KeyValueUtils.KEYS.GET_ALL_COMPLETE);
              if (TextUtils.isEmpty(getAllComplete) || Boolean.toString(false).equals(getAllComplete)) {
                  Log.i(TAG, "Failed previously to complete " + KeyValueUtils.KEYS.GET_ALL_COMPLETE);
                  DeviceService.getAll(getActivity());
              } else {
                  DeviceService.getUpdates(getActivity());
              }
              long delay = (long) 10000;
              frame.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      frame.refreshComplete();
                  }
              }, delay);

          }
      });
        this.instantiateViews();
        // OnCreate we need to set values from Database and these will be updated later once
        // update from server is received.
        unprocessedValue = KeyValueUtils.getValue(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT);
        setUnprocessedCount();
        try {
            String[] monthDay = DF_YYYY_MM.format(new Date()).split(Pattern.quote(" "));
            currentMonthExpValue = MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]);
           setMonthlyExpense();
        } catch (Exception e) {
            Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
        updateChartData();

        llTakePhoto.setOnClickListener(this);
        llChoosePhoto.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.ll_take_photo:
                takePhoto();
                break;
            case R.id.ll_choose_photo:
                chooseImage();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IMAGE_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageGallery = data.getData();
            String imageAbsolutePath = AppUtils.getImageFileFromURI(getActivity(), imageGallery);
            if (null != imageAbsolutePath) {
                File pickerImage = new File(imageAbsolutePath);
                if ((pickerImage.length() / 1048576) >= 10) {
                    showToastMsg("Image size of more than 10MB not supported.",
                            SuperToast.Background.RED,
                            SuperToast.Duration.MEDIUM);
                } else {
                    startProgressToken();
                    ImageUpload.process(getActivity(), imageAbsolutePath);
                }
            }

        }

        else if (requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            String capturedImgFile = AppUtils.getImageFilePath();
            if (null != capturedImgFile) {

                File capturedFile = new File(capturedImgFile);
                Uri contentUri = Uri.fromFile(capturedFile);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);

                startProgressToken();
                ImageUpload.process(getActivity(), capturedImgFile);
            }
        }

        else if (resultCode == RESULT_CANCELED) {
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


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = AppUtils.createImageFile();
        // Continue only if the File was successfully created
        if (null != photoFile) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, RESULT_IMAGE_CAPTURE);
        } else {
            showMessage("We seemed to have encountered issue saving Receipt image.", SuperToast.Background.RED);
        }
    }


    public void chooseImage() {
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(g, RESULT_IMAGE_GALLERY);
    }

    private void showMessage(final String message, final int backgroundColor) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (null != getActivity()) {
            /** getMainLooper() function of Looper class, which will provide you the Looper against the Main UI thread. */
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                    superActivityToast.setText(message);
                    superActivityToast.setDuration(SuperToast.Duration.SHORT);
                    superActivityToast.setBackground(backgroundColor);
                    superActivityToast.setTextColor(Color.WHITE);
                    superActivityToast.setTouchToDismiss(true);
                    superActivityToast.show();
                }
            });
        }
    }

    public void showToastMsg(String msg, int backgroundColor, int duration) {
        SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
        superActivityToast.setText(msg);
        superActivityToast.setDuration(duration);
        superActivityToast.setBackground(backgroundColor);
        superActivityToast.setTextColor(Color.WHITE);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();
    }

    private void startProgressToken() {
        uploadImageToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        uploadImageToast.setText("Image Uploading.");
        uploadImageToast.setIndeterminate(true);
        uploadImageToast.setProgressIndeterminate(true);
        uploadImageToast.show();
    }

    private void setUnprocessedCount() {
        Log.d(TAG, "executing setUnprocessedCount");
        Activity activity = getActivity();
        if (activity != null) {
           unprocessedDocumentCount.setText(getString(R.string.processing_info, unprocessedValue));
        } else {
            Log.d(TAG, "setUnprocessedCount the response of getActivity() is null.");
        }
    }

    private void instantiateViews() {
        //Todo ask about current month expense , empty chart
        unprocessedDocumentCount = (TextView) view.findViewById(R.id.processing_info);
       currentMonthExp = (TextView) view.findViewById(R.id.current_amount);
        mChart = (PieChart) view.findViewById(R.id.pie_chart);
       // emptyChart = (TextView) view.findViewById(R.id.empty_chart);
        setUpChartView();
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


    private void setMonthlyExpense() {
        Log.d(TAG, "executing setMonthlyExpense");
        Activity activity = getActivity();
        if (null != activity) {
            String amount = AppUtils.currencyFormatter().format(Double.valueOf(currentMonthExpValue));
            String month = DF_MMM.format(new Date());
            String monthlyExp = month +" "+ amount;
           // Spannable wordToSpan = new SpannableString(getString(R.string.monthly_amount, month, amount));
          //  wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.father_bg)), wordToSpan.length() - month.length() - PADDING_SPACE, wordToSpan.length(), 0);
          //  wordToSpan.setSpan(new RelativeSizeSpan(1.22f), wordToSpan.length() - month.length() - PADDING_SPACE, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            currentMonthExp.setText(monthlyExp);
        } else {
            Log.d(TAG, "setMonthlyExpense the response of getActivity() is null.");
        }
    }

    private void updateChartData() {
        expByBizData = ChartService.getPieData();
        if (expByBizData.getXVals().size() > 0) {
            if (mChart.getVisibility() == View.GONE) {
                mChart.setVisibility(View.VISIBLE);
              //  emptyChart.setVisibility(View.GONE);
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
           // emptyChart.setVisibility(View.VISIBLE);
        }
    }

    private void addLegend() {
        Legend legend = mChart.getLegend();
        int[] colorCodes = legend.getColors();
        String[] labelArr = legend.getLabels();
        legend.setEnabled(false);

      /*  FlowLayout ll = (FlowLayout) view.findViewById(R.id.legend_layout);
        ll.removeAllViews();*/
        for (int i = 0; i < colorCodes.length; i++) {

            LinearLayout legendLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.legend_item, null);

            View legendColor = legendLayout.getChildAt(0);
            legendColor.setBackgroundColor(colorCodes[i]);

            TextView legendText = (TextView) legendLayout.getChildAt(1);
            legendText.setText(labelArr[i]);

          //  ll.addView(legendLayout);
        }
    }


}
