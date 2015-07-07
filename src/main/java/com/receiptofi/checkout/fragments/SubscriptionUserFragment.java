package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscribeConfirmationActivity;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.wrapper.TokenWrapper;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;

/**
 * User: hitender
 * Date: 7/2/15 11:33 AM
 */
public class SubscriptionUserFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SubscriptionUserFragment.class.getSimpleName();

    private EditText firstName;
    private EditText lastName;
    private EditText postalCode;

    private TextView subscriptionTitle;
    private TextView planName;
    private TextView planDescription;
    private TextView planPrice;
    private ButtonRectangle btnSubscribe;
    private PlanModel pm;
    private String mFirstName;
    private String mLastName;

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
        btnSubscribe = (ButtonRectangle) rootView.findViewById(R.id.btn_subscribe);

        btnSubscribe.setOnClickListener(this);

        pm = getActivity().getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PLAN_MODEL);

        if (TokenWrapper.getTokenModel() != null) {
            if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getFirstName())) {
                mFirstName = TokenWrapper.getTokenModel().getFirstName();
                firstName.setText(mFirstName);
            }

            if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getLastName())) {
                mLastName = TokenWrapper.getTokenModel().getLastName();
                lastName.setText(mLastName);
            }

            if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getPostalCode())) {
                postalCode.setText(TokenWrapper.getTokenModel().getPostalCode());
            }
        }

        planName = (TextView) rootView.findViewById(R.id.subscription_plan_list_item_plan_name);
        planName.setText(pm.getName() + " - $" + pm.getPrice());

        planDescription = (TextView) rootView.findViewById(R.id.subscription_plan_list_item_plan_description);
        planDescription.setText(pm.getDescription());

        /** Must call below method to make the fragment menu works. */
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_subscribe) {
            Intent intent = new Intent(getActivity(), SubscribeConfirmationActivity.class);
            if (null != pm) {
                intent.putExtra(Constants.INTENT_EXTRA_PLAN_MODEL, pm);
            }

            if (!mFirstName.isEmpty() && !mLastName.isEmpty()) {
                intent.putExtra(Constants.INTENT_EXTRA_FIRST_NAME, mFirstName);
                intent.putExtra(Constants.INTENT_EXTRA_LAST_NAME, mLastName);
            }
            startActivityForResult(intent, 200);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
