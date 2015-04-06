package com.receiptofi.checkout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.receiptofi.checkout.adapters.ExpenseTagListAdapter;
import com.receiptofi.checkout.fragments.ReceiptDetailFragment;
import com.receiptofi.checkout.fragments.ReceiptListFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;

import java.util.List;

/**
 * Created by PT on 1/3/15.
 */
public class ReceiptListActivity extends FragmentActivity implements ReceiptListFragment.OnReceiptSelectedListener {

    private static final String TAG = ReceiptListActivity.class.getSimpleName();

    private int groupIndex = -1;
    private int childIndex = -1;

    private CheckBox recheckBox;
    private ListView tagList;
    private EditText noteText;
    private ExpenseTagModel selectedTagModel;

    /** Called when the activity is first created. */
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
        if(groupIndex >-1 && childIndex >-1){
            new ReceiptDataTask().execute();
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

    private void initDrawerView(){
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.receipt_drawer_layout);
        recheckBox = (CheckBox)findViewById(R.id.receipt_action_recheck);
        tagList = (ListView)findViewById(R.id.receipt_action_expense_tag_list);
        noteText = (EditText)findViewById(R.id.receipt_action_note);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                ReceiptModel rModel = ReceiptListFragment.children.get(groupIndex).get(childIndex);

                boolean reCheck = recheckBox.isChecked();
                String tagId = null;
                if(selectedTagModel != null && !selectedTagModel.getId().equals(rModel.getExpenseTagId())) {
                    tagId = selectedTagModel.getId();
                }
                String notes = null;
                if(!(noteText.getText().toString()).equals(rModel.getNotes())){
                    notes = noteText.getText().toString();
                }
                Log.d(TAG, "\n RecheckBox is checked: " + reCheck
                            + "\n selected tag is: " + tagId
                            + "\n added notes are: " + notes);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void setDrawerView(final List<ExpenseTagModel> tagModelList){
        ReceiptModel rModel = ReceiptListFragment.children.get(groupIndex).get(childIndex);
        String tagId = rModel.getExpenseTagId();
        Log.d(TAG, "Current tag is: " + tagId);
        tagList.setAdapter(new ExpenseTagListAdapter(this, tagModelList, tagId));
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTagModel = tagModelList.get(position);
            }
        });
        if(!TextUtils.isEmpty(rModel.getNotes())){
            noteText.setText(rModel.getNotes());
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

    private class ReceiptDataTask extends AsyncTask< Void, Void, List<ExpenseTagModel>>{

        @Override
        protected List<ExpenseTagModel> doInBackground(Void... voids) {
            return ExpenseTagUtils.getAll();
        }

        @Override
        protected void onPostExecute(List<ExpenseTagModel> expenseTagModels) {
            setDrawerView(expenseTagModels);
        }
    }
}
