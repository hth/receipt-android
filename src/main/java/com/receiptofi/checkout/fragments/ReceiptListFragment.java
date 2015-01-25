package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
import com.receiptofi.checkout.adapters.ReceiptListAdapter;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;

import java.util.List;

/**
 * Created by PT on 1/1/15.
 */
public class ReceiptListFragment extends Fragment {

    private static final String TAG = ReceiptListFragment.class.getSimpleName();

    private View rootView;
    private ExpandableListView explv;
    public static List<ReceiptGroupHeader> groups;
    public static List<List<ReceiptModel>> children;

    private OnReceiptSelectedListener mCallback;
    public static ReceiptGroup receiptGroup = ReceiptGroup.getInstance();

    public static final int RECEIPT_MODEL_UPLOADED = 0x2436;

    public final Handler updateHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case RECEIPT_MODEL_UPLOADED:
                    if(receiptGroup != null) {
                    Log.d(TAG, "receiptGroupObserver onChanged");
                    groups = receiptGroup.getReceiptGroupHeaders();
                    children = receiptGroup.getReceiptModels();
                    ((ReceiptListAdapter)explv.getExpandableListAdapter()).notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onReceiptSelected(int index, int position);
    }

    public ReceiptListFragment() {
        super();
        if(receiptGroup != null) {
            groups = receiptGroup.getReceiptGroupHeaders();
            children = receiptGroup.getReceiptModels();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        explv.setEmptyView(view.findViewById(R.id.empty_view));

        DataSetObserver receiptGroupObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPLOADED);
            }
        };
        receiptGroup.registerObserver(receiptGroupObserver);
        Log.d(TAG, "****************************        receiptGroupObserver registered");


        final ReceiptListAdapter adapter = new ReceiptListAdapter(getActivity());
        explv.setAdapter(adapter);
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
