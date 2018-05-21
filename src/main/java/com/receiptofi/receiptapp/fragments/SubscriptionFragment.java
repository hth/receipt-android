package com.receiptofi.receiptapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.SubscriptionUserActivity;
import com.receiptofi.receiptapp.model.PlanModel;
import com.receiptofi.receiptapp.model.wrapper.PlanWrapper;
import com.receiptofi.receiptapp.model.wrapper.TokenWrapper;
import com.receiptofi.receiptapp.service.SubscriptionService;
import com.receiptofi.receiptapp.utils.Constants;

import java.util.List;

/**
 * User: hitender
 * Date: 6/28/15 2:12 AM
 */
public class SubscriptionFragment extends Fragment {
    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    private ListView plans;
    private PlanModel planModel;
    private SuperActivityToast progressToast;
    private PlanListAdapter planAdapter;

    public static final int TOKEN_SUCCESS = 0x2880;
    public static final int TOKEN_FAILURE = 0x2882;
    public static final int PLAN_FETCH_SUCCESS = 0x2884;
    public static final int PLAN_FETCH_FAILURE = 0x2886;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case TOKEN_SUCCESS:
                    Log.d(TAG, "Token success received=" + what);
                    showData();
                    stopToast();
                    break;
                case TOKEN_FAILURE:
                    Log.d(TAG, "Token failure received=" + what);
                    stopToast();
                    break;
                case PLAN_FETCH_SUCCESS:
                    Log.d(TAG, "Plans success fetched=" + what);
                    showData();
                    break;
                case PLAN_FETCH_FAILURE:
                    Log.d(TAG, "Plans failure fetched=" + what);
                    stopToast();
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        populateData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopToast();
    }

    private void populateData() {
        if (PlanWrapper.refresh()) {
            Log.d(TAG, "Cache containing Plans is empty and token is stale, fetching fresh");
            SubscriptionService.getToken(getActivity());
            SubscriptionService.getPlans(getActivity());
            showToast("Fetching available plans.");

        } else {
            Log.d(TAG, "Cache containing Plans is empty and token is stale, fetching fresh");
            SubscriptionService.getToken(getActivity());
            showToast("Refreshing plans.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_subscription, container, false);
        plans = (ListView) rootView.findViewById(R.id.plans);
        planAdapter = new PlanListAdapter(getActivity());
        plans.setAdapter(planAdapter);
        plans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                planModel = PlanWrapper.getPlanModels().get(position);
                if (null != TokenWrapper.getTokenModel()) {
                    onPlanSelection(planModel);
                } else {
                    ((MainMaterialDrawerActivity) getActivity()).showToastMsg("Wait a moment, token is not ready",
                            SuperToast.Background.BLUE,
                            SuperToast.Duration.SHORT);
                }
            }
        });

        new PlanTask().execute();
        return rootView;
    }

    private void showData() {
        if (!PlanWrapper.getPlanModels().isEmpty()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    planAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public class PlanListAdapter extends ArrayAdapter<PlanModel> {
        private final String TAG = PlanListAdapter.class.getSimpleName();
        private final LayoutInflater inflater;

        public PlanListAdapter(Context context) {
            super(context, R.layout.list_item_subscription_plan, PlanWrapper.getPlanModels());
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return PlanWrapper.getPlanModels().size();
        }

        @Override
        public PlanModel getItem(int position) {
            return PlanWrapper.getPlanModels().get(position);
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
                    convertView = inflater.inflate(R.layout.list_item_subscription_plan, parent, false);
                    ImageView nextAction = (ImageView) convertView.findViewById(R.id.iv_next_arrow);
                    nextAction.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_chevron_right)
                            .colorRes(R.color.app_theme_bg)
                            .sizePx(44));

                    holder = new ViewHolder();
                    holder.planName = (TextView) convertView.findViewById(R.id.tv_plan_name);
                    holder.planDescription = (TextView) convertView.findViewById(R.id.tv_plan_description);
                    holder.planPrice = (TextView) convertView.findViewById(R.id.tv_plan_price);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                PlanModel pm = getItem(position);
                if (null != pm) {
                    if (null != TokenWrapper.getTokenModel() && !TextUtils.isEmpty(TokenWrapper.getTokenModel().getPlanId())) {
                        if (pm.getId().equals(TokenWrapper.getTokenModel().getPlanId())) {
                            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
                            if (viewHolder != null && viewHolder.id != null && viewHolder.id.equals(TokenWrapper.getTokenModel().getPlanId())) {
                                Log.d(TAG, "pm.getId()=" + pm.getId() + ", TokenWrapper.getTokenModel().getPlanId()=" + TokenWrapper.getTokenModel().getPlanId());
                                convertView.setBackgroundColor(Color.LTGRAY);
                            }
                        }
                    }
                    holder.id = pm.getId();
                    holder.planName.setText(pm.getName());
                    holder.planDescription.setText(pm.getDescription());
                    holder.planPrice.setText("$" + String.valueOf(pm.getPrice()));
                }
                return convertView;
            } catch (Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
            }
            return null;
        }

        private class ViewHolder {
            String id;
            TextView planName;
            TextView planDescription;
            TextView planPrice;
        }
    }

    private class PlanTask extends AsyncTask<Void, Void, List<PlanModel>> {

        @Override
        protected List<PlanModel> doInBackground(Void... args) {
            return PlanWrapper.getPlanModels();
        }

        @Override
        protected void onPostExecute(List<PlanModel> plans) {
            Log.d(TAG, "Completed querying, sending notification to fragment");
            showData();
        }
    }

    private void onPlanSelection(PlanModel planModel) {
        /** Stop toast message anyways, since user has moved past the list. */
        stopToast();
        Intent intent = new Intent(getActivity(), SubscriptionUserActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PLAN_MODEL, planModel);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showToast(String message) {
        /** Required when user is coming from Refresh home page to subscription fragment. */
        SuperActivityToast.cancelAllSuperActivityToasts();

        progressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        progressToast.setText(message);
        progressToast.setIndeterminate(true);
        progressToast.setProgressIndeterminate(true);
        progressToast.show();
    }

    public void stopToast() {
        if (null != progressToast && progressToast.isShowing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressToast.dismiss();
                }
            });
        }
    }
}
