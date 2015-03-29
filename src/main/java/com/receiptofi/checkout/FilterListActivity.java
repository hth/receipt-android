package com.receiptofi.checkout;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.receiptofi.checkout.fragments.ReceiptDetailFragment;
import com.receiptofi.checkout.fragments.FilterListFragment;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.db.ReceiptUtils;

import java.util.Date;

/**
 * Created by PT on 3/28/15.
 */
public class FilterListActivity  extends FragmentActivity implements FilterListFragment.OnReceiptSelectedListener {

    private static final String TAG = FilterListActivity.class.getSimpleName();

    private int groupIndex = -1;
    private int childIndex = -1;

    private ProgressDialog loader;
    private ReceiptGroup receiptGroup;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Run query to fetch data
        if(getIntent().hasExtra(Constants.INTENT_EXTRA_FILTER_TYPE)){
            showLoader("Please wait...");
            String filterType = getIntent().getStringExtra(Constants.INTENT_EXTRA_FILTER_TYPE);
            if(Constants.ReceiptFilter.FIlter_BY_BIZ_AND_MONTH.getValue().equalsIgnoreCase(filterType)){
                receiptGroup = ReceiptUtils.filterByBizByMonth(getIntent().getStringExtra(Constants.INTENT_EXTRA_BIZ_NAME), new Date());
            } else if(Constants.ReceiptFilter.FIlter_BY_KEYWORD.getValue().equalsIgnoreCase(filterType)){

            } else if(Constants.ReceiptFilter.FIlter_BY_KEYWORD_AND_DATE.getValue().equalsIgnoreCase(filterType)){

            }
            hideLoader();
        }
        setContentView(R.layout.filter_list_page);

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
            FilterListFragment filterListFragment = new FilterListFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            filterListFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, filterListFragment).commit();
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
            receiptDetailFragment.updateReceiptDetailView(index, position, receiptGroup.getReceiptModels().get(index).get(position));

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

    public ReceiptGroup getReceiptGroup(){
        return receiptGroup;
    }

    public int getGroupIndex(){
        return groupIndex;
    }

    public int getChildIndex(){
        return childIndex;
    }

    public void showLoader(String msg) {
        loader = new ProgressDialog(this);
        loader.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.setIndeterminate(true);
        loader.setMessage(msg);
        loader.show();
    }

    public void hideLoader() {
        if (loader != null) {
            loader.dismiss();
        }
        loader = null;
    }
}
