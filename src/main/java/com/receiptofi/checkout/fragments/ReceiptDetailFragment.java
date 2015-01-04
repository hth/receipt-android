package com.receiptofi.checkout.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.receiptofi.checkout.R;

/**
 * Created by PT on 1/1/15.
 */
public class ReceiptDetailFragment extends Fragment {

    private static final String TAG = ReceiptDetailFragment.class.getSimpleName();

    public final static String ARG_INDEX = "index";
    public final static String ARG_POSITION = "position";
    int mCurrentIndex = -1;
    int mCurrentPosition = -1;

    private TextView receiptDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "executing onCreateView");
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(ARG_INDEX);
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        View receiptDetailView = inflater.inflate(R.layout.receipt_detail_view, container, false);
        receiptDetail = (TextView)receiptDetailView.findViewById(R.id.receipt_detail);

        return receiptDetailView;

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "executing onStart");

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateReceiptDetailView(args.getInt(ARG_INDEX), args.getInt(ARG_POSITION));
        } else if (mCurrentIndex != -1 && mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateReceiptDetailView(mCurrentIndex, mCurrentPosition);
        }
    }

    public void updateReceiptDetailView(int index, int position) {
        Log.d(TAG, "executing updateReceiptDetailView");
        receiptDetail.setText(ReceiptListFragment.children[index][position]);
        mCurrentIndex = index;
        mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "executing onSaveInstanceState");

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_INDEX, mCurrentIndex);
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
}