package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.receiptofi.checkout.FilterListActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.FilterListAdapter;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by PT on 3/28/15.
 */
public class FilterListFragment extends Fragment {

    private static final String TAG = FilterListFragment.class.getSimpleName();
    public static List<ReceiptGroupHeader> groups = new LinkedList<>();
    public static List<List<ReceiptModel>> children = new LinkedList<>();
    public static boolean hideTotal;
    private View rootView;
    private ExpandableListView explv;
    private OnReceiptSelectedListener mCallback;

    public FilterListFragment() {
        super();
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

        final FilterListAdapter adapter = new FilterListAdapter(getActivity());
        explv.setAdapter(adapter);
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);

                ((FilterListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
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

    public void notifyDataChanged(ReceiptGroup receiptGroup) {
        groups = receiptGroup.getReceiptGroupHeaders();
        children = receiptGroup.getReceiptModels();
        hideTotal = ((FilterListActivity) getActivity()).hideTotal();
        ((FilterListAdapter) explv.getExpandableListAdapter()).notifyDataSetChanged();
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /**
         * Called by HeadlinesFragment when a list item is selected
         */
        public void onReceiptSelected(int index, int position);
    }
}
