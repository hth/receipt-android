package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptListActivity;
import com.receiptofi.checkout.adapters.ReceiptListAdapter;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptGroupObservable;
import com.receiptofi.checkout.model.ReceiptModel;

import java.util.LinkedList;
import java.util.List;

/**
 * User: PT
 * Date: 1/1/15 12:44 PM
 */
public class ReceiptListFragment extends Fragment {

    public static final int RECEIPT_MODEL_UPDATED = 0x2436;
    private static final String TAG = ReceiptListFragment.class.getSimpleName();
    public static List<ReceiptGroupHeader> groups = new LinkedList<>();
    public static List<List<ReceiptModel>> children = new LinkedList<>();
    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case RECEIPT_MODEL_UPDATED:
                    Log.d(TAG, "receiptGroupObserver onChanged");
                    groups = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptGroupHeaders();
                    children = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptModels();
                    ((ReceiptListAdapter) explv.getExpandableListAdapter()).notifyDataSetChanged();
                    int groupPosition = ((ReceiptListActivity) getActivity()).getGroupIndex();
                    int childPosition = ((ReceiptListActivity) getActivity()).getChildIndex();

                    // we wouldn't want to run this, if there is nothing selected in list
                    if (groupPosition != -1 && childPosition != -1) {
                        Log.d(TAG, "groupPosition at start is: " + groupPosition + " childPosition at start is: " + childPosition);
                        if (groups.size() > groupPosition) {
                            if (children.get(groupPosition).size() > childPosition) {
                                // In this case there is current group has a child at same postion
                                Log.d(TAG, "Keeping group and child position as it is");
                                Log.d(TAG, "groupPosition line 58 is: " + groupPosition + " childPosition is: " + childPosition);
                                ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                            } else {
                                // In this case last child was deleted from current group, but the group have more children
                                ((ReceiptListActivity) getActivity()).setChildIndex(--childPosition);
                                childPosition = ((ReceiptListActivity) getActivity()).getChildIndex();
                                Log.d(TAG, "Reducing child position");
                                Log.d(TAG, "groupPosition line 64 is: " + groupPosition + " childPosition is: " + childPosition);
                                if (explv != null) {
                                    explv.expandGroup(groupPosition);
                                    int index = explv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                                    explv.setItemChecked(index, true);
                                }
                                ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                            }
                        } else {
                            // In this case last child was deleted from current group, but the group doesn't has more children
                            ((ReceiptListActivity) getActivity()).setGroupIndex(--groupPosition);
                            groupPosition = ((ReceiptListActivity) getActivity()).getGroupIndex();
                            ((ReceiptListActivity) getActivity()).setChildIndex((children.get(groupPosition).size()) - 1);
                            childPosition = ((ReceiptListActivity) getActivity()).getChildIndex();
                            Log.d(TAG, "Reducing group position & setting child position");
                            Log.d(TAG, "groupPosition line 71 is: " + groupPosition + " childPosition is: " + childPosition);
                            if (explv != null) {
                                explv.expandGroup(groupPosition);
                                int index = explv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                                explv.setItemChecked(index, true);
                            }
                            ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });
    
    public static ReceiptGroupObservable receiptGroupObservable = ReceiptGroupObservable.getInstance();
    private ExpandableListView explv;
    private OnReceiptSelectedListener mCallback;
    private DataSetObserver receiptGroupObserver;

    public ReceiptListFragment() {
        super();
        if (receiptGroupObservable != null) {
            groups = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptGroupHeaders();
            children = ReceiptGroupObservable.getMonthlyReceiptGroup().getReceiptModels();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiptGroupObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        receiptGroupObservable.registerObserver(receiptGroupObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        receiptGroupObservable.unregisterObserver(receiptGroupObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.receipt_list_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        explv = (ExpandableListView) view.findViewById(R.id.exp_list_view);
        explv.setEmptyView(view.findViewById(R.id.empty_view));
        explv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        Log.d(TAG, "****************************        receiptGroupObserver registered");


        final ReceiptListAdapter adapter = new ReceiptListAdapter(getActivity());
        explv.setAdapter(adapter);
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);

                ((ReceiptListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
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

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /**
         * Called by HeadlinesFragment when a list item is selected
         */
        public void onReceiptSelected(int index, int position);
    }
}
