package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.adapters.ExpenseTagListAdapter;
import com.receiptofi.receiptapp.fragments.FilterListFragment;
import com.receiptofi.receiptapp.fragments.ReceiptDetailFragment;
import com.receiptofi.receiptapp.fragments.ReceiptDetailImageForTabletDialogFragment;
import com.receiptofi.receiptapp.fragments.ReceiptDetailImageFragment;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.ExpenseTagModel;
import com.receiptofi.receiptapp.model.FilterGroupObservable;
import com.receiptofi.receiptapp.model.ReceiptGroup;
import com.receiptofi.receiptapp.model.ReceiptModel;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.service.DeviceService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants;
import com.receiptofi.receiptapp.utils.Constants.ReceiptFilter;
import com.receiptofi.receiptapp.utils.ConstantsJson;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.db.ExpenseTagUtils;
import com.receiptofi.receiptapp.utils.db.ReceiptUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

/**
 * User: PT
 * Date: 3/28/15 7:58 AM
 */
public class FilterListActivity extends Activity implements FilterListFragment.OnReceiptSelectedListener {

    private static final String TAG = FilterListActivity.class.getSimpleName();

    private int groupIndex = -1;
    private int childIndex = -1;

    private FilterListFragment filterListFragment = null;
    private ReceiptFilter receiptFilter;
    public DrawerLayout drawerLayout;
    private CheckBox recheckBox;
    private ListView tagList;
    private EditText noteText;
    private ExpenseTagModel selectedTagModel;

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
            up.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_chevron_left)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        initDrawerView();

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
        if (groupIndex > -1 && childIndex > -1) {
            setDrawerView();
        }
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public int getChildIndex() {
        return childIndex;
    }

    public void setGroupIndex(int groupPosition) {
        groupIndex = groupPosition;
    }

    public void setChildIndex(int childPosition) {
        childIndex = childPosition;
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
                Log.d(TAG, "Search query is: " + args[1]);
                receiptGroup = ReceiptUtils.filterByBizByMonth(args[1], new Date());
            } else if (ReceiptFilter.FILTER_BY_KEYWORD.getValue().equals(args[0])) {
                Log.d(TAG, "Search query is: " + args[1]);
                receiptGroup = ReceiptUtils.searchByKeyword(args[1]);
            }
            return receiptGroup;
        }

        @Override
        protected void onPostExecute(ReceiptGroup receiptGroup) {
            Log.d(TAG, "Completed querying, sending notification to fragment");
            filterListFragment.notifyDataChanged(receiptGroup);
        }
    }

    // Kevin add
    private void initDrawerView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.receipt_drawer_layout);
        // set the drawer to remain closed until a receipt is selected
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        recheckBox = (CheckBox) findViewById(R.id.receipt_action_recheck);
        tagList = (ListView) findViewById(R.id.receipt_action_expense_tag_list);
        noteText = (EditText) findViewById(R.id.receipt_action_note);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (groupIndex >= 0 && childIndex >= 0) {
                    ReceiptModel rModel = FilterListFragment.children.get(groupIndex).get(childIndex);

                    // Assign values only if fields have been changed
                    boolean reCheck = recheckBox.isChecked();
                    String tagId = null;
                    if (selectedTagModel != null && !selectedTagModel.getId().equals(rModel.getExpenseTagId())) {
                        tagId = selectedTagModel.getId();
                    }
                    String notes = null;
                    if (!TextUtils.isEmpty(noteText.getText().toString()) && !(noteText.getText().toString()).equals(rModel.getNotes())) {
                        notes = noteText.getText().toString();
                    }
                    Log.d(TAG, "reCheck: " + reCheck + " tagId: " + tagId + " notes: " + notes);

                    if (reCheck || null != tagId || !TextUtils.isEmpty(notes)) {
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put(ConstantsJson.EXPENSE_TAG_ID, tagId);
                            postData.put(ConstantsJson.NOTES, notes);
                            postData.put(ConstantsJson.RECHECK, reCheck ? "RECHECK" : "");
                            postData.put(ConstantsJson.RECEIPT_ID, rModel.getId());

                            ExternalCallWithOkHttp.doPost(FilterListActivity.this, postData, API.RECEIPT_ACTION, IncludeAuthentication.YES, new ResponseHandler() {
                                @Override
                                public void onSuccess(Headers headers, String body) {
                                    Log.d(TAG, "Executing onDrawerClosed: success: ");
                                    if (recheckBox.isChecked()) {
                                        //TODO
                                    }
                                    DeviceService.onSuccess(headers, body);

                                    if (!TextUtils.isEmpty(FilterGroupObservable.getKeyWord())) {
                                        ReceiptUtils.searchByKeyword(FilterGroupObservable.getKeyWord());
                                    }
                                }

                                @Override
                                public void onError(int statusCode, String error) {
                                    Log.d(TAG, "Executing onDrawerClosed: onError: " + error);
                                    showToast(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.MEDIUM, SuperToast.Background.RED);
                                }

                                @Override
                                public void onException(Exception exception) {
                                    Log.d(TAG, "Executing onDrawerClosed: onException: " + exception.getMessage());
                                    showToast(exception.getMessage(), SuperToast.Duration.MEDIUM, SuperToast.Background.RED);
                                }
                            });

                        } catch (JSONException e) {
                            Log.e(TAG, "Exception while adding data on drawer close: " + e.getMessage(), e);
                        }
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void setDrawerView() {
        // unlock the drawer
//        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.receipt_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        final List<ExpenseTagModel> tagModelList = new LinkedList<>(expTagMap.values());
        ReceiptModel rModel = FilterListFragment.children.get(groupIndex).get(childIndex);

        recheckBox.setChecked(false);
        String tagId = rModel.getExpenseTagId();
        Log.d(TAG, "Current tag is: " + tagId);
        tagList.setAdapter(new ExpenseTagListAdapter(this, tagModelList, tagId));
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTagModel = tagModelList.get(position);
            }
        });
        if (!TextUtils.isEmpty(rModel.getNotes())) {
            noteText.setText(rModel.getNotes());
        } else {
            noteText.setText("");
        }
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void closeDrawer() {
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout = (DrawerLayout) findViewById(R.id.receipt_drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        }
    }

    public boolean isDrawerOpened() {
        if (drawerLayout != null) {
            return drawerLayout.isDrawerOpen(GravityCompat.END);
        }
        return false;
    }

    /**
     * This function is important cannot be deleted.
     * We use this function to handle th Drawer menu frame click function to avoid the behind button be clicked.
     *
     * @param view
     */
    public void handleEmpty(View view) {
        Log.d(TAG, "handleEmpty View Click function");
    }

    public void showReceiptDetailImageFragment(String url) {
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        if (AppUtils.isTablet(this)) {
            // Handle Table environment
            FragmentManager fm = getFragmentManager();
            ReceiptDetailImageForTabletDialogFragment detailImage = new ReceiptDetailImageForTabletDialogFragment();
            Bundle args_tablet = new Bundle();
            args_tablet.putString(Constants.ARG_IMAGE_URL, url);
            detailImage.setArguments(args_tablet);
            detailImage.show(fm, "fragment_detail_image");

        } else {
            // Create fragment and give it an argument for the selected article
            ReceiptDetailImageFragment newFragment = new ReceiptDetailImageFragment();
            Bundle args = new Bundle();
            args.putString(Constants.ARG_IMAGE_URL, url);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            // Handle normal phone environment
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }
    }

    private void showToast(final String msg, final int length, final int color) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperToast superToast = new SuperToast(FilterListActivity.this);
                superToast.setText(msg);
                superToast.setDuration(length);
                superToast.setBackground(color);
                superToast.setTextColor(Color.WHITE);
                superToast.setAnimations(SuperToast.Animations.FLYIN);
                superToast.setGravity(Gravity.TOP, 0, 20);
                superToast.show();
            }
        });
    }
}
