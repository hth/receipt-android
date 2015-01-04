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

/**
 * Created by PT on 1/1/15.
 */
public class ReceiptListFragment extends Fragment {

    View rootView;
    ExpandableListView explv;
    private String[] groups;
    public static String[][] children;

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

        groups = new String[]{"Test Header 1", "Test Header 2", "Test Header 3", "Test Header 4"};

        children = new String[][]{
                {"s simply dummy text of the printing and..."},
                {"Contrary to popular belief, Lorem Ipsum is...", "\"Lorem ipsum\" pseudo-latin is also used as placeholder text"},
                {"It is a long established fact that a reader..."},
                {"There are many variations of passages of..."}
        };
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
        explv.setAdapter(new ReceiptListAdapter(getActivity(), groups, children));
        explv.setGroupIndicator(null);

        /*
        explv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long id) {
                ((ReceiptListActivity)getActivity()).onReceiptSelected(groupPosition, 0);
                return false;
            }
        });
         */



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
