package com.receiptofi.checkout;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.fragments.FilterListFragment;
import com.receiptofi.checkout.fragments.ReceiptDetailFragment;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.Constants.ReceiptFilter;
import com.receiptofi.checkout.utils.db.ReceiptUtils;

import java.util.Date;

/**
 * Created by PT on 3/28/15.
 */
public class FilterListActivity extends Activity implements FilterListFragment.OnReceiptSelectedListener {

    private static final String TAG = FilterListActivity.class.getSimpleName();

    private int groupIndex = -1;
    private int childIndex = -1;

    private FilterListFragment filterListFragment = null;
    private ReceiptFilter receiptFilter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "executing onCreate");
        setContentView(R.layout.filter_list_page);

        /** Setup back up button with its own icon. */
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        if (upId > 0) {
            ImageView up = (ImageView) findViewById(upId);
            up.setImageDrawable(new IconDrawable(this, Iconify.IconValue.fa_angle_left)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Run query to fetch data
        if (getIntent().hasExtra(Constants.INTENT_EXTRA_FILTER_TYPE)) {
            String filterType = getIntent().getStringExtra(Constants.INTENT_EXTRA_FILTER_TYPE);
            if (ReceiptFilter.FILTER_BY_BIZ_AND_MONTH.getValue().equalsIgnoreCase(filterType)) {
                receiptFilter = ReceiptFilter.FILTER_BY_BIZ_AND_MONTH;
                new FilterDataTask().execute(ReceiptFilter.FILTER_BY_BIZ_AND_MONTH.getValue(), getIntent().getStringExtra(Constants.INTENT_EXTRA_BIZ_NAME));
                addFragments(savedInstanceState);
            }
        } else if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            receiptFilter = ReceiptFilter.FILTER_BY_KEYWORD;
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "executing onNewIntent");
        // set filter correctly
        receiptFilter = ReceiptFilter.FILTER_BY_KEYWORD;
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "executing handleIntent");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // use the query to search your data somehow
            new FilterDataTask().execute(ReceiptFilter.FILTER_BY_KEYWORD.getValue(), intent.getStringExtra(SearchManager.QUERY));
            if (filterListFragment == null) {
                addFragments(null);
            }
            if (!AppUtils.isTablet(this)) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, filterListFragment);

                // Commit the transaction
                transaction.commit();
            }
        }
    }

    private void addFragments(Bundle savedInstanceState) {
        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (null != savedInstanceState) {
                return;
            }

            // Create an instance of FilterListFragment
            filterListFragment = new FilterListFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            filterListFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, filterListFragment).commit();
        } else {
            filterListFragment = (FilterListFragment) getFragmentManager().findFragmentById(R.id.flist_fragment);
        }
    }

    public void onReceiptSelected(int index, int position) {
        // The user selected the headline of an article from the HeadlinesFragment
        Log.d(TAG, "executing onReceiptSelected: index is: " + index + " position is: " + position);

        groupIndex = index;
        childIndex = position;
        // Capture the article fragment from the activity layout
        ReceiptDetailFragment receiptDetailFragment = (ReceiptDetailFragment)
                getFragmentManager().findFragmentById(R.id.fdetail_fragment);

        if (receiptDetailFragment != null) {
            Log.d(TAG, "detail fragment already instantiated");
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            receiptDetailFragment.updateReceiptDetailView(index, position, true);

        } else {
            Log.d(TAG, "Instantiating new detail fragment");
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ReceiptDetailFragment newFragment = new ReceiptDetailFragment();
            Bundle args = new Bundle();
            args.putBoolean(Constants.ARG_TYPE_FILTER, true);
            args.putInt(Constants.ARG_INDEX, index);
            args.putInt(Constants.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public boolean hideTotal() {
        return ReceiptFilter.FILTER_BY_KEYWORD == receiptFilter;
    }

    /*
     *  arg[0] -> Filtertype
     *  arg[1] -> searchQuery
     */
    private class FilterDataTask extends AsyncTask<String, Integer, ReceiptGroup> {

        @Override
        protected ReceiptGroup doInBackground(String... args) {
            ReceiptGroup receiptGroup = null;
            if (ReceiptFilter.FILTER_BY_BIZ_AND_MONTH.getValue().equals(args[0])) {
                Log.d(TAG, "!!!!! search query is: " + args[1]);
                receiptGroup = ReceiptUtils.filterByBizByMonth(args[1], new Date());
            } else if (ReceiptFilter.FILTER_BY_KEYWORD.getValue().equals(args[0])) {
                Log.d(TAG, "!!!!! search query is: " + args[1]);
                receiptGroup = ReceiptUtils.searchByKeyword(args[1]);
            }
            return receiptGroup;
        }

        @Override
        protected void onPostExecute(ReceiptGroup receiptGroup) {
            Log.d(TAG, "!!!!! query finished - sending notification to fragment ");
            filterListFragment.notifyDataChanged(receiptGroup);
        }
    }
}
