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
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
import com.receiptofi.checkout.adapters.ReceiptListAdapter;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;

import java.util.LinkedList;
import java.util.List;

/**
 * User: PT
 * Date: 1/1/15 12:44 PM
 */
public class ReceiptListFragment extends Fragment {

    private static final String TAG = ReceiptListFragment.class.getSimpleName();

    private View rootView;
    private ExpandableListView explv;
    public static List<ReceiptGroupHeader> groups = new LinkedList<>();
    public static List<List<ReceiptModel>> children = new LinkedList<>();

    private OnReceiptSelectedListener mCallback;
    public static ReceiptGroup receiptGroup = ReceiptGroup.getInstance();

    public static final int RECEIPT_MODEL_UPDATED = 0x2436;

    public final Handler updateHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case RECEIPT_MODEL_UPDATED:
                    // TODO: run enough test to make sure not checking null won't cause any issues
                    //if(receiptGroup != null) {
                    Log.d(TAG, "receiptGroupObserver onChanged");
                    groups = receiptGroup.getReceiptGroupHeaders();
                    children = receiptGroup.getReceiptModels();
                    ((ReceiptListAdapter)explv.getExpandableListAdapter()).notifyDataSetChanged();
                    int groupPosition = ((ReceiptListActivity)getActivity()).getGroupIndex();
                    int childPosition = ((ReceiptListActivity)getActivity()).getChildIndex();
                    ((ReceiptListActivity)getActivity()).onReceiptSelected(groupPosition, childPosition);

                    //try{
                       // ReceiptModel receiptModel = children.get(groupPosition).get(childPosition);

                   /* } catch (Exception e){
                        Log.d(TAG, e.getMessage() + " seems like this receipt is not available");
                        e.printStackTrace();
                    } */

                    //}
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
        explv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        DataSetObserver receiptGroupObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
            }
        };
        receiptGroup.registerObserver(receiptGroupObserver);
        Log.d(TAG, "****************************        receiptGroupObserver registered");


        final ReceiptListAdapter adapter = new ReceiptListAdapter(getActivity());
        explv.setAdapter(adapter);
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);

                ((ReceiptListActivity)getActivity()).onReceiptSelected(groupPosition, childPosition);
                return false;
            }
        });
        // TODO for testing only
        //updateHandler.postDelayed(r, 12000);
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

    // TODO for testing only
    final Runnable r = new Runnable()
    {
        public void run()
        {
            Log.d(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% \n %%%%%%%%%%%%%%%%%%%");
            Log.d(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% \n %%%%%%%%%%%%%%%%%%%");
            Log.d(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% \n %%%%%%%%%%%%%%%%%%%");
            int numHeaders = receiptGroup.getReceiptGroupHeaders().size();
            int numChildGroup = receiptGroup.getReceiptModels().size();

            receiptGroup.getReceiptModels().get(0).remove(0);
           // receiptGroup.getReceiptGroupHeaders().remove(numHeaders-1);
           // receiptGroup.getReceiptModels().remove(numChildGroup-1);
            /*
            for (int index = numHeaders -1; index > -1; index--){
                Log.d(TAG, "numHeaders is: " + numHeaders  + "index is: " + index);
                receiptGroup.getReceiptGroupHeaders().remove(index);
            }
            */
            /*
            for (int index =numChildGroup-1; index > -1; index--){
                Log.d(TAG, "numChildGroup is: " + numChildGroup  + "index is: " + index);
                receiptGroup.getReceiptModels().remove(index);
            }
            */
            updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
        }
    };
}
