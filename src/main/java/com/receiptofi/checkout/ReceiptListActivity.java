package com.receiptofi.checkout;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.receiptofi.checkout.adapters.ExpenseTagListAdapter;
import com.receiptofi.checkout.fragments.ReceiptDetailFragment;
import com.receiptofi.checkout.fragments.ReceiptListFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.ConstantsJson;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.views.ToastBox;
import com.squareup.okhttp.Headers;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by PT on 1/3/15.
 */
public class ReceiptListActivity extends Activity implements ReceiptListFragment.OnReceiptSelectedListener {

    private static final String TAG = ReceiptListActivity.class.getSimpleName();

    private int groupIndex = -1;
    private int childIndex = -1;

    private CheckBox recheckBox;
    private ListView tagList;
    private EditText noteText;
    private ExpenseTagModel selectedTagModel;

    private SearchView searchView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_list_page);
        initDrawerView();

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
            getFragmentManager().beginTransaction().add(R.id.fragment_container, receiptListFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "executing onResume");
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        Log.d(TAG, "Done onResume!!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                DeviceService.getNewUpdates(this);
                return true;
            case R.id.menu_notofication:
                launchNotifications();
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

    public void onReceiptSelected(int index, int position) {
        // The user selected the headline of an article from the HeadlinesFragment
        Log.d(TAG, "executing onReceiptSelected: index is: " + index + " position is: " + position);
        if (index < 0 || position < 0) {
            return;
        }

        groupIndex = index;
        childIndex = position;
        // Capture the article fragment from the activity layout
        ReceiptDetailFragment receiptDetailFragment = (ReceiptDetailFragment)
                getFragmentManager().findFragmentById(R.id.rdetail_fragment);

        if (receiptDetailFragment != null) {
            Log.d(TAG, "detail fragment already instantiated");
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            receiptDetailFragment.updateReceiptDetailView(index, position, false);

        } else {
            Log.d(TAG, "Instantiating new detail fragment");
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ReceiptDetailFragment newFragment = new ReceiptDetailFragment();
            Bundle args = new Bundle();
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

    private void initDrawerView() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.receipt_drawer_layout);
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

                ReceiptModel rModel = ReceiptListFragment.children.get(groupIndex).get(childIndex);

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

                        ExternalCallWithOkHttp.doPost(ReceiptListActivity.this, postData, API.RECEIPT_ACTION, IncludeAuthentication.YES, new ResponseHandler() {
                            @Override
                            public void onSuccess(Headers headers, String body) {
                                DeviceService.onSuccess(headers, body);
                            }

                            @Override
                            public void onError(int statusCode, String error) {
                                Log.d(TAG, "Executing onDrawerClosed: onError: " + error);
                                ToastBox.makeText(ReceiptListActivity.this, JsonParseUtils.parseError(error), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onException(Exception exception) {
                                Log.d(TAG, "Executing onDrawerClosed: onException: " + exception.getMessage());
                                ToastBox.makeText(ReceiptListActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        Log.e(TAG, "Exception while adding data on drawer close: " + e.getMessage(), e);
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
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.receipt_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        final List<ExpenseTagModel> tagModelList = new LinkedList<>(expTagMap.values());
        ReceiptModel rModel = ReceiptListFragment.children.get(groupIndex).get(childIndex);

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
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.receipt_drawer_layout);
        drawerLayout.openDrawer(Gravity.END);
    }

    private void launchNotifications() {
        startActivity(new Intent(this, NotificationActivity.class));
    }

    private void launchSettings() {
        startActivity(new Intent(this, PreferencesTabActivity.class));
    }

    private void logout() {
        KeyValueUtils.updateValuesForKeyWithBlank(API.key.XR_AUTH);
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }

}
