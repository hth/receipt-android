package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.receiptofi.checkout.FilterListActivity;
import com.receiptofi.checkout.LaunchActivity;
import com.receiptofi.checkout.NotificationActivity;
import com.receiptofi.checkout.PreferencesTabActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
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

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = HomeFragment.class.getSimpleName();
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
    private View view;
    protected PtrFrameLayout mPtrFrameLayout;

    private OnFragmentInteractionListener mListener;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case IMAGE_UPLOAD_SUCCESS:
                    unprocessedValue = Integer.toString(msg.arg1);
                    setUnprocessedCount();
                    showErrorMsg((String) msg.obj);
//                    endAnimation();
                    break;
                case IMAGE_UPLOAD_FAILURE:
                    showErrorMsg((String) msg.obj);
//                    endAnimation();
                    break;
                case IMAGE_ADDED_TO_QUEUED:
//                    endAnimation();
                    break;
                case IMAGE_ALREADY_QUEUED:
                    showErrorMsg((String) msg.obj);
//                    endAnimation();
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
                    updateChartData();
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);
        /**
         * Setup Material design pull to refresh
         */
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.material_style_ptr_frame);
        // header
        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(15), 0, dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
//        mPtrFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrameLayout.autoRefresh(false);
//            }
//        }, 100);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if(scrollView.getScrollY() == 0)
                    return true;
                else
                    return false;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                // Below is the real refresh event.
                DeviceService.getNewUpdates(getActivity());
                // We make a deley 10s in case on internet resposne during the refresh.
//                long delay = (long) (1000 + Math.random() * 1000);
//                    delay = Math.max(0, delay);
                long delay = (long)10000;
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
            String[] monthDay = DF_YYYY_MM.format(new Date()).split(" ");
            currentMonthExpValue = MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]);
            setMonthlyExpense();
        } catch (Exception e) {
            Log.e(TAG, "Exception" + e.getMessage(), e);
        }
        updateChartData();
        Log.d(TAG, "Done onCreate!!");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void instantiateViews() {
        unprocessedDocumentCount = (TextView) view.findViewById(R.id.processing_info);
        currentMonthExp = (TextView) view.findViewById(R.id.current_amount);
        mChart = (PieChart) view.findViewById(R.id.pie_chart);
        emptyChart = (TextView) view.findViewById(R.id.empty_chart);
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

        FlowLayout ll = (FlowLayout) view.findViewById(R.id.legend_layout);
        ll.removeAllViews();
        for (int i = 0; i < colorCodes.length; i++) {

            LinearLayout legendLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.legend_item, null);

            View legendColor = legendLayout.getChildAt(0);
            legendColor.setBackgroundColor(colorCodes[i]);

            TextView legendText = (TextView) legendLayout.getChildAt(1);
            legendText.setText(labelArr[i]);

            ll.addView(legendLayout);
        }
    }

    private void setUnprocessedCount() {
        Log.d(TAG, "executing setUnprocessedCount");
        Activity activity = getActivity();
        if(activity != null) {
            unprocessedDocumentCount.setText(getString(R.string.processing_info, unprocessedValue));
        } else {
            Log.d(TAG, "setUnprocessedCount the response of getActivity() is null.");
        }
    }

    private void setMonthlyExpense() {
        Log.d(TAG, "executing setMonthlyExpense");
        currentMonthExp.setText(getString(R.string.monthly_amount, DF_MMM.format(new Date()), currentMonthExpValue));
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
        Intent intent = new Intent(getActivity(), FilterListActivity.class);
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
            showErrorMsg("some error occurred !!");
        }
    }

    public void chooseImage(View view) {
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(g, RESULT_IMAGE_GALLERY);
    }

    public void invokeReceiptList(View view) {
        Log.d(TAG, "InvokeReceiptList getActivity is: " + getActivity());
        startActivity(new Intent(getActivity(), ReceiptListActivity.class));
    }


    private void showErrorMsg(String msg) {
        ToastBox.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void endAnimation() {
        MenuItem item = optionMenu.findItem(R.id.menu_refresh);
        if (null != item.getActionView()) {
            // Remove the animation.
            item.getActionView().clearAnimation();
            item.setActionView(null);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getActivity().getResources().getDisplayMetrics());
    }

}
