package com.receiptofi.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.receiptofi.checkout.fragments.ReceiptDetailFragment;
import com.receiptofi.checkout.fragments.ReceiptListFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.utils.db.KeyValueUtils;

/**
 * Created by PT on 1/3/15.
 */
public class ReceiptListActivity extends FragmentActivity implements ReceiptListFragment.OnReceiptSelectedListener {

    private static final String TAG = ReceiptListActivity.class.getSimpleName();

    private int groupIndex = -1;
    private int childIndex = -1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_list_page);

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            ReceiptListFragment receiptListFragment = new ReceiptListFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            receiptListFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, receiptListFragment).commit();
        }

    }

    public void onReceiptSelected(int index, int position) {
        // The user selected the headline of an article from the HeadlinesFragment
        Log.d(TAG, "executing onReceiptSelected: index is: " + index + " position is: " + position);

        groupIndex = index;
        childIndex = position;
        // Capture the article fragment from the activity layout
        ReceiptDetailFragment receiptDetailFragment = (ReceiptDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.rdetail_fragment);

        if (receiptDetailFragment != null) {
            Log.d(TAG, "detail fragment already instantiated");
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            receiptDetailFragment.updateReceiptDetailView(index, position);

        } else {
            Log.d(TAG, "Instantiating new detail fragment");
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ReceiptDetailFragment newFragment = new ReceiptDetailFragment();
            Bundle args = new Bundle();
            args.putInt(ReceiptDetailFragment.ARG_INDEX, index);
            args.putInt(ReceiptDetailFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    public int getGroupIndex(){
        return groupIndex;
    }

    public int getChildIndex(){
        return childIndex;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_search:
                // TODO call search
                return true;
            case R.id.menu_refresh:
                // TODO call getUpdate
                return true;
            case R.id.menu_settings:
                launchSettings();
                return true;
            case R.id.menu_logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void logout() {
        KeyValueUtils.updateValuesForKeyWithBlank(API.key.XR_AUTH);
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }
}
