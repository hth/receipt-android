package com.receiptofi.checkout.fragments;

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
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscriptionUserActivity;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.wrapper.PlanWrapper;
import com.receiptofi.checkout.model.wrapper.TokenWrapper;
import com.receiptofi.checkout.service.SubscriptionService;
import com.receiptofi.checkout.utils.Constants;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.List;

/**
 * User: hitender
 * Date: 6/28/15 2:12 AM
 */
public class SubscriptionFragment extends Fragment {
    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    private static final int CACHE_TOKEN_SECONDS = 59;

    private ListView plans;
    private PlanModel planModel;
    private SuperActivityToast progressToast;

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
                    Log.d(TAG, "Token received=" + what);
                    stopProgressToken();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showData();
                        }
                    });
                    break;
                case TOKEN_FAILURE:
                    Log.d(TAG, "Token received=" + what);
                    stopProgressToken();
                    break;
                case PLAN_FETCH_SUCCESS:
                    Log.d(TAG, "Plans fetched=" + what);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showData();
                        }
                    });
                    break;
                case PLAN_FETCH_FAILURE:
                    Log.d(TAG, "Plans fetched=" + what);
                    stopProgressToken();
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
        boolean yes = fetchToken();

        //TODO(hth) may be remove token cache and instead fetch every time user comes to this screen
        if (yes && PlanWrapper.getPlanModels().isEmpty()) {
            Log.d(TAG, "Cache containing Plans is empty and token is stale, fetching fresh");
            SubscriptionService.getToken(getActivity());
            SubscriptionService.getPlans(getActivity());
            startProgressToken("Fetching available plans.");

        } else if (yes) {
            Log.d(TAG, "Cache containing Plans is empty and token is stale, fetching fresh");
            SubscriptionService.getToken(getActivity());
            startProgressToken("Refreshing plans.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_subscription, container, false);
        plans = (ListView) rootView.findViewById(R.id.plans);
        plans.setAdapter(new PlanListAdapter(getActivity()));
        plans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                planModel = PlanWrapper.getPlanModels().get(position);
                if (null != TokenWrapper.getTokenModel()) {
                    onPlanSelection(planModel);
                } else {
                    ((MainMaterialDrawerActivity)getActivity()).showErrorMsg("Wait a moment, token is not ready");
                }
            }
        });

        new PlanTask().execute();
        return rootView;
    }

    private boolean fetchToken() {
        return null == TokenWrapper.getLastUpdated() ||
                Seconds.secondsBetween(TokenWrapper.getLastUpdated(), DateTime.now()).getSeconds() > CACHE_TOKEN_SECONDS;
    }

    private void showData() {
        if (!PlanWrapper.getPlanModels().isEmpty()) {
            ((PlanListAdapter) plans.getAdapter()).notifyDataSetChanged();
        }
    }

    public class PlanListAdapter extends ArrayAdapter<PlanModel> {
        private final String TAG = PlanListAdapter.class.getSimpleName();
        private final LayoutInflater inflater;

        public PlanListAdapter(Context context) {
            super(context, R.layout.subscription_plan_list_item, PlanWrapper.getPlanModels());
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
                PlanModel pm = getItem(position);
                if (null != pm) {
                    if (null != TokenWrapper.getTokenModel() && !TextUtils.isEmpty(TokenWrapper.getTokenModel().getPlanId())) {
                        if (pm.getId().equals(TokenWrapper.getTokenModel().getPlanId())) {
                            //TODO(hth)(kevin) why is this log printed 6 times when plan table is refreshed
                            Log.d(TAG, "pm.getId()=" + pm.getId() + ", TokenWrapper.getTokenModel().getPlanId()=" + TokenWrapper.getTokenModel().getPlanId());
                            convertView.setBackgroundColor(Color.LTGRAY);
                        }
                    }
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
        Intent intent = new Intent(getActivity(), SubscriptionUserActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PLAN_MODEL, planModel);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void startProgressToken(String message) {
        progressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        progressToast.setText(message);
        progressToast.setIndeterminate(true);
        progressToast.setProgressIndeterminate(true);
        progressToast.show();
    }

    public void stopProgressToken() {
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
