package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.receiptofi.checkout.R;

/**
 * User: hitender
 * Date: 7/2/15 11:33 AM
 */
public class SubscriptionUserFragment extends Fragment {
    private static final String TAG = SubscriptionUserFragment.class.getSimpleName();

    private EditText firstName;
    private EditText lastName;
    private EditText postalCode;

    private TextView subscriptionTitle;
    private TextView planName;
    private TextView planDescription;
    private TextView planPrice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscription_user, container, false);
        firstName = (EditText) rootView.findViewById(R.id.subscription_user_first_name);
        lastName = (EditText) rootView.findViewById(R.id.subscription_user_last_name);
        postalCode = (EditText) rootView.findViewById(R.id.subscription_user_postal_code);

        subscriptionTitle = (TextView) rootView.findViewById(R.id.subscription_title_id);
        subscriptionTitle.setText(getResources().getString(R.string.subscription_status_subscribe));

        LinearLayout subscriptionPlanLinearLayout = (LinearLayout) rootView.findViewById(R.id.subscription_user);
        View child = inflater.inflate(R.layout.subscription_plan_list_item, null);
        subscriptionPlanLinearLayout.addView(child);

        planName = (TextView) child.findViewById(R.id.subscription_plan_list_item_plan_name);
        planName.setText("hello");

        planDescription = (TextView) child.findViewById(R.id.subscription_plan_list_item_plan_description);
        planDescription.setText("Description");

        planPrice = (TextView) child.findViewById(R.id.subscription_plan_list_item_plan_price);
        planPrice.setText("$10");


//
//        plans.setAdapter(new PlanListAdapter(getActivity()));
//
//        new PlanTask().execute();
        return rootView;
    }
}
