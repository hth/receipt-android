package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.fragments.SubscriptionUserFragment;

/**
 * User: hitender
 * Date: 07/04/15 1:57 AM
 */
public class SubscriptionUserActivity extends Activity {

    private static final String TAG = SubscriptionUserActivity.class.getSimpleName();
    private SearchView searchView;
    public SubscriptionUserFragment subscriptionUserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_user);

        /** Setup back up button with its own icon. */
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        if (upId > 0) {
            ImageView up = (ImageView) findViewById(upId);
            up.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_chevron_left)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
            subscriptionUserFragment = new SubscriptionUserFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            subscriptionUserFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.fragment_container, subscriptionUserFragment).commit();
        }
        setTitle(getResources().getString(R.string.purchase_title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscription_user, menu);
        setSearchConfig(menu);
        return true;
    }

    private void setSearchConfig(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        /**
         * Replace the default menu search image.
         */
        Drawable mDraw = new IconDrawable(this, FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageDrawable(mDraw);

        /**
         * Below if is designed for FilterListActivity.
         * Because this fragment can be used by both normal ReceiptList Activity and FilterList Activity
         */
        if (null != getIntent() && !TextUtils.isEmpty(getIntent().getStringExtra(SearchManager.QUERY))) {
            if (getIntent().hasExtra(SearchManager.QUERY) && TextUtils.isEmpty(searchView.getQuery())) {
                searchView.setIconified(false);
                searchView.setQuery(getIntent().getStringExtra(SearchManager.QUERY), false);
            }
            // Remove the default SearchView Icon
            int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
            ImageView magImage = (ImageView) searchView.findViewById(magId);
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setTextColor(Color.WHITE);
        searchAutoCompleteTextView.setHint("Search");
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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

}
