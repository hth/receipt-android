package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braintreepayments.api.Braintree;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.SubscribeConfirmationActivity;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.wrapper.TokenWrapper;
import com.receiptofi.checkout.service.SubscriptionService;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.ConstantsJson;
import com.receiptofi.checkout.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: hitender
 * Date: 7/2/15 11:33 AM
 */
public class SubscriptionUserFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SubscriptionUserFragment.class.getSimpleName();

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPostalCode;

    private TextView subscriptionTitle;
    private TextView planName;
    private TextView planDescription;
    private TextView planPrice;
    private ButtonRectangle btnSubscribe;
    private SuperActivityToast progressToast;
    private PlanModel pm;
    private String firstName;
    private String lastName;
    private String postalCode;
    private Braintree braintree;
    private Boolean braintreeReady = false;

    public static final int SUBSCRIPTION_PAYMENT_SUCCESS = 0X2571;
    public static final int SUBSCRIPTION_PAYMENT_FAILED = 0X2572;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            stopProgressToken();
            final int what = msg.what;
            switch (what) {
                case SUBSCRIPTION_PAYMENT_SUCCESS:
                    Log.d(TAG, "payment successs");
                    Intent intent = new Intent(getActivity(), SubscribeConfirmationActivity.class);
                    if (null != pm) {
                        intent.putExtra(Constants.INTENT_EXTRA_PLAN_MODEL, pm);
                    }

                    if (!firstName.isEmpty() && !lastName.isEmpty()) {
                        intent.putExtra(Constants.INTENT_EXTRA_FIRST_NAME, firstName);
                        intent.putExtra(Constants.INTENT_EXTRA_LAST_NAME, lastName);
                        intent.putExtra(Constants.INTENT_EXTRA_POSTAL_CODE, postalCode);
                    }
                    getActivity().startActivityForResult(intent, 200);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case SUBSCRIPTION_PAYMENT_FAILED:
                    Log.d(TAG, "payment failed");
                    Dialog dialog = new Dialog(getActivity(), "Payment Failed", "Sorry your payment is failed!");
                    dialog.show();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
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

            etFirstName = (EditText) rootView.findViewById(R.id.subscription_user_first_name);
            etLastName = (EditText) rootView.findViewById(R.id.subscription_user_last_name);
            etPostalCode = (EditText) rootView.findViewById(R.id.subscription_user_postal_code);

            if (null != TokenWrapper.getTokenModel() && TokenWrapper.getTokenModel().isHasCustomerInfo()) {
                if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getFirstName())) {
                    firstName = TokenWrapper.getTokenModel().getFirstName();
                    etFirstName.setText(firstName);
                }

                if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getLastName())) {
                    lastName = TokenWrapper.getTokenModel().getLastName();
                    etLastName.setText(lastName);
                }

                if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getPostalCode())) {
                    postalCode = TokenWrapper.getTokenModel().getPostalCode();
                    etPostalCode.setText(TokenWrapper.getTokenModel().getPostalCode());
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

        if (TokenWrapper.getTokenModel() != null) {
            if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getToken())) {
                braintree = Braintree.restoreSavedInstanceState(getActivity(), savedInstanceState);
                if (braintree != null) {
                    // braintree is ready to use
                    braintreeReady = true;
                } else {
                    Braintree.setup(getActivity(), TokenWrapper.getTokenModel().getToken(), new Braintree.BraintreeSetupFinishedListener() {
                        @Override
                        public void onBraintreeSetupFinished(boolean setupSuccessful, Braintree braintree, String errorMessage, Exception exception) {
                            if (setupSuccessful) {
                                // braintree is now setup and available for use
                                SubscriptionUserFragment.this.braintree = braintree;
                                braintreeReady = true;
                            } else {
                                // Braintree could not be initialized, check errors and try again
                                // This is usually a result of a network connectivity error
                                braintreeReady = false;
                            }
                        }
                    });
                }
            }
        }
        /** Must call below method to make the fragment menu works. */
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (braintree != null) {
            braintree.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_subscribe) {
            firstName = etFirstName.getText().toString();
            lastName = etLastName.getText().toString();
            postalCode = etPostalCode.getText().toString();
            //TODO(hth) add un subscribe option
            if (validateFieldsString()) {
                if (TokenWrapper.getTokenModel() != null) {
                    if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getToken()) && braintreeReady) {
                        Intent intent = new Intent(getActivity(), BraintreePaymentActivity.class);
                        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, TokenWrapper.getTokenModel().getToken());
                        startActivityForResult(intent, 100);
                    }
                } else {
                    String message;
                    if (braintreeReady) {
                        message = "Payment Service is ready!";
                    } else {
                        message = "Payment Service is not ready!";
                    }
                    SuperToast.create(getActivity(), "No any valid token! " + message, SuperToast.Duration.LONG,
                            Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();
                }
            } else {
                SuperToast.create(getActivity(), "Need Valid First Name, Last Name and post code!", SuperToast.Duration.LONG,
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
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    public void postNonceToServer(String nonce) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(ConstantsJson.PLAN_ID, pm.getId());
            postData.put(ConstantsJson.FIRST_NAME, firstName);
            postData.put(ConstantsJson.LAST_NAME, lastName);
            postData.put(ConstantsJson.POSTAL, postalCode);
            postData.put(ConstantsJson.COMPANY, "Some Company");
            postData.put(ConstantsJson.PAYMENT_NONCE, nonce);
            SubscriptionService.doPayment(getActivity(), postData);
            this.startProgressToken("Payment is ongoing");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void startProgressToken(String message) {
        progressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        progressToast.setText(message);
        progressToast.setIndeterminate(true);
        progressToast.setProgressIndeterminate(true);
        progressToast.show();
    }

    private void stopProgressToken() {
        if (null != progressToast && progressToast.isShowing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressToast.dismiss();
                }
            });
        }
    }

    private boolean validateFieldsString() {
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(postalCode)) {
            return false;
        } else if (!Validation.isAlphaNumeric(firstName) || !Validation.isAlphaNumeric(lastName)) {
            return false;
        } else {
            return true;
        }
    }

}