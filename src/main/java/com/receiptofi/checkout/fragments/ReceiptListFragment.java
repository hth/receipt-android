package com.receiptofi.checkout.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
import com.receiptofi.checkout.adapters.ReceiptListAdapter;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;

import java.util.List;

/**
 * Created by PT on 1/1/15.
 */
public class ReceiptListFragment extends Fragment {

    View rootView;
    ExpandableListView explv;
    List<ReceiptGroupHeader> headerList;
    public static List<List<ReceiptModel>> childListGroup;

    OnReceiptSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onReceiptSelected(int index, int position);
    }

    public ReceiptListFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data for master/detail views
        ReceiptGroup receiptGroup = MonthlyReportUtils.fetchMonthly();
        if(receiptGroup != null) {
            List<ReceiptGroupHeader> headerList = receiptGroup.getReceiptGroupHeaders();
            List<List<ReceiptModel>> childListGroup = receiptGroup.getReceiptGroup();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.receipt_list_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        explv = (ExpandableListView) view.findViewById(R.id.exp_list_view);
        explv.setAdapter(new ReceiptListAdapter(getActivity(), headerList, childListGroup));
        explv.setGroupIndicator(null);

        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                ((ReceiptListActivity)getActivity()).onReceiptSelected(groupPosition, childPosition);
                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnReceiptSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnReceiptSelectedListener");
        }
    }
}
