package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.gc.materialdesign.views.ButtonRectangle;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscribeConfirmationActivity;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.wrapper.TokenWrapper;
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
        pm = getActivity().getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PLAN_MODEL);
        if (null != pm) {
            subscriptionTitle = (TextView) rootView.findViewById(R.id.subscription_title_id);
            if (pm.getId().equals(TokenWrapper.getTokenModel().getPlanId())) {
                subscriptionTitle.setText(getResources().getString(R.string.subscription_status_unSubscribe));
            } else {
                subscriptionTitle.setText(getResources().getString(R.string.subscription_status_subscribe));
            }

            LinearLayout subscriptionPlanLinearLayout = (LinearLayout) rootView.findViewById(R.id.subscription_user);
            View childPlan = inflater.inflate(R.layout.subscription_plan_list_item, null);
            subscriptionPlanLinearLayout.addView(childPlan, 1);

            planName = (TextView) childPlan.findViewById(R.id.subscription_plan_list_item_plan_name);
            planName.setText(pm.getName());

            planDescription = (TextView) childPlan.findViewById(R.id.subscription_plan_list_item_plan_description);
            planDescription.setText(pm.getDescription());

            planPrice = (TextView) childPlan.findViewById(R.id.subscription_plan_list_item_plan_price);
            planPrice.setText("$" + String.valueOf(pm.getPrice()));

            firstName = (EditText) rootView.findViewById(R.id.subscription_user_first_name);
            lastName = (EditText) rootView.findViewById(R.id.subscription_user_last_name);
            postalCode = (EditText) rootView.findViewById(R.id.subscription_user_postal_code);

            if (null != TokenWrapper.getTokenModel() && TokenWrapper.getTokenModel().isHasCustomerInfo()) {
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

            View childSubmission = inflater.inflate(R.layout.subscription_submission, null);
            subscriptionPlanLinearLayout.addView(childSubmission);

            btnSubscribe = (ButtonRectangle) rootView.findViewById(R.id.btn_subscribe);
            if (pm.getId().equals(TokenWrapper.getTokenModel().getPlanId())) {
                btnSubscribe.setText("UN-SUBSCRIBE");
            } else {
                btnSubscribe.setText("SUBSCRIBE");
            }
            btnSubscribe.setOnClickListener(this);
        } else {
            Log.e(TAG, "PM is null");
        }

        /** Must call below method to make the fragment menu works. */
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_subscribe) {
            //TODO(hth) add un subscribe option
            if (TokenWrapper.getTokenModel() != null) {
                if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getToken())) {
                    Intent intent = new Intent(getActivity(), BraintreePaymentActivity.class);
                    intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, TokenWrapper.getTokenModel().getToken());
                    startActivityForResult(intent, 100);
                }
            } else {
                SuperToast.create(getActivity(), "No any valid token!", SuperToast.Duration.LONG,
                        Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult, requestCode:" + requestCode + ". resultCode is:" + resultCode);
        if (requestCode == 100) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                Intent intent = new Intent(getActivity(), SubscribeConfirmationActivity.class);
                intent.putExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE, paymentMethodNonce);
                if (null != pm) {
                    intent.putExtra(Constants.INTENT_EXTRA_PLAN_MODEL, pm);
                }

                if (!mFirstName.isEmpty() && !mLastName.isEmpty()) {
                    intent.putExtra(Constants.INTENT_EXTRA_FIRST_NAME, mFirstName);
                    intent.putExtra(Constants.INTENT_EXTRA_LAST_NAME, mLastName);
                    intent.putExtra(Constants.INTENT_EXTRA_POSTAL_CODE, postalCode.getText());
                }
                getActivity().startActivityForResult(intent, 200);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }
}
