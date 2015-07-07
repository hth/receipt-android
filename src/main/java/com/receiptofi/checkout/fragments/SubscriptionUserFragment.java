package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public PlanModel pm;
    public String mFirstName;
    public String mLastName;

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
        //TODO: Due to the token retrieved from backedn is not work until now, kevin use below test token to verify UI process.
        String tempToken = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJkMjMyMTZiY2U4NTk5YTQxZjA3NDZmNzJkZTk4MDYxYTU3NjdkY2YyNGJkOGJiYzM1ODQwY2NjYmEwZTRmOTAyfGNyZWF0ZWRfYXQ9MjAxNS0wNy0wN1QwODozMTo1MC4wNzAwNTMyNTUrMDAwMFx1MDAyNm1lcmNoYW50X2lkPWRjcHNweTJicndkanIzcW5cdTAwMjZwdWJsaWNfa2V5PTl3d3J6cWszdnIzdDRuYzgiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL2RjcHNweTJicndkanIzcW4vY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwibWVyY2hhbnRBY2NvdW50SWQiOiJzdGNoMm5mZGZ3c3p5dHc1IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sImNvaW5iYXNlRW5hYmxlZCI6dHJ1ZSwiY29pbmJhc2UiOnsiY2xpZW50SWQiOiIxMWQyNzIyOWJhNThiNTZkN2UzYzAxYTA1MjdmNGQ1YjQ0NmQ0ZjY4NDgxN2NiNjIzZDI1NWI1NzNhZGRjNTliIiwibWVyY2hhbnRBY2NvdW50IjoiY29pbmJhc2UtZGV2ZWxvcG1lbnQtbWVyY2hhbnRAZ2V0YnJhaW50cmVlLmNvbSIsInNjb3BlcyI6ImF1dGhvcml6YXRpb25zOmJyYWludHJlZSB1c2VyIiwicmVkaXJlY3RVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbS9jb2luYmFzZS9vYXV0aC9yZWRpcmVjdC1sYW5kaW5nLmh0bWwiLCJlbnZpcm9ubWVudCI6Im1vY2sifSwibWVyY2hhbnRJZCI6ImRjcHNweTJicndkanIzcW4iLCJ2ZW5tbyI6Im9mZmxpbmUiLCJhcHBsZVBheSI6eyJzdGF0dXMiOiJtb2NrIiwiY291bnRyeUNvZGUiOiJVUyIsImN1cnJlbmN5Q29kZSI6IlVTRCIsIm1lcmNoYW50SWRlbnRpZmllciI6Im1lcmNoYW50LmNvbS5icmFpbnRyZWVwYXltZW50cy5zYW5kYm94LkJyYWludHJlZS1EZW1vIiwic3VwcG9ydGVkTmV0d29ya3MiOlsidmlzYSIsIm1hc3RlcmNhcmQiLCJhbWV4Il19fQ==";
        if (view.getId() == R.id.btn_subscribe) {
            if (TokenWrapper.getTokenModel() != null) {
                if (!TextUtils.isEmpty(TokenWrapper.getTokenModel().getToken())) {
                    Intent intent = new Intent(getActivity(), BraintreePaymentActivity.class);
                    String token = TokenWrapper.getTokenModel().getFirstName();
                    //TODO: Replace the temp token with real one.
//                    intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, token);
                    intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, tempToken);
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
                }
                getActivity().startActivityForResult(intent, 200);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }
}
