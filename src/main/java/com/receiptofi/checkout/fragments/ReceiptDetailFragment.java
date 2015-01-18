package com.receiptofi.checkout.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.ReceiptItemAdapter;
import com.receiptofi.checkout.model.ReceiptModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * Created by PT on 1/1/15.
 */
public class ReceiptDetailFragment extends Fragment {

    private static final String TAG = ReceiptDetailFragment.class.getSimpleName();

    public final static String ARG_INDEX = "index";
    public final static String ARG_POSITION = "position";
    int mCurrentIndex = -1;
    int mCurrentPosition = -1;

    // Receipt detail biz info
    private TextView rdBizName;
    private TextView rdBizAddLine1;
    private TextView rdBizAddLine2;
    private TextView rdBizAddLine3;
    private TextView rdBizPhone;

    // Receipt detail date
    private TextView rdDate;

    // Receipt detail list item
    private ListView reItemsList;


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
        rdBizName = (TextView)receiptDetailView.findViewById(R.id.rd_biz_name);
        rdBizAddLine1 = (TextView)receiptDetailView.findViewById(R.id.rd_biz_add_line1);
        rdBizAddLine2 = (TextView)receiptDetailView.findViewById(R.id.rd_biz_add_line2);
        rdBizAddLine3 = (TextView)receiptDetailView.findViewById(R.id.rd_biz_add_line3);
        rdBizPhone = (TextView)receiptDetailView.findViewById(R.id.rd_biz_phone);

        rdDate = (TextView)receiptDetailView.findViewById(R.id.rd_date);

        reItemsList = (ListView)receiptDetailView.findViewById(R.id.rd_items_list);

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
        try {
        ReceiptModel rdModel = ReceiptListFragment.childListGroup.get(index).get(position);
        rdBizName.setText(rdModel.getBizName());

        StringTokenizer tokenizer = new StringTokenizer(rdModel.getAddress(), ",");
        if(tokenizer.countTokens() <= 4){
            rdBizAddLine1.setText((tokenizer.nextToken()).trim());
            String addressLine2 = "";
            while (tokenizer.hasMoreTokens()){
                addressLine2 = addressLine2 + tokenizer.nextToken() + ",";
            }
            addressLine2 = addressLine2.replaceAll(",$", "");
            rdBizAddLine2.setText(addressLine2.trim());
            rdBizAddLine3.setVisibility(View.GONE);
        } else {
            rdBizAddLine1.setText((tokenizer.nextToken()).trim());
            rdBizAddLine2.setText((tokenizer.nextToken()).trim());
            String addressLine3 = "";
            while (tokenizer.hasMoreTokens()){
                addressLine3 = addressLine3 + tokenizer.nextToken() + ",";
            }
            addressLine3 = addressLine3.replaceAll(",$", "");
            rdBizAddLine3.setText(addressLine3.trim());
            rdBizAddLine3.setVisibility(View.VISIBLE);
        }
            rdBizPhone.setText(rdModel.getPhone());

        DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        DateFormat outputDF = new SimpleDateFormat("MMM dd',' yyyy HH:mm a");
        String formattedDate = outputDF.format(inputDF.parse(rdModel.getDate()));
        rdDate.setText(formattedDate);


            // Set Adaptor on items list
        reItemsList.setAdapter(new ReceiptItemAdapter(getActivity(), rdModel.getReceiptItems()));

        // Update trackers
        mCurrentIndex = index;
        mCurrentPosition = position;

        } catch (ParseException e) {
            Log.d(TAG, "ParseException " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
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