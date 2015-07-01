package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.NotificationAdapter;
import com.receiptofi.checkout.model.NotificationModel;
import com.receiptofi.checkout.utils.db.NotificationUtils;

import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class NotificationFragment extends Fragment {
    private static final String TAG = NotificationFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View mView;
    protected PtrFrameLayout mPtrFrameLayout;
    private ListView listView;
    private TextView mEmptyTextview;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_notification, container, false);
        setupView();
        // start query
        new NotificationDataTask().execute();
        return mView;
    }

    private void setupView() {
        listView = (ListView) mView.findViewById(R.id.notification_list);
        mEmptyTextview = (TextView) mView.findViewById(R.id.empty_view);
        /**
         * Setup Material design pull to refresh
         */
        mPtrFrameLayout = (PtrFrameLayout) mView.findViewById(R.id.material_style_ptr_frame);
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

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (listIsAtTop())
                    return true;
                else
                    return false;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                // Below is the real refresh event.
                new NotificationDataTask().execute();
                // We make a deley 10s in case on internet resposne during the refresh.
//                long delay = (long) (1000 + Math.random() * 1000);
//                    delay = Math.max(0, delay);
                long delay = (long) 10000;
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, delay);
            }
        });
    }

    private void setListData(List<NotificationModel> notificationModels) {
        listView.setAdapter(new NotificationAdapter(getActivity(), notificationModels));
    }

    private class NotificationDataTask extends AsyncTask<Void, Void, List<NotificationModel>> {

        @Override
        protected List<NotificationModel> doInBackground(Void... voids) {
            return NotificationUtils.getAll();
        }

        @Override
        protected void onPostExecute(List<NotificationModel> notificationModels) {
            if (mPtrFrameLayout != null) {
                mPtrFrameLayout.refreshComplete();
            }
            setListData(notificationModels);
            // Show up the empty view when this user has empty notification.
            if (notificationModels.size() == 0) {
                mEmptyTextview.setVisibility(View.VISIBLE);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getActivity().getResources().getDisplayMetrics());
    }

    private boolean listIsAtTop() {
        if (listView.getChildCount() == 0) return true;
        return listView.getChildAt(0).getTop() == 0;
    }
}
