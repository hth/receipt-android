package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;


public class SubscribeActivity extends Activity {
    String paymentMethodNonce = "";
    private static final String TAG = SubscribeActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String paymentMethodNonce = extras.getString(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
            if (paymentMethodNonce != null) {
                Log.d(TAG, "paymentMethodNonce is :" + paymentMethodNonce);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_subscribe, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSubscribePressed(View button) {
        Intent intent = new Intent(this, SubscribeConfirmationActivity.class);
//        intent.putExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE, paymentMethodNonce);
        startActivityForResult(intent, 200);
    }
}
