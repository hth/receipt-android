package com.receiptofi.checkout.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.BillingAccountModel;
import com.receiptofi.checkout.model.BillingHistoryModel;
import com.receiptofi.checkout.utils.db.BillingAccountUtils;

/**
 * Created by PT on 4/9/15.
 */
public class BillingFragment extends Fragment {

    private static final String TAG = BillingFragment.class.getSimpleName();

    private TextView billingPlan;
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

        billingPlan = (TextView)rootView.findViewById(R.id.billing_plan_value);
        billingDate = (TextView)rootView.findViewById(R.id.billing_date_value);


        billingHistoryList = (ListView)rootView.findViewById(R.id.billing_history_list);
        billingHistoryList.setAdapter(new BillingListAdapter(getActivity()));

        new BillingDataTask().execute();
        return rootView;
    }

    private void showData() {
        billingPlan.setText(billingAccountData.getAccountBillingType());
        billingDate.setText(billingAccountData.isBilledAccount() + "");
        ((BillingListAdapter)billingHistoryList.getAdapter()).notifyDataSetChanged();
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
                    holder.billingDate = (TextView) convertView.findViewById(R.id.billing_history_list_item_date);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.billingMonth.setText(getItem(position).getBilledForMonth());
                holder.billingPlan.setText(getItem(position).getAccountBillingType());
                holder.billingDate.setText(getItem(position).getBilledStatus());
                return convertView;
            } catch (Exception e) {
                Log.d(TAG, "Exception " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private class ViewHolder {
            TextView billingMonth;
            TextView billingPlan;
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
            Log.d(TAG, "!!!!! query finished - sending notification to fragment ");
            showData();
        }
    }
}