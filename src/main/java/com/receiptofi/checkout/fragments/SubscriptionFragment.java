package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscribeActivity;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.http.ResponseParser;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.squareup.okhttp.Headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 6/28/15 2:12 AM
 */
public class SubscriptionFragment extends Fragment {
    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    private ListView plans;
    private List<PlanModel> planModels = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.subscription_fragment, container, false);
        plans = (ListView) rootView.findViewById(R.id.plans);
        plans.setAdapter(new PlanListAdapter(getActivity()));

        plans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onBraintreeSubmit();
            }
        });

        new PlanTask().execute();
        return rootView;
    }

    private void showData() {
        if (!planModels.isEmpty()) {
            ((PlanListAdapter) plans.getAdapter()).notifyDataSetChanged();
        }
    }

    public class PlanListAdapter extends ArrayAdapter<PlanModel> {
        private final String TAG = PlanListAdapter.class.getSimpleName();
        private final LayoutInflater inflater;

        public PlanListAdapter(Context context) {
            super(context, R.layout.subscription_plan_list_item, planModels);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return planModels.size();
        }

        @Override
        public PlanModel getItem(int position) {
            return planModels.get(position);
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
                    convertView = inflater.inflate(R.layout.subscription_plan_list_item, parent, false);
                    ImageView nextAction = (ImageView) convertView.findViewById(R.id.subscription_plan_list_item_next_arrow);
                    nextAction.setImageDrawable(new IconDrawable(getActivity(), Iconify.IconValue.fa_chevron_right)
                            .colorRes(R.color.app_theme_bg)
                            .sizePx(44));

                    holder = new ViewHolder();
                    holder.planName = (TextView) convertView.findViewById(R.id.subscription_plan_list_item_plan_name);
                    holder.planDescription = (TextView) convertView.findViewById(R.id.subscription_plan_list_item_plan_description);
                    holder.planPrice = (TextView) convertView.findViewById(R.id.subscription_plan_list_item_plan_price);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.planName.setText(getItem(position).getName());
                holder.planDescription.setText(getItem(position).getPlanDescription());
                holder.planPrice.setText(getItem(position).getPrice());
                return convertView;
            } catch (Exception e) {
                Log.d(TAG, "reason=" + e.getMessage(), e);
            }
            return null;
        }

        private class ViewHolder {
            TextView planName;
            TextView planDescription;
            TextView planPrice;
        }
    }

    private class PlanTask extends AsyncTask<Void, Void, List<PlanModel>> {

        @Override
        protected List<PlanModel> doInBackground(Void... args) {



            PlanModel planModel = new PlanModel("A", "B", "C", "Plan Description", "E", "Plan Name", "G", "$0.10");
            PlanModel planModel2 = new PlanModel("A", "B", "C", "Plan Description", "E", "Plan Name", "G", "$0.20");
            List<PlanModel> planModels = new ArrayList<>();
            planModels.add(planModel);
            planModels.add(planModel2);
            return planModels;
        }

        @Override
        protected void onPostExecute(List<PlanModel> plans) {
            planModels = plans;
            Log.d(TAG, "!!!!! query finished - sending notification to fragment ");
            showData();
        }
    }

    public void onBraintreeSubmit() {
        Intent intent = new Intent(getActivity(), BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiI3YjBhMmY3N2I4YzBmYjlkZWUwNTJjM2Q1MjFlN2M3MmIwOGFiMmQ5NTc3MTU0ZmIxY2Q2YzMwNjE0ZTg3NzBmfGNyZWF0ZWRfYXQ9MjAxNS0wNy0wM1QwMDo0OTowNS4zMTYzMTgzOTgrMDAwMFx1MDAyNm1lcmNoYW50X2lkPWRjcHNweTJicndkanIzcW5cdTAwMjZwdWJsaWNfa2V5PTl3d3J6cWszdnIzdDRuYzgiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL2RjcHNweTJicndkanIzcW4vY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwibWVyY2hhbnRBY2NvdW50SWQiOiJzdGNoMm5mZGZ3c3p5dHc1IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sImNvaW5iYXNlRW5hYmxlZCI6dHJ1ZSwiY29pbmJhc2UiOnsiY2xpZW50SWQiOiIxMWQyNzIyOWJhNThiNTZkN2UzYzAxYTA1MjdmNGQ1YjQ0NmQ0ZjY4NDgxN2NiNjIzZDI1NWI1NzNhZGRjNTliIiwibWVyY2hhbnRBY2NvdW50IjoiY29pbmJhc2UtZGV2ZWxvcG1lbnQtbWVyY2hhbnRAZ2V0YnJhaW50cmVlLmNvbSIsInNjb3BlcyI6ImF1dGhvcml6YXRpb25zOmJyYWludHJlZSB1c2VyIiwicmVkaXJlY3RVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbS9jb2luYmFzZS9vYXV0aC9yZWRpcmVjdC1sYW5kaW5nLmh0bWwiLCJlbnZpcm9ubWVudCI6Im1vY2sifSwibWVyY2hhbnRJZCI6ImRjcHNweTJicndkanIzcW4iLCJ2ZW5tbyI6Im9mZmxpbmUiLCJhcHBsZVBheSI6eyJzdGF0dXMiOiJtb2NrIiwiY291bnRyeUNvZGUiOiJVUyIsImN1cnJlbmN5Q29kZSI6IlVTRCIsIm1lcmNoYW50SWRlbnRpZmllciI6Im1lcmNoYW50LmNvbS5icmFpbnRyZWVwYXltZW50cy5zYW5kYm94LkJyYWludHJlZS1EZW1vIiwic3VwcG9ydGVkTmV0d29ya3MiOlsidmlzYSIsIm1hc3RlcmNhcmQiLCJhbWV4Il19fQ==");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Kevin in onActivityResult, requestCode:" + requestCode + ". resultCode is:" + resultCode);
        if (requestCode == 100) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                Intent intent = new Intent(getActivity(), SubscribeActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE, paymentMethodNonce);
                startActivityForResult(intent, 200);
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    public void postNonceToServer(String nonce) {
        Log.d(TAG, "kevin in postNonceToServer, the nonce is: " + nonce);

//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("payment_method_nonce", nonce);
//        client.post("http://your-server/payment-methods", params,
//                new AsyncHttpResponseHandler() {
//                    // Your implementation here
//                }
//        );
    }
}
