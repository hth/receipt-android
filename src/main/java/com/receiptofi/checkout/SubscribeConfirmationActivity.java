package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.wrapper.TokenWrapper;
import com.receiptofi.checkout.utils.Constants;


public class SubscribeConfirmationActivity extends Activity {

    public TextView tvMessage;
    private static final String TAG = SubscribeConfirmationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_confirmation);

        /** Setup back up button with its own icon. */
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        if (upId > 0) {
            ImageView up = (ImageView) findViewById(upId);
            up.setImageDrawable(new IconDrawable(this, Iconify.IconValue.fa_chevron_left)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvMessage = (TextView) findViewById(R.id.tv_message);
        PlanModel pm = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PLAN_MODEL);
        String firstName = getIntent().getStringExtra(Constants.INTENT_EXTRA_FIRST_NAME);
        String lastName = getIntent().getStringExtra(Constants.INTENT_EXTRA_LAST_NAME);
        String message = "";
        if (pm != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            message = firstName + " " + lastName + " your card has been successfully charged for " + pm.getPrice() + " and you are enrolled for " + pm.getDescription() + ". Your last transactions and subscription has been cancelled. First of every month your card would be charged for " + pm.getBillingPlan() + ".";
        }
        tvMessage.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_subscribe_confirmation, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onOkPressed(View button) {
        onBraintreeSubmit();
    }

    public void onBraintreeSubmit() {


    }


    public void postNonceToServer(String nonce) {
        Log.d(TAG, "kevin in postNonceToServer, the nonce is: " + nonce);

    }
}
