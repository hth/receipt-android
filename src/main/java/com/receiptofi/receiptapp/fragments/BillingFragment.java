package com.receiptofi.receiptapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.BillingAccountModel;
import com.receiptofi.receiptapp.model.BillingHistoryModel;
import com.receiptofi.receiptapp.utils.db.BillingAccountUtils;

/**
 * User: PT
 * Date: 4/9/15 7:30 PM
 */
public class BillingFragment extends Fragment {
    private static final String TAG = BillingFragment.class.getSimpleName();

    private TextView billingTitle, billingHistoryTitle, billingPlan;
    private TextView billingDate;
    private ListView billingHistoryList;
    private BillingAccountModel billingAccountData = new BillingAccountModel();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.billing_fragment, container, false);

        billingPlan = (TextView) rootView.findViewById(R.id.billing_plan_value);
        billingTitle = (TextView) rootView.findViewById(R.id.tv_billing);
        billingHistoryTitle = (TextView) rootView.findViewById(R.id.billing_history_header);

        billingHistoryList = (ListView) rootView.findViewById(R.id.billing_history_list);
        billingHistoryList.setAdapter(new BillingListAdapter(getActivity()));

        setupView();

        new BillingDataTask().execute();
        return rootView;
    }

    private void setupView() {
        billingTitle.setPaintFlags(billingTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        billingHistoryTitle.setPaintFlags(billingHistoryTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    private void showData() {
        if (billingAccountData != null) {
            billingPlan.setText(billingAccountData.displayBillingType() + "");
            ((BillingListAdapter) billingHistoryList.getAdapter()).notifyDataSetChanged();
        }
    }

    public class BillingListAdapter extends ArrayAdapter<BillingHistoryModel> {

        private final String TAG = BillingListAdapter.class.getSimpleName();
        private final LayoutInflater inflater;

        public BillingListAdapter(Context context) {
            super(context, R.layout.billing_history_list_item, billingAccountData.getBillingHistories());
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return billingAccountData.getBillingHistories().size();
        }

        @Override
        public BillingHistoryModel getItem(int position) {
            return billingAccountData.getBillingHistories().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.billing_history_list_item, parent, false);

                    holder = new ViewHolder();
                    holder.billingMonth = (TextView) convertView.findViewById(R.id.billing_history_list_item_month);
                    holder.billingPlan = (TextView) convertView.findViewById(R.id.billing_history_list_item_plan);
                    holder.billingStatus = (TextView) convertView.findViewById(R.id.billing_history_list_item_bill_status);
                    holder.billingDate = (TextView) convertView.findViewById(R.id.billing_history_list_item_date);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.billingMonth.setText(getItem(position).displayBilledMonth());
                holder.billingPlan.setText(getItem(position).displayBillingType());
                holder.billingStatus.setText("Billed");
                holder.billingDate.setText(getItem(position).displayBilledInfo());
                if (getItem(position).displayBilledInfo().equalsIgnoreCase("Payment Due")) {
                    holder.billingDate.setTextColor(Color.RED);
                } else {
                    holder.billingDate.setTextColor(getResources().getColor(R.color.gray_dark));
                }
                return convertView;
            } catch (Exception e) {
                Log.d(TAG, "reason=" + e.getMessage(), e);
            }
            return null;
        }

        private class ViewHolder {
            TextView billingMonth;
            TextView billingPlan;
            TextView billingStatus;
            TextView billingDate;
        }
    }

    private class BillingDataTask extends AsyncTask<Void, Void, BillingAccountModel> {

        @Override
        protected BillingAccountModel doInBackground(Void... args) {
            return BillingAccountUtils.getBillingAccount();
        }

        @Override
        protected void onPostExecute(BillingAccountModel billingAccountModel) {
            billingAccountData = billingAccountModel;
            Log.d(TAG, "Completed querying, sending notification to fragment");
            showData();
        }
    }
}