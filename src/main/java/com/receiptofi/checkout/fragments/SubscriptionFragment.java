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
import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscriptionUserActivity;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.service.SubscriptionService;
import com.receiptofi.checkout.utils.AppUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 6/28/15 2:12 AM
 */
public class SubscriptionFragment extends Fragment {
    private static final String TAG = SubscriptionFragment.class.getSimpleName();

    private ListView plans;
    private List<PlanModel> planModels = new LinkedList<>();
    private PlanModel planModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscription, container, false);
        plans = (ListView) rootView.findViewById(R.id.plans);
        plans.setAdapter(new PlanListAdapter(getActivity()));
        plans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                planModel = planModels.get(position);
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
                holder.planDescription.setText(getItem(position).getDescription());
                holder.planPrice.setText(getItem(position).getPrice());
                return convertView;
            } catch (Exception e) {
                Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
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

//            PlanModel planModel = new PlanModel("A", "B", "C", "Plan Description", "E", "Plan Name", "G", "$0.10");
//            PlanModel planModel2 = new PlanModel("A", "B", "C", "Plan Description", "E", "Plan Name", "G", "$0.20");
//            List<PlanModel> planModels = new ArrayList<>();
//            planModels.add(planModel);
//            planModels.add(planModel2);

            return SubscriptionService.getPlanModels();
        }

        @Override
        protected void onPostExecute(List<PlanModel> plans) {
            planModels = plans;
            Log.d(TAG, "Completed querying, sending notification to fragment");
            showData();
        }
    }

    private void onPlanSelection(PlanModel planModel) {
        Intent intent = new Intent(getActivity(), SubscriptionUserActivity.class);
        intent.putExtras(planModel.getAsBundle());
        startActivity(intent);
    }
}
