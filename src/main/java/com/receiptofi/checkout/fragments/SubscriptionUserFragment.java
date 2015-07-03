package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.subscription_user_fragment, container, false);
        firstName = (EditText) rootView.findViewById(R.id.subscription_user_first_name);
        lastName = (EditText) rootView.findViewById(R.id.subscription_user_last_name);
        postalCode = (EditText) rootView.findViewById(R.id.subscription_user_postal_code);


//
//        plans.setAdapter(new PlanListAdapter(getActivity()));
//
//        new PlanTask().execute();
        return rootView;
    }
}
