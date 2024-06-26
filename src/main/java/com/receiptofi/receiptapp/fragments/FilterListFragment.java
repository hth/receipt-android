package com.receiptofi.receiptapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.FilterListActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ReceiptListActivity;
import com.receiptofi.receiptapp.adapters.FilterListAdapter;
import com.receiptofi.receiptapp.adapters.ReceiptListAdapter;
import com.receiptofi.receiptapp.model.FilterGroupObservable;
import com.receiptofi.receiptapp.model.ReceiptGroup;
import com.receiptofi.receiptapp.model.ReceiptGroupHeader;
import com.receiptofi.receiptapp.model.ReceiptGroupObservable;
import com.receiptofi.receiptapp.model.ReceiptModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.views.PinnedHeaderExpandableListView;

import java.util.LinkedList;
import java.util.List;

/**
 * User: PT
 * Date: 3/28/15 7:11 AM
 */
public class FilterListFragment extends Fragment {

    private static final String TAG = FilterListFragment.class.getSimpleName();

    public static final int RECEIPT_MODEL_UPDATED = 0x2436;

    public static List<ReceiptGroupHeader> groups = new LinkedList<>();
    public static List<List<ReceiptModel>> children = new LinkedList<>();

    public static boolean hideTotal;

    private ExpandableListView explv;
    private OnReceiptSelectedListener mCallback;
    private SearchView searchView;
    private static FilterGroupObservable receiptGroupObservable = FilterGroupObservable.getInstance();
    private DataSetObserver receiptGroupObserver;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case RECEIPT_MODEL_UPDATED:
                    Log.d(TAG, "receiptGroupObserver onChanged RECEIPT_MODEL_UPDATED");
                    groups = FilterGroupObservable.getMonthlyReceiptGroup().getReceiptGroupHeaders();
                    children = FilterGroupObservable.getMonthlyReceiptGroup().getReceiptModels();
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    public FilterListFragment() {
        super();
        if (receiptGroupObservable != null) {
            groups = FilterGroupObservable.getMonthlyReceiptGroup().getReceiptGroupHeaders();
            children = FilterGroupObservable.getMonthlyReceiptGroup().getReceiptModels();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiptGroupObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        receiptGroupObservable.registerObserver(receiptGroupObserver);
        /** Remove selected receipt from list to avoid re-loading of the detail receipt when refresh is complete. */
        ((FilterListActivity) getActivity()).setGroupIndex(-1);
        ((FilterListActivity) getActivity()).setChildIndex(-1);

        /** When resume, refresh anyway to update list with any changes. */
        updateHandler.sendEmptyMessage(RECEIPT_MODEL_UPDATED);
    }

    @Override
    public void onPause() {
        super.onPause();
        receiptGroupObservable.unregisterObserver(receiptGroupObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "executing onCreateView");
        View rootView = inflater.inflate(R.layout.receipt_list_fragment, container, false);
        // We only add menu within Phone environment.
        if (!AppUtils.isTablet(getActivity())) {
            // Must call below method to make the fragment menu works. REALLY IMPORTANT.
            setHasOptionsMenu(true);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "executing onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_main, menu);
        setSearchConfig(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        explv = (PinnedHeaderExpandableListView) view.findViewById(R.id.exp_list_view);
        explv.setEmptyView(view.findViewById(R.id.empty_view));
        explv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        final FilterListAdapter adapter = new FilterListAdapter(getActivity());
        explv.setAdapter(adapter);
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);

                ((FilterListActivity) getActivity()).onReceiptSelected(groupPosition, childPosition);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnReceiptSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnReceiptSelectedListener");
        }
    }

    public void notifyDataChanged(ReceiptGroup receiptGroup) {
        groups = receiptGroup.getReceiptGroupHeaders();
        children = receiptGroup.getReceiptModels();
        hideTotal = ((FilterListActivity) getActivity()).hideTotal();
        ((FilterListAdapter) explv.getExpandableListAdapter()).notifyDataSetChanged();
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnReceiptSelectedListener {
        /**
         * Called by HeadlinesFragment when a list item is selected
         */
        void onReceiptSelected(int index, int position);
    }

    private SearchView setSearchConfig(Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setIconified(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        /**
         * Replace the default menu search image.
         */
        Drawable mDraw = new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageDrawable(mDraw);

        // Remove the default SearchView Icon
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        if (getActivity().getIntent().hasExtra(SearchManager.QUERY) && TextUtils.isEmpty(searchView.getQuery())) {
            searchView.setQuery(getActivity().getIntent().getStringExtra(SearchManager.QUERY), false);
        }

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setTextColor(Color.WHITE);
        searchAutoCompleteTextView.setHint("Search");
        searchAutoCompleteTextView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        return searchView;
    }
}
