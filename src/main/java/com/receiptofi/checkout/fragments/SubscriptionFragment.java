package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscriptionUserActivity;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.service.SubscriptionService;

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

    public static final int PLAN_FETCH_SUCCESS = 0x2660;
    public static final int PLAN_FETCH_FAILURE = 0x2662;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case PLAN_FETCH_SUCCESS:
                    stopProgressToken();
                    showData();
                    break;
                case PLAN_FETCH_FAILURE:
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

        //TODO(hth) cache plans result for a short while
        if (SubscriptionService.getPlanModels().isEmpty()) {
            SubscriptionService.getPlans(getActivity());
            startProgressToken();
        } else {
            SubscriptionService.getPlans(getActivity());
            startProgressToken();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscription, container, false);
        plans = (ListView) rootView.findViewById(R.id.plans);
        plans.setAdapter(new PlanListAdapter(getActivity()));
        plans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                planModel = SubscriptionService.getPlanModels().get(position);
                onPlanSelection(planModel);
//
//
//                SubscriptionUserFragment fragment = (SubscriptionUserFragment) getFragmentManager().findFragmentById(R.layout.fragment_subscription_user);
//
//                SubscriptionUserFragment subscriptionUserFragment = new SubscriptionUserFragment();
//                subscriptionUserFragment.setArguments(planModel.getAsBundle());
//                subscriptionUserFragment.isVisible();
            }
        });

        new PlanTask().execute();
        return rootView;
    }

    private void showData() {
        if (!SubscriptionService.getPlanModels().isEmpty()) {
            ((PlanListAdapter) plans.getAdapter()).notifyDataSetChanged();
        }
    }

    public class PlanListAdapter extends ArrayAdapter<PlanModel> {
        private final String TAG = PlanListAdapter.class.getSimpleName();
        private final LayoutInflater inflater;

        public PlanListAdapter(Context context) {
            super(context, R.layout.subscription_plan_list_item, SubscriptionService.getPlanModels());
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return SubscriptionService.getPlanModels().size();
        }

        @Override
        public PlanModel getItem(int position) {
            return SubscriptionService.getPlanModels().get(position);
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
                holder.planDescription.setText(getItem(position).getDescription());
                holder.planPrice.setText(getItem(position).getPrice());
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
            return SubscriptionService.getPlanModels();
        }

        @Override
        protected void onPostExecute(List<PlanModel> plans) {
            Log.d(TAG, "Completed querying, sending notification to fragment");
            showData();
        }
    }

    private void onPlanSelection(PlanModel planModel) {
        Intent intent = new Intent(getActivity(), SubscriptionUserActivity.class);
        intent.putExtras(planModel.getAsBundle());
        startActivity(intent);
    }

    private void startProgressToken() {
        progressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        progressToast.setText("Fetching available plans.");
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
