package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.TransactionDetail;
import com.receiptofi.checkout.utils.Constants;

public class SubscribeConfirmationActivity extends Activity {
    private static final String TAG = SubscribeConfirmationActivity.class.getSimpleName();

    public TextView tvMessage;
    private String type;
    private PlanModel pm;
    private String firstName;
    private String lastName;
    private String postalCode;

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

        type = getIntent().getStringExtra(Constants.INTENT_EXTRA_TRANSACTION_TYPE);
        pm = getIntent().getParcelableExtra(Constants.INTENT_EXTRA_PLAN_MODEL);
        firstName = getIntent().getStringExtra(Constants.INTENT_EXTRA_FIRST_NAME);
        lastName = getIntent().getStringExtra(Constants.INTENT_EXTRA_LAST_NAME);
        postalCode = getIntent().getStringExtra(Constants.INTENT_EXTRA_POSTAL_CODE);

        String message = "";
        if (!TextUtils.isEmpty(type) && pm != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            switch(TransactionDetail.TYPE.valueOf(type)) {
                case PAY:
                    message = firstName + " " + lastName + ", your card has been successfully charged for " + pm.getPrice() + " and you are enrolled for " + pm.getDescription() + ". Your last transactions and subscription has been cancelled. First of every month your card would be charged for " + pm.getBillingPlan() + ".";
                    break;
                case SUB:
                    message = firstName + " " + lastName + ", you have successfully unsubscribed from " + pm.getBillingPlan() + ".";
                    break;
                default:
            }

        }
        tvMessage.setText(message);
        setTitle(getResources().getString(R.string.purchase_confirmation_title));
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
        Intent intent = new Intent(this, MainMaterialDrawerActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
